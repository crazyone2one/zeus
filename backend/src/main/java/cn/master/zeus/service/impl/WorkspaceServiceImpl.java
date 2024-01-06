package cn.master.zeus.service.impl;

import cn.master.zeus.common.constants.UserGroupConstants;
import cn.master.zeus.common.constants.UserGroupType;
import cn.master.zeus.common.exception.BusinessException;
import cn.master.zeus.dto.RelatedSource;
import cn.master.zeus.dto.WorkspaceMemberDTO;
import cn.master.zeus.dto.WorkspaceResource;
import cn.master.zeus.dto.request.BaseRequest;
import cn.master.zeus.dto.request.QueryMemberRequest;
import cn.master.zeus.entity.Project;
import cn.master.zeus.entity.SystemGroup;
import cn.master.zeus.entity.UserGroup;
import cn.master.zeus.entity.Workspace;
import cn.master.zeus.mapper.UserGroupMapper;
import cn.master.zeus.mapper.WorkspaceMapper;
import cn.master.zeus.service.ISystemGroupService;
import cn.master.zeus.service.ISystemUserService;
import cn.master.zeus.service.IWorkspaceService;
import cn.master.zeus.util.SessionUtils;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static cn.master.zeus.entity.table.ProjectTableDef.PROJECT;
import static cn.master.zeus.entity.table.SystemGroupTableDef.SYSTEM_GROUP;
import static cn.master.zeus.entity.table.UserGroupTableDef.USER_GROUP;
import static cn.master.zeus.entity.table.WorkspaceTableDef.WORKSPACE;

