package cn.master.zeus.service.impl;

import cn.master.zeus.common.constants.UserGroupType;
import cn.master.zeus.common.exception.BusinessException;
import cn.master.zeus.dto.UserDTO;
import cn.master.zeus.dto.UserGroupPermissionDTO;
import cn.master.zeus.dto.request.AddMemberRequest;
import cn.master.zeus.dto.request.BaseRequest;
import cn.master.zeus.dto.request.QueryMemberRequest;
import cn.master.zeus.dto.request.member.EditPasswordRequest;
import cn.master.zeus.dto.request.user.SystemUserDTO;
import cn.master.zeus.dto.request.user.UserRequest;
import cn.master.zeus.entity.Project;
import cn.master.zeus.entity.SystemGroup;
import cn.master.zeus.entity.SystemUser;
import cn.master.zeus.entity.UserGroup;
import cn.master.zeus.mapper.ProjectMapper;
import cn.master.zeus.mapper.SystemUserMapper;
import cn.master.zeus.mapper.UserGroupMapper;
import cn.master.zeus.service.ISystemUserService;
import cn.master.zeus.service.IUserGroupPermissionService;
import cn.master.zeus.util.SessionUtils;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static cn.master.zeus.entity.table.ProjectTableDef.PROJECT;
import static cn.master.zeus.entity.table.SystemGroupTableDef.SYSTEM_GROUP;
import static cn.master.zeus.entity.table.SystemUserTableDef.SYSTEM_USER;
import static cn.master.zeus.entity.table.UserGroupTableDef.USER_GROUP;
import static cn.master.zeus.entity.table.WorkspaceTableDef.WORKSPACE;

