package cn.master.zeus.service.impl;

import cn.master.zeus.common.constants.UserGroupConstants;
import cn.master.zeus.common.exception.BusinessException;
import cn.master.zeus.dto.WorkspaceMemberDTO;
import cn.master.zeus.dto.request.project.ProjectRequest;
import cn.master.zeus.entity.Project;
import cn.master.zeus.entity.SystemGroup;
import cn.master.zeus.entity.SystemUser;
import cn.master.zeus.entity.UserGroup;
import cn.master.zeus.mapper.ProjectMapper;
import cn.master.zeus.mapper.UserGroupMapper;
import cn.master.zeus.service.IProjectService;
import cn.master.zeus.service.ISystemGroupService;
import cn.master.zeus.util.SessionUtils;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static cn.master.zeus.entity.table.ProjectTableDef.PROJECT;
import static cn.master.zeus.entity.table.SystemGroupTableDef.SYSTEM_GROUP;
import static cn.master.zeus.entity.table.SystemUserTableDef.SYSTEM_USER;
import static cn.master.zeus.entity.table.UserGroupTableDef.USER_GROUP;
import static com.mybatisflex.core.query.QueryMethods.distinct;
import static com.mybatisflex.core.query.QueryMethods.max;

/**
 * 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements IProjectService {
    private final UserGroupMapper userGroupMapper;
    private final ISystemGroupService systemGroupService;

    @Override
    public Page<Project> getProjectPage(ProjectRequest request) {
        QueryChain<Project> queryChain = queryChain().where(PROJECT.WORKSPACE_ID.eq(request.getWorkspaceId())
                        .and(PROJECT.NAME.like(request.getName())))
                .orderBy(PROJECT.UPDATE_TIME.desc());
        Page<Project> paginate = mapper.paginate(new Page<>(request.getPageNumber(), request.getPageSize()), queryChain);
        List<Project> records = paginate.getRecords();
        if (CollectionUtils.isNotEmpty(records)) {
            records.forEach(r -> r.setMemberSize(setMember(r.getId())));
        }
        return paginate;
    }

    private long setMember(String project) {
        QueryWrapper wrapper = new QueryWrapper();
        QueryWrapper query = new QueryWrapper()
                .select("distinct *").from(
                        wrapper.select(SYSTEM_USER.ALL_COLUMNS)
                                .from(USER_GROUP).join(SYSTEM_USER).on(USER_GROUP.USER_ID.eq(SYSTEM_USER.ID))
                                .where(USER_GROUP.SOURCE_ID.eq(project))
                                .orderBy(USER_GROUP.UPDATE_TIME.desc())
                ).as("temp");
        return mapper.selectCountByQuery(query);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public Project addProject(Project project) {
        saveProject(project);
        String userId = SessionUtils.getUserId();
        UserGroup userGroup = UserGroup.builder().userId(userId).groupId(UserGroupConstants.PROJECT_ADMIN).sourceId(project.getId()).build();
        userGroupMapper.insert(userGroup);
        updateLastProjectIdIfNull(project.getId(), userId);
        return project;
    }

    @Override
    public long getProjectMemberSize(String id) {
        QueryChain<UserGroup> userGroupQueryChain = QueryChain.of(UserGroup.class).select("count(distinct (`system_user`.id))")
                .from(USER_GROUP)
                .join(SYSTEM_USER).on(USER_GROUP.USER_ID.eq(SYSTEM_USER.ID))
                .where(USER_GROUP.SOURCE_ID.eq(id));
        return userGroupMapper.selectCountByQuery(userGroupQueryChain);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProject(String id) {
        deleteProjectUserGroup(id);
        mapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProject(Project project) {
        boolean exists = queryChain().where(PROJECT.NAME.eq(project.getName()).and(PROJECT.WORKSPACE_ID.eq(SessionUtils.getCurrentWorkspaceId())
                .and(PROJECT.ID.ne(project.getId())))).exists();
        if (exists) {
            BusinessException.throwException("project_name_already_exists");
        }
        mapper.update(project);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMember(WorkspaceMemberDTO memberDTO) {
        String projectId = memberDTO.getProjectId();
        String userId = memberDTO.getId();
        List<SystemGroup> memberGroups = systemGroupService.getProjectMemberGroups(projectId, userId);
        // 修改后的角色
        List<String> groups = memberDTO.getGroupIds();
        List<String> allGroupIds = new java.util.ArrayList<>(memberGroups.stream().map(SystemGroup::getId).toList());
        groups.forEach(group -> {
            if (checkSourceRole(projectId, userId, group) == 0) {
                UserGroup userGroup = UserGroup.builder().userId(userId).groupId(group).sourceId(projectId).build();
                userGroupMapper.insert(userGroup);
            }
        });
        allGroupIds.removeAll(groups);
        if (CollectionUtils.isNotEmpty(allGroupIds)) {
            QueryChain<UserGroup> userGroupQueryChain = QueryChain.of(UserGroup.class).where(USER_GROUP.USER_ID.eq(userId).and(USER_GROUP.SOURCE_ID.eq(projectId)).and(USER_GROUP.GROUP_ID.in(allGroupIds)));
            userGroupMapper.deleteByQuery(userGroupQueryChain);
        }
    }

    @Override
    public List<Project> getProjectList(ProjectRequest request) {
        return queryChain().where(PROJECT.WORKSPACE_ID.eq(request.getWorkspaceId())
                        .and(PROJECT.NAME.like(request.getName())))
                .orderBy(PROJECT.UPDATE_TIME.desc()).list();
    }

    @Override
    public List<Project> getUserProject(ProjectRequest request) {
        boolean superUser = userGroupMapper.isSuperUser(SessionUtils.getUserId());
        if (superUser) {
            return queryChain().where(PROJECT.WORKSPACE_ID.eq(request.getWorkspaceId())
                            .and(PROJECT.NAME.like(request.getName())))
                    .orderBy(PROJECT.UPDATE_TIME.desc()).list();
        }
        return queryChain().select(distinct(PROJECT.ALL_COLUMNS))
                .from(SYSTEM_GROUP)
                .join(USER_GROUP).on(SYSTEM_GROUP.ID.eq(USER_GROUP.GROUP_ID))
                .join(PROJECT).on(USER_GROUP.SOURCE_ID.eq(PROJECT.ID))
                .where(SYSTEM_GROUP.TYPE.eq("PROJECT").and(USER_GROUP.USER_ID.eq(request.getUserId()))
                        .and(PROJECT.NAME.like(request.getName())).and(PROJECT.WORKSPACE_ID.eq(request.getWorkspaceId())))
                .list();
    }

    private long checkSourceRole(String sourceId, String userId, String group) {
        QueryChain<UserGroup> userGroupQueryChain = QueryChain.of(UserGroup.class).select("COUNT(id)")
                .from(USER_GROUP)
                .where(USER_GROUP.SOURCE_ID.eq(sourceId).and(USER_GROUP.USER_ID.eq(userId)).and(USER_GROUP.GROUP_ID.eq(group)));
        return userGroupMapper.selectCountByQuery(userGroupQueryChain);
    }

    private void deleteProjectUserGroup(String projectId) {
        QueryChain<UserGroup> userGroupQueryChain = QueryChain.of(UserGroup.class).where(USER_GROUP.SOURCE_ID.eq(projectId));
        userGroupMapper.deleteByQuery(userGroupQueryChain);
    }

    private void updateLastProjectIdIfNull(String id, String userId) {
        UpdateChain.of(SystemUser.class).set(SYSTEM_USER.LAST_PROJECT_ID, id)
                .where(SYSTEM_USER.ID.eq(userId).and(SYSTEM_USER.LAST_PROJECT_ID.isNull().or(SYSTEM_USER.LAST_PROJECT_ID.eq(""))));
    }

    private void saveProject(Project project) {
        boolean exists = queryChain().where(PROJECT.WORKSPACE_ID.eq(project.getWorkspaceId()).and(PROJECT.NAME.eq(project.getName()))).exists();
        if (exists) {
            BusinessException.throwException("project_name_already_exists");
        }
        project.setSystemId(genSystemId());
        project.setCreateUser(SessionUtils.getUserId());
        mapper.insert(project);
    }

    private String genSystemId() {
        QueryChain<Project> select = queryChain().select(max(PROJECT.SYSTEM_ID));
        String maxSystemIdInDb = (String) mapper.selectObjectByQuery(select);
        String systemId = "10001";
        if (StringUtils.isNotEmpty(maxSystemIdInDb)) {
            systemId = String.valueOf(Long.parseLong(maxSystemIdInDb) + 1);
        }
        return systemId;
    }
}