/**
 * 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class WorkspaceServiceImpl extends ServiceImpl<WorkspaceMapper, Workspace> implements IWorkspaceService {
    private final ISystemUserService systemUserService;
    private final UserGroupMapper userGroupMapper;
    private final ISystemGroupService systemGroupService;

    private static final String GLOBAL = "global";

    @Override
    public Page<Workspace> getWorkspacePage(BaseRequest request) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(WORKSPACE.NAME.like(request.getName())).orderBy(WORKSPACE.UPDATE_TIME.desc());
        val paginate = mapper.paginate(Page.of(request.getPageNumber(), request.getPageSize()), wrapper);
        val records = paginate.getRecords();
        if (CollectionUtils.isNotEmpty(records)) {
            records.forEach(item -> {
                QueryMemberRequest queryMemberRequest = new QueryMemberRequest();
                queryMemberRequest.setWorkspaceId(item.getId());
                val memberList = systemUserService.getMemberList(queryMemberRequest);
                item.setMemberSize(memberList.size());
            });
        }
        return paginate;
    }

    @Override
    public List<Workspace> getWorkspaceListByUserId(String userId) {
        val superUser = userGroupMapper.isSuperUser(userId);
        if (superUser) {
            return mapper.selectAll();
        }
        List<RelatedSource> relatedSource = getRelatedSource(userId);
        assert relatedSource != null;
        List<String> wsIds = relatedSource
                .stream()
                .map(RelatedSource::getWorkspaceId)
                .distinct()
                .toList();
        if (org.springframework.util.CollectionUtils.isEmpty(wsIds)) {
            return new ArrayList<>();
        }
        return queryChain().where(WORKSPACE.ID.in(wsIds)).list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Workspace addWorkspaceByAdmin(Workspace workspace) {
        val userId = SessionUtils.getUserId();
        checkWorkspace(workspace);
        workspace.setCreateUser(userId);
        mapper.insert(workspace);

        val userGroup = UserGroup.builder().userId(userId).sourceId(workspace.getId())
                .groupId(UserGroupConstants.WS_ADMIN).build();
        userGroupMapper.insert(userGroup);
        return workspace;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateWorkspaceByAdmin(Workspace workspace) {
        checkWorkspace(workspace);
        return mapper.update(workspace);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWorkspaceMember(WorkspaceMemberDTO memberDTO) {
        String workspaceId = memberDTO.getWorkspaceId();
        String userId = memberDTO.getId();
        // 已有角色
        List<SystemGroup> memberGroups = systemGroupService.getWorkspaceMemberGroups(workspaceId, userId);
        // 修改后的角色
        List<String> groups = memberDTO.getGroupIds();
        List<String> allGroupIds = new ArrayList<>(memberGroups.stream().map(SystemGroup::getId).toList());
        // 更新用户时添加了角色
        groups.forEach(g -> {
            if (checkSourceRole(workspaceId, userId, g) == 0) {
                UserGroup userGroup = UserGroup.builder().userId(userId).groupId(g).sourceId(workspaceId).build();
                userGroupMapper.insert(userGroup);
            }
        });
        allGroupIds.removeAll(groups);
        if (!allGroupIds.isEmpty()) {
            userGroupMapper.deleteByQuery(QueryChain.of(UserGroup.class)
                    .where(USER_GROUP.USER_ID.eq(userId).and(USER_GROUP.SOURCE_ID.eq(workspaceId)).and(USER_GROUP.GROUP_ID.in(allGroupIds))));
        }
    }

    @Override
    public WorkspaceResource listResource(String groupId, String type) {
        SystemGroup group = QueryChain.of(SystemGroup.class).where(SYSTEM_GROUP.ID.eq(groupId)).one();
        WorkspaceResource resource = new WorkspaceResource();
        if (group == null) {
            return resource;
        }
        if (StringUtils.equals(UserGroupType.WORKSPACE, type)) {
            resource.setWorkspaces(getWorkspaceGroupResource(group.getScopeId()));
        }
        if (StringUtils.equals(UserGroupType.PROJECT, type)) {
            resource.setProjects(getProjectGroupResource(group.getScopeId()));
        }
        return resource;
    }

    private List<Project> getProjectGroupResource(String scopeId) {
        if (StringUtils.equals(scopeId, GLOBAL)) {
            return QueryChain.of(Project.class).list();
        }
        Workspace workspace = QueryChain.of(Workspace.class).where(WORKSPACE.ID.eq(scopeId)).one();
        if (Objects.nonNull(workspace)) {
            return QueryChain.of(Project.class)
                    .where(PROJECT.WORKSPACE_ID.eq(workspace.getId()))
                    .list();
        }
        Project project = QueryChain.of(Project.class).where(PROJECT.ID.eq(scopeId)).one();
        List<Project> list = new ArrayList<>();
        if (project != null) {
            list.add(project);
        }
        return list;
    }

    private List<Workspace> getWorkspaceGroupResource(String scopeId) {
        return QueryChain.of(Workspace.class)
                .where(WORKSPACE.ID.eq(scopeId).when(!StringUtils.equals(scopeId, GLOBAL)))
                .list();
    }

    private long checkSourceRole(String workspaceId, String userId, String roleId) {
        return systemGroupService.checkSourceRole(workspaceId, userId, roleId);
    }

    private void checkWorkspace(Workspace workspace) {
        val count = QueryChain.of(Workspace.class).where(WORKSPACE.NAME.eq(workspace.getName())
                .and(WORKSPACE.ID.ne(workspace.getId()))).exists();
        if (count) {
            BusinessException.throwException("工作空间名已存在");
        }
    }

    private List<RelatedSource> getRelatedSource(String userId) {
        val queryChain = QueryChain.of(UserGroup.class).select(PROJECT.WORKSPACE_ID.as("workspaceId"), PROJECT.ID.as("projectId"))
                .from(USER_GROUP)
                .join(PROJECT).on(USER_GROUP.SOURCE_ID.eq(PROJECT.ID))
                .join(WORKSPACE).on(PROJECT.WORKSPACE_ID.eq(WORKSPACE.ID))
                .where(USER_GROUP.USER_ID.eq(userId)).union(
                        QueryChain.of(UserGroup.class).select(WORKSPACE.ID.as("workspaceId"))
                                .select("''")
                                .from(USER_GROUP)
                                .join(WORKSPACE).on(USER_GROUP.SOURCE_ID.eq(WORKSPACE.ID))
                                .where(USER_GROUP.USER_ID.eq(userId))
                );
        return mapper.selectListByQueryAs(queryChain, RelatedSource.class);
    }
}
