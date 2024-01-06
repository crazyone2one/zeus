package cn.master.zeus.service.impl;

import cn.master.zeus.common.constants.UserGroupType;
import cn.master.zeus.dto.WorkspaceResource;
import cn.master.zeus.dto.request.GroupRequest;
import cn.master.zeus.dto.request.group.EditGroupRequest;
import cn.master.zeus.entity.Project;
import cn.master.zeus.entity.SystemGroup;
import cn.master.zeus.entity.UserGroup;
import cn.master.zeus.entity.Workspace;
import cn.master.zeus.mapper.SystemGroupMapper;
import cn.master.zeus.service.ISystemGroupService;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;

import static cn.master.zeus.entity.table.ProjectTableDef.PROJECT;
import static cn.master.zeus.entity.table.SystemGroupTableDef.SYSTEM_GROUP;
import static cn.master.zeus.entity.table.UserGroupTableDef.USER_GROUP;
import static cn.master.zeus.entity.table.WorkspaceTableDef.WORKSPACE;
import static com.mybatisflex.core.constant.SqlConsts.DISTINCT;
import static com.mybatisflex.core.query.QueryMethods.distinct;

/**
 * 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class SystemGroupServiceImpl extends ServiceImpl<SystemGroupMapper, SystemGroup> implements ISystemGroupService {
    private static final String GLOBAL = "global";

    @Override
    public List<SystemGroup> getGroupsByType(GroupRequest request) {
        String resourceId = request.getResourceId();
        String type = request.getType();
        List<String> scopeList = Arrays.asList(GLOBAL, resourceId);
        if (StringUtils.equals(type, UserGroupType.PROJECT) && StringUtils.isNotBlank(request.getProjectId())) {
            scopeList = Arrays.asList(GLOBAL, resourceId, request.getProjectId());
        }
        return queryChain().where(SYSTEM_GROUP.SCOPE_ID.in(scopeList).and(SYSTEM_GROUP.TYPE.eq(type))).list();
    }

    @Override
    public List<SystemGroup> getWorkspaceMemberGroups(String workspaceId, String userId) {
        QueryChain<SystemGroup> queryChain = queryChain().select(SYSTEM_GROUP.ALL_COLUMNS).from(WORKSPACE)
                .join(USER_GROUP).on(WORKSPACE.ID.eq(USER_GROUP.SOURCE_ID))
                .join(SYSTEM_GROUP).on(SYSTEM_GROUP.ID.eq(USER_GROUP.GROUP_ID))
                .where(WORKSPACE.ID.eq(workspaceId).and(USER_GROUP.USER_ID.eq(userId)));
        return mapper.selectListByQueryAs(queryChain, SystemGroup.class);
    }

    @Override
    public long checkSourceRole(String sourceId, String userId, String groupId) {
        return QueryChain.of(UserGroup.class).where(USER_GROUP.USER_ID.eq(userId).and(USER_GROUP.SOURCE_ID.eq(sourceId)).and(USER_GROUP.GROUP_ID.eq(groupId))).count();
    }

    @Override
    public List<Map<String, Object>> getAllUserGroup(String userId) {
        List<Map<String, Object>> list = new ArrayList<>();
        List<UserGroup> userGroups = QueryChain.of(UserGroup.class).where(USER_GROUP.USER_ID.eq(userId)).list();
        List<String> groupsIds = userGroups.stream().map(UserGroup::getGroupId).distinct().toList();
        groupsIds.forEach(id -> {
            SystemGroup group = mapper.selectOneById(id);
            String type = group.getType();
            Map<String, Object> map = new HashMap<>(2);
            map.put("type", id + "+" + type);
            WorkspaceResource workspaceResource = listResource(id, group.getType());
            List<String> collect = userGroups.stream().filter(ugp -> ugp.getGroupId().equals(id)).map(UserGroup::getSourceId).toList();
            map.put("ids", collect);
            if (StringUtils.equals(type, UserGroupType.WORKSPACE)) {
                map.put("workspaces", workspaceResource.getWorkspaces());
            }
            if (StringUtils.equals(type, UserGroupType.PROJECT)) {
                map.put("projects", workspaceResource.getProjects());
            }
            list.add(map);
        });
        return list;
    }

    @Override
    public List<SystemGroup> getGroupByType(EditGroupRequest request) {
        String type = request.getType();
        if (StringUtils.isBlank(type)) {
            return new ArrayList<>();
        }
        QueryChain<SystemGroup> queryChain = queryChain()
                .select(distinct(SYSTEM_GROUP.ID,SYSTEM_GROUP.NAME,SYSTEM_GROUP.TYPE))
                .where(SYSTEM_GROUP.TYPE.eq(type).when(!StringUtils.equals(type, UserGroupType.SYSTEM))
                        .and(SYSTEM_GROUP.SCOPE_ID.eq(GLOBAL).when(BooleanUtils.isTrue(request.isOnlyQueryGlobal()))));
        return queryChain.list();
    }

    private WorkspaceResource listResource(String groupId, String type) {
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
}