/**
 * 用户信息表 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SystemUserServiceImpl extends ServiceImpl<SystemUserMapper, SystemUser> implements ISystemUserService {
    private final UserGroupMapper userGroupMapper;
    private final PasswordEncoder passwordEncoder;
    private final IUserGroupPermissionService userGroupPermissionService;
    private final ProjectMapper projectMapper;

    @Override
    public List<SystemUser> getMemberList(QueryMemberRequest request) {
        QueryWrapper wrapper = new QueryWrapper();
        QueryWrapper query = new QueryWrapper()
                .select("distinct *").from(
                        wrapper.select(SYSTEM_USER.ALL_COLUMNS)
                                .from(USER_GROUP).join(SYSTEM_USER).on(USER_GROUP.USER_ID.eq(SYSTEM_USER.ID))
                                .where(USER_GROUP.SOURCE_ID.eq(request.getWorkspaceId()))
                                .and(SYSTEM_USER.NAME.like(request.getName(), StringUtils.isNoneBlank(request.getName())))
                                .orderBy(USER_GROUP.UPDATE_TIME.desc())
                ).as("temp");
        return mapper.selectListByQuery(query);
    }

    @Override
    public Page<SystemUserDTO> getMemberPage(BaseRequest request) {
        QueryWrapper wrapper = new QueryWrapper();
        QueryWrapper query = new QueryWrapper()
                .select("distinct *").from(
                        wrapper.select(SYSTEM_USER.ALL_COLUMNS)
                                .from(USER_GROUP).join(SYSTEM_USER).on(USER_GROUP.USER_ID.eq(SYSTEM_USER.ID))
                                .where(USER_GROUP.SOURCE_ID.eq(request.getWorkspaceId()))
                                .and(SYSTEM_USER.NAME.like(request.getName(), StringUtils.isNoneBlank(request.getName())))
                                .orderBy(USER_GROUP.UPDATE_TIME.desc())
                ).as("temp");
        Page<SystemUserDTO> paginate = mapper.paginateAs(Page.of(request.getPageNumber(), request.getPageSize()), query, SystemUserDTO.class);
        buildUserGroup(paginate, request.getWorkspaceId());
        return paginate;
    }

    @Override
    public List<SystemUser> getUserList() {
        return queryChain().orderBy(SYSTEM_USER.UPDATE_TIME.desc()).list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMember(AddMemberRequest request) {
        if (CollectionUtils.isEmpty(request.getUserIds()) || CollectionUtils.isEmpty(request.getGroupIds())) {
            log.warn("user ids or group ids is empty.");
            return;
        }
        List<String> allUserIds = queryChain().list().stream().map(SystemUser::getId).toList();
        List<SystemGroup> wsGroups = QueryChain.of(SystemGroup.class).where(SYSTEM_GROUP.TYPE.eq(UserGroupType.WORKSPACE)).list();
        List<String> wsGroupIds = wsGroups.stream().map(SystemGroup::getId).toList();
        for (String userId : request.getUserIds()) {
            if (!allUserIds.contains(userId)) {
                log.warn("user id {} is not exist!", userId);
                continue;
            }
            List<UserGroup> userGroups = QueryChain.of(UserGroup.class).where(USER_GROUP.USER_ID.eq(userId).and(USER_GROUP.SOURCE_ID.in(request.getWorkspaceId()))).list();
            if (!userGroups.isEmpty()) {
                BusinessException.throwException("该用户已存在于当前成员列表中");
            }
            for (String groupId : request.getGroupIds()) {
                if (!wsGroupIds.contains(groupId)) {
                    log.warn("group id {} is not exist or not belong to workspace level.", groupId);
                    continue;
                }
                UserGroup userGroup = UserGroup.builder().groupId(groupId).sourceId(request.getWorkspaceId()).userId(userId).build();
                userGroupMapper.insert(userGroup);
            }
        }
    }

    @Override
    public Page<SystemUserDTO> getUserPageList(UserRequest request) {
        QueryChain<SystemUser> queryChain = queryChain().select(SYSTEM_USER.ID, SYSTEM_USER.NAME, SYSTEM_USER.STATUS, SYSTEM_USER.EMAIL)
                .where(SYSTEM_USER.NAME.like(request.getName()).and(SYSTEM_USER.EMAIL.like(request.getEmail())));
        Page<SystemUserDTO> paginate = mapper.paginateAs(request.getPageNumber(), request.getPageSize(), queryChain, SystemUserDTO.class);
        buildUserGroup(paginate, request.getWorkspaceId());
        return paginate;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserRole(cn.master.zeus.dto.request.member.UserRequest user) {
        String userId = user.getId();
        QueryChain<UserGroup> userGroupQueryChain = QueryChain.of(UserGroup.class).where(USER_GROUP.USER_ID.eq(userId));
        List<UserGroup> userGroups = userGroupQueryChain.list();
        List<String> list = userGroups.stream().map(UserGroup::getSourceId).toList();
        if (!CollectionUtils.isEmpty(list)) {
            if (list.contains(user.getLastWorkspaceId())) {
                user.setLastWorkspaceId(null);
                mapper.update(user);
            }
        }
        userGroupMapper.deleteByQuery(userGroupQueryChain);
        List<Map<String, Object>> groups = user.getGroups();
        if (!groups.isEmpty()) {
            insertUserGroup(groups, user.getId());
        }
        boolean exists = queryChain().where(SYSTEM_USER.EMAIL.eq(user.getEmail()).and(SYSTEM_USER.ID.ne(user.getId()))).exists();
        if (exists) {
            BusinessException.throwException("邮箱已存在");
        }
        mapper.update(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(cn.master.zeus.dto.request.member.UserRequest request) {
        SystemUser user = new SystemUser();
        BeanUtils.copyProperties(request, user);
        user.setStatus(true);
        user.setUserName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        checkEmailIsExist(user.getEmail());
        return mapper.insert(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDTO switchUserResource(String sign, String sourceId) {
        String userId = SessionUtils.getUserId();
        UserDTO user = getUserDTO(userId);
        SystemUser newUser = new SystemUser();
        boolean superUser = userGroupMapper.isSuperUser(userId);
        if (StringUtils.equals("workspace", sign)) {
            user.setLastWorkspaceId(sourceId);
            user.setLastProjectId(StringUtils.EMPTY);
            List<Project> projects = getProjectListByWsAndUserId(userId, sourceId);
            if (CollectionUtils.isNotEmpty(projects)) {
                user.setLastProjectId(projects.get(0).getId());
            } else {
                if (superUser) {
                    List<Project> allWsProject = QueryChain.of(Project.class).where(PROJECT.WORKSPACE_ID.eq(sourceId)).list();
                    if (CollectionUtils.isNotEmpty(allWsProject)) {
                        user.setLastProjectId(allWsProject.get(0).getId());
                    }
                } else {
                    user.setLastProjectId(StringUtils.EMPTY);
                }
            }
        }
        BeanUtils.copyProperties(user, newUser);
        mapper.update(newUser);
        return getUserDTO(userId);
    }

    private List<Project> getProjectListByWsAndUserId(String userId, String workspaceId) {
        List<Project> projects = QueryChain.of(Project.class).where(PROJECT.WORKSPACE_ID.eq(workspaceId)).list();
        List<UserGroup> userGroups = QueryChain.of(UserGroup.class).where(USER_GROUP.USER_ID.eq(userId)).list();
        List<Project> projectList = new ArrayList<>();
        userGroups.forEach(userGroup -> projects.forEach(project -> {
            if (StringUtils.equals(userGroup.getSourceId(), project.getId())) {
                if (!projectList.contains(project)) {
                    projectList.add(project);
                }
            }
        }));
        return projectList;
    }

    @Override
    public UserDTO getUserDTO(String userId) {
        UserGroupPermissionDTO userGroupPermission = userGroupPermissionService.getUserGroupPermission(userId);
        SystemUser user = mapper.selectOneById(userId);
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        // 设置其他属性
        userDTO.setUserGroups(userGroupPermission.getUserGroups());
        userDTO.setGroups(userGroupPermission.getGroups());
        userDTO.setGroupPermissions(userGroupPermission.getList());
        return userDTO;
    }

    @Override
    public Page<SystemUserDTO> getProjectMemberPage(BaseRequest request) {
        QueryWrapper wrapper = new QueryWrapper();
        QueryWrapper query = new QueryWrapper()
                .select("distinct *").from(
                        wrapper.select(SYSTEM_USER.ALL_COLUMNS)
                                .from(USER_GROUP).join(SYSTEM_USER).on(USER_GROUP.USER_ID.eq(SYSTEM_USER.ID))
                                .where(USER_GROUP.SOURCE_ID.eq(request.getProjectId()))
                                .orderBy(USER_GROUP.UPDATE_TIME.desc())
                ).as("temp");
        Page<SystemUserDTO> paginate = mapper.paginateAs(Page.of(request.getPageNumber(), request.getPageSize()), query, SystemUserDTO.class);
        List<SystemUserDTO> records = paginate.getRecords();
        if (CollectionUtils.isNotEmpty(records)) {
            records.forEach(u -> {
                List<SystemGroup> groups = getWorkspaceMemberGroups(request.getWorkspaceId(), u.getId());
                groups.removeAll(Collections.singleton(null));
                u.setGroups(groups);
            });
        }
        return paginate;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDTO updateCurrentUser(SystemUser user) {
        String currentUserId = SessionUtils.getUserId();
        if (!StringUtils.equals(currentUserId, user.getId())) {
            BusinessException.throwException("未经授权");
        }
        updateUser(user);
        return getUserDTO(user.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(SystemUser user) {
        if (StringUtils.isNotBlank(user.getEmail())) {
            boolean exists = queryChain().where(SYSTEM_USER.EMAIL.eq(user.getEmail())
                    .and(SYSTEM_USER.ID.ne(user.getId()))).exists();
            if (exists) {
                BusinessException.throwException("用户邮箱已存在");
            }
        }
        SystemUser userFromDB = mapper.selectOneById(user.getId());
        if (user.getLastWorkspaceId() != null && !StringUtils.equals(user.getLastWorkspaceId(), userFromDB.getLastWorkspaceId())) {
            List<Project> projects = getProjectListByWsAndUserId(user.getId(), user.getLastWorkspaceId());
            if (!projects.isEmpty()) {
                boolean present = projects.stream().allMatch((p -> StringUtils.equals(p.getId(), user.getLastProjectId())));
                if (!present) {
                    user.setLastProjectId(projects.get(0).getId());
                }
            } else {
                user.setLastProjectId(StringUtils.EMPTY);
            }
        }
        mapper.update(user);
    }

    @Override
    public Page<SystemUser> getProjectMemberList(BaseRequest request) {
        QueryWrapper wrapper = new QueryWrapper();
        QueryWrapper query = new QueryWrapper()
                .select("distinct *").from(
                        wrapper.select(SYSTEM_USER.ALL_COLUMNS)
                                .from(USER_GROUP).join(SYSTEM_USER).on(USER_GROUP.USER_ID.eq(SYSTEM_USER.ID))
                                .where(USER_GROUP.SOURCE_ID.eq(request.getProjectId()))
                                .orderBy(USER_GROUP.UPDATE_TIME.desc())
                ).as("temp");
        return mapper.paginate(Page.of(request.getPageNumber(), request.getPageSize()), query);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProjectMember(String projectId, String userId) {
        List<SystemGroup> groups = QueryChain.of(SystemGroup.class).where(SYSTEM_GROUP.TYPE.eq(UserGroupType.PROJECT)).list();
        List<String> groupIds = groups.stream().map(SystemGroup::getId).toList();
        if (CollectionUtils.isEmpty(groupIds)) {
            return;
        }
        SystemUser user = mapper.selectOneById(userId);
        if (StringUtils.equals(projectId, user.getLastProjectId())) {
            user.setLastProjectId(StringUtils.EMPTY);
            mapper.update(user);
        }
        QueryChain<UserGroup> userGroupQueryChain = QueryChain.of(UserGroup.class).where(USER_GROUP.USER_ID.eq(userId)
                .and(USER_GROUP.SOURCE_ID.eq(projectId))
                .and(USER_GROUP.GROUP_ID.in(groupIds)));
        userGroupMapper.deleteByQuery(userGroupQueryChain);
    }

    @Override
    public void addProjectMember(AddMemberRequest request) {
        addGroupMember("PROJECT", request.getProjectId(), request.getUserIds(), request.getGroupIds());
    }

    @Override
    public int updateUserPassword(EditPasswordRequest request) {
        SystemUser user = mapper.selectOneById(request.getId());
        String newPassword = request.getNewPassword();
        user.setPassword(passwordEncoder.encode(newPassword));
        return mapper.update(user);
    }

    private void addGroupMember(String type, String sourceId, List<String> userIds, List<String> groupIds) {
        if (!StringUtils.equalsAny(type, "PROJECT", "WORKSPACE") || StringUtils.isBlank(sourceId)
                || CollectionUtils.isEmpty(userIds) || CollectionUtils.isEmpty(groupIds)) {
            log.warn("add member warning, please check param!");
            return;
        }
        List<String> dbOptionalGroupIds = getGroupIdsByType(type, sourceId);
        for (String userId : userIds) {
            SystemUser user = mapper.selectOneById(userId);
            if (Objects.isNull(user)) {
                log.warn("add member warning, invalid user id: " + userId);
                continue;
            }
            List<String> toAddGroupIds = new ArrayList<>(groupIds);
            List<String> existGroupIds = getUserExistSourceGroup(userId, sourceId);
            toAddGroupIds.removeAll(existGroupIds);
            toAddGroupIds.retainAll(dbOptionalGroupIds);
            if (CollectionUtils.isEmpty(toAddGroupIds)) {
                log.warn("group ids not in db or not has permission, please check!");
                continue;
            }
            toAddGroupIds.forEach(groupId -> {
                UserGroup userGroup = UserGroup.builder().userId(userId).groupId(groupId).sourceId(sourceId).build();
                userGroupMapper.insert(userGroup);
            });
        }
    }

    private List<String> getGroupIdsByType(String type, String sourceId) {
        List<String> scopeList = Arrays.asList("global", sourceId);
        if (StringUtils.equals(type, "PROJECT")) {
            Project project = projectMapper.selectOneById(sourceId);
            if (Objects.nonNull(project)) {
                scopeList = Arrays.asList("global", sourceId, project.getWorkspaceId());
            }
        }
        List<SystemGroup> groups = QueryChain.of(SystemGroup.class).where(SYSTEM_GROUP.SCOPE_ID.in(scopeList)
                .and(SYSTEM_GROUP.TYPE.eq(type))).list();
        return groups.stream().map(SystemGroup::getId).toList();
    }

    private List<String> getUserExistSourceGroup(String userId, String sourceId) {
        List<UserGroup> userGroups = QueryChain.of(UserGroup.class).where(USER_GROUP.USER_ID.eq(userId).and(USER_GROUP.SOURCE_ID.eq(sourceId))).list();
        return userGroups.stream().map(UserGroup::getGroupId).collect(Collectors.toList());
    }

    private void checkEmailIsExist(String email) {
        boolean exists = queryChain().where(SYSTEM_USER.EMAIL.eq(email)).exists();
        if (exists) {
            BusinessException.throwException("邮箱已存在");
        }
    }

    private void insertUserGroup(List<Map<String, Object>> groups, String userId) {
        groups.forEach(map -> {
            String idType = (String) map.get("type");
            String[] arr = idType.split("\\+");
            String groupId = arr[0];
            String type = arr[1];
            if (StringUtils.equals(type, UserGroupType.SYSTEM)) {
                UserGroup userGroup = UserGroup.builder().userId(userId).groupId(groupId).sourceId("system").build();
                userGroupMapper.insert(userGroup);
            } else {
                List<String> ids = (List<String>) map.get("ids");
                //SystemGroup group = QueryChain.of(SystemGroup.class).where(SYSTEM_GROUP.ID.eq(groupId)).one();
                ids.forEach(id -> {
                    UserGroup userGroup = UserGroup.builder().userId(userId).groupId(groupId).sourceId(id).build();
                    userGroupMapper.insert(userGroup);
                });
            }
        });
    }

    private void buildUserGroup(Page<SystemUserDTO> paginate, String request) {
        List<SystemUserDTO> records = paginate.getRecords();
        if (CollectionUtils.isNotEmpty(records)) {
            records.forEach(u -> {
                List<SystemGroup> groups = getWorkspaceMemberGroups(request, u.getId());
                groups.removeAll(Collections.singleton(null));
                u.setGroups(groups);
            });
        }
    }

    private List<SystemGroup> getWorkspaceMemberGroups(String wId, String uId) {
        return QueryChain.of(SystemGroup.class).select(SYSTEM_GROUP.ALL_COLUMNS).from(WORKSPACE)
                .join(USER_GROUP).on(WORKSPACE.ID.eq(USER_GROUP.SOURCE_ID))
                .join(SYSTEM_GROUP).on(SYSTEM_GROUP.ID.eq(USER_GROUP.GROUP_ID))
                .where(WORKSPACE.ID.eq(wId).and(USER_GROUP.USER_ID.eq(uId))).list();
    }
}
