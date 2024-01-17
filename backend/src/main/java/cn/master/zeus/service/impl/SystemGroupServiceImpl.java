package cn.master.zeus.service.impl;

import cn.master.zeus.common.constants.UserGroupConstants;
import cn.master.zeus.common.constants.UserGroupType;
import cn.master.zeus.common.exception.BusinessException;
import cn.master.zeus.dto.*;
import cn.master.zeus.dto.request.GroupRequest;
import cn.master.zeus.dto.request.group.EditGroupRequest;
import cn.master.zeus.dto.request.group.EditGroupUserRequest;
import cn.master.zeus.entity.*;
import cn.master.zeus.mapper.SystemGroupMapper;
import cn.master.zeus.mapper.SystemUserMapper;
import cn.master.zeus.mapper.UserGroupMapper;
import cn.master.zeus.mapper.UserGroupPermissionMapper;
import cn.master.zeus.service.ISystemGroupService;
import cn.master.zeus.service.IUserGroupPermissionService;
import cn.master.zeus.service.IUserGroupService;
import cn.master.zeus.util.JsonUtils;
import cn.master.zeus.util.SessionUtils;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static cn.master.zeus.entity.table.ProjectTableDef.PROJECT;
import static cn.master.zeus.entity.table.SystemGroupTableDef.SYSTEM_GROUP;
import static cn.master.zeus.entity.table.SystemUserTableDef.SYSTEM_USER;
import static cn.master.zeus.entity.table.UserGroupPermissionTableDef.USER_GROUP_PERMISSION;
import static cn.master.zeus.entity.table.UserGroupTableDef.USER_GROUP;
import static cn.master.zeus.entity.table.WorkspaceTableDef.WORKSPACE;
import static com.mybatisflex.core.query.QueryMethods.distinct;

/**
 * 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemGroupServiceImpl extends ServiceImpl<SystemGroupMapper, SystemGroup> implements ISystemGroupService {
    private static final String GLOBAL = "global";
    private final IUserGroupService userGroupService;
    private final UserGroupMapper userGroupMapper;
    private final UserGroupPermissionMapper userGroupPermissionMapper;
    private final IUserGroupPermissionService userGroupPermissionService;
    final StringRedisTemplate stringRedisTemplate;
    private final SystemUserMapper systemUserMapper;

    private static final Map<String, List<String>> MAP = new HashMap<>(4) {{
        put(UserGroupType.SYSTEM, Arrays.asList(UserGroupType.SYSTEM, UserGroupType.WORKSPACE, UserGroupType.PROJECT));
        put(UserGroupType.WORKSPACE, Arrays.asList(UserGroupType.WORKSPACE, UserGroupType.PROJECT));
        put(UserGroupType.PROJECT, Collections.singletonList(UserGroupType.PROJECT));
    }};

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
                .select(distinct(SYSTEM_GROUP.ID, SYSTEM_GROUP.NAME, SYSTEM_GROUP.TYPE))
                .where(SYSTEM_GROUP.TYPE.eq(type).when(!StringUtils.equals(type, UserGroupType.SYSTEM))
                        .and(SYSTEM_GROUP.SCOPE_ID.eq(GLOBAL).when(BooleanUtils.isTrue(request.isOnlyQueryGlobal()))));
        return queryChain.list();
    }

    @Override
    public List<SystemGroup> getProjectMemberGroups(String projectId, String userId) {
        return queryChain()
                .from(PROJECT)
                .join(USER_GROUP).on(USER_GROUP.SOURCE_ID.eq(PROJECT.ID))
                .join(SYSTEM_GROUP).on(SYSTEM_GROUP.ID.eq(USER_GROUP.GROUP_ID))
                .where(PROJECT.ID.eq(projectId).and(USER_GROUP.USER_ID.eq(userId))).list();
    }

    @Override
    public Page<GroupDTO> getGroupPage(EditGroupRequest request) {
        List<UserGroupDTO> userGroup = userGroupService.getUserGroup(SessionUtils.getUserId(), request.getProjectId());
        List<String> groupTypeList = userGroup.stream().map(UserGroupDTO::getType).distinct().toList();
        return getGroups(groupTypeList, request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SystemGroup addGroup(EditGroupRequest request) {
        checkGroupExist(request);
        SystemGroup group = SystemGroup.builder().name(request.getName())
                .description(request.getDescription())
                .system(false)
                .type(request.getType())
                .scopeId(BooleanUtils.isTrue(request.getGlobal()) ? GLOBAL : request.getScopeId())
                .creator(SessionUtils.getUserId())
                .build();
        mapper.insert(group);
        return group;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editGroup(EditGroupRequest request) {
        if (StringUtils.equals(request.getId(), UserGroupConstants.SUPER_GROUP)) {
            BusinessException.throwException("超级管理员无法编辑！");
        }
        if (StringUtils.equals(request.getId(), UserGroupConstants.ADMIN)) {
            BusinessException.throwException("系统管理员无法编辑！");
        }
        checkGroupExist(request);
        mapper.update(request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteGroup(String id) {
        SystemGroup group = mapper.selectOneById(id);
        if (Objects.isNull(group)) {
            BusinessException.throwException("group does not exist!");
        }
        if (BooleanUtils.isTrue(group.getSystem())) {
            BusinessException.throwException("系统用户组不支持删除！");
        }
        mapper.deleteById(id);
        // 删除用户组与用户的关系
        QueryChain<UserGroup> userGroupQueryChain = QueryChain.of(UserGroup.class).where(USER_GROUP.GROUP_ID.eq(id));
        userGroupService.remove(userGroupQueryChain);
        // 删除用户组与角色的关系
        QueryChain<UserGroupPermission> userGroupPermissionQueryChain = QueryChain.of(UserGroupPermission.class).where(USER_GROUP_PERMISSION.GROUP_ID.eq(id));
        userGroupPermissionMapper.deleteByQuery(userGroupPermissionQueryChain);
    }

    @Override
    public GroupPermissionDTO getGroupResource(SystemGroup group) {
        GroupPermissionDTO dto = new GroupPermissionDTO();
        List<UserGroupPermission> groupPermissions = QueryChain.of(UserGroupPermission.class).where(USER_GROUP_PERMISSION.GROUP_ID.eq(group.getId())).list();
        List<String> groupPermissionIds = groupPermissions.stream().map(UserGroupPermission::getPermissionId).toList();
        GroupJson groupJson = loadPermissionJsonFromService();
        List<GroupResource> resource = groupJson.getResource();
        List<GroupPermission> permissions = groupJson.getPermissions();
        List<GroupResourceDTO> dtoPermissions = dto.getPermissions();
        dtoPermissions.addAll(getResourcePermission(resource, permissions, group, groupPermissionIds));
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editGroupPermission(EditGroupRequest request) {
        // 超级用户组禁止修改权限
        if (StringUtils.equals(request.getUserGroupId(), UserGroupConstants.SUPER_GROUP)) {
            return;
        }
        List<GroupPermission> permissions = request.getPermissions();
        if (CollectionUtils.isEmpty(permissions)) {
            return;
        }
        QueryChain<UserGroupPermission> userGroupPermissionQueryChain = QueryChain.of(UserGroupPermission.class)
                .where(USER_GROUP_PERMISSION.GROUP_ID.eq(request.getUserGroupId()));
        userGroupPermissionMapper.deleteByQuery(userGroupPermissionQueryChain);
        String groupId = request.getUserGroupId();
        permissions.forEach(permission -> {
            if (BooleanUtils.isTrue(permission.getChecked())) {
                String permissionId = permission.getId();
                String resourceId = permission.getResourceId();
                UserGroupPermission build = UserGroupPermission.builder().groupId(groupId).permissionId(permissionId).moduleId(resourceId).build();
                userGroupPermissionMapper.insert(build);
            }
        });
    }

    @Override
    public Page<SystemUser> getGroupUser(EditGroupRequest request) {
        QueryChain<SystemUser> queryChain = QueryChain.of(SystemUser.class).select(distinct(SYSTEM_USER.ID, SYSTEM_USER.NAME, SYSTEM_USER.EMAIL))
                .join(USER_GROUP).on(USER_GROUP.USER_ID.eq(SYSTEM_USER.ID))
                .where(USER_GROUP.GROUP_ID.eq(request.getUserGroupId())
                        .and(SYSTEM_USER.NAME.like(request.getName()))
                        .and(USER_GROUP.SOURCE_ID.eq(request.getProjectId())))
                .orderBy(USER_GROUP.UPDATE_TIME.desc());
        return systemUserMapper.paginate(Page.of(request.getPageNumber(), request.getPageSize()), queryChain);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addGroupUser(EditGroupUserRequest request) {
        if (StringUtils.isBlank(request.getGroupId()) || CollectionUtils.isEmpty(request.getUserIds())) {
            log.info("add group user warning, please check param!");
            return;
        }
        SystemGroup group = mapper.selectOneById(request.getGroupId());
        if (group == null) {
            log.info("add group user warning, group is null. group id: " + request.getGroupId());
            return;
        }
        if (StringUtils.equals(group.getType(), UserGroupType.SYSTEM)) {
            UserGroupPermissionDTO userGroupPermission = userGroupPermissionService.getUserGroupPermission(SessionUtils.getUserId());
            long count = userGroupPermission.getGroups().stream().filter(g -> StringUtils.equals(g.getType(), UserGroupType.SYSTEM)).count();
            if (count > 0) {
                addSystemGroupUser(group, request.getUserIds());
            } else {
                log.warn("no permission to add system group!");
            }
        } else {
            if (CollectionUtils.isNotEmpty(request.getSourceIds())) {
                addNotSystemGroupUser(group, request.getUserIds(), request.getSourceIds());
            }
        }
    }

    private void addNotSystemGroupUser(SystemGroup group, List<String> userIds, List<String> sourceIds) {
        for (String userId : userIds) {
            SystemUser user = systemUserMapper.selectOneById(userId);
            if (Objects.isNull(user)) {
                continue;
            }
            List<UserGroup> userGroups = QueryChain.of(UserGroup.class).where(USER_GROUP.USER_ID.eq(userId).and(USER_GROUP.GROUP_ID.eq(group.getId()))).list();
            List<String> existSourceIds = userGroups.stream().map(UserGroup::getSourceId).toList();
            List<String> toAddSourceIds = new ArrayList<>(sourceIds);
            toAddSourceIds.removeAll(existSourceIds);
            toAddSourceIds.forEach(s -> {
                UserGroup userGroup = UserGroup.builder().userId(userId).groupId(group.getId()).sourceId(s).build();
                userGroupMapper.insert(userGroup);
            });
        }
    }

    private void addSystemGroupUser(SystemGroup group, List<String> userIds) {
        for (String userId : userIds) {
            SystemUser user = systemUserMapper.selectOneById(userId);
            if (Objects.isNull(user)) {
                continue;
            }
            List<UserGroup> userGroups = QueryChain.of(UserGroup.class).where(USER_GROUP.USER_ID.eq(userId).and(USER_GROUP.GROUP_ID.eq(group.getId()))).list();
            if (CollectionUtils.isEmpty(userGroups)) {
                UserGroup userGroup = UserGroup.builder().userId(userId).groupId(group.getId()).sourceId("system").build();
                userGroupMapper.insert(userGroup);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editGroupUser(EditGroupUserRequest request) {
        String groupId = request.getGroupId();
        SystemGroup group = mapper.selectOneById(groupId);
        if (!StringUtils.equals(group.getType(), UserGroupType.SYSTEM)) {
            List<String> userIds = request.getUserIds();
            if (CollectionUtils.isNotEmpty(userIds)) {
                // 编辑单个用户
                String userId = userIds.get(0);
                List<String> sourceIds = request.getSourceIds();
                QueryChain<UserGroup> userGroupQueryChain = QueryChain.of(UserGroup.class).where(USER_GROUP.USER_ID.eq(userId).and(USER_GROUP.GROUP_ID.eq(groupId)));
                userGroupMapper.deleteByQuery(userGroupQueryChain);
                sourceIds.forEach(s -> {
                    UserGroup userGroup = UserGroup.builder().userId(userId).groupId(groupId).sourceId(s).build();
                    userGroupMapper.insert(userGroup);
                });
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeGroupMember(String userId, String groupId) {
        QueryChain<UserGroup> userGroupQueryChain = QueryChain.of(UserGroup.class).where(USER_GROUP.GROUP_ID.eq(groupId).and(USER_GROUP.USER_ID.eq(userId)));
        userGroupMapper.deleteByQuery(userGroupQueryChain);
    }

    private List<GroupResourceDTO> getResourcePermission(List<GroupResource> resources, List<GroupPermission> permissions, SystemGroup group, List<String> permissionList) {
        List<GroupResourceDTO> dto = new ArrayList<>();
        List<GroupResource> grs;
        if (StringUtils.equals(group.getId(), UserGroupConstants.SUPER_GROUP)) {
            grs = resources;
            permissions.forEach(p -> p.setChecked(true));
        } else {
            grs = resources
                    .stream()
                    .filter(g -> g.getId().startsWith(group.getType()) || BooleanUtils.isTrue(g.isGlobal()))
                    .collect(Collectors.toList());
            permissions.forEach(p -> {
                if (permissionList.contains(p.getId())) {
                    p.setChecked(true);
                }
            });
        }

        for (GroupResource r : grs) {
            GroupResourceDTO resourceDTO = new GroupResourceDTO();
            resourceDTO.setResource(r);
            List<GroupPermission> collect = permissions
                    .stream()
                    .filter(p -> StringUtils.equals(r.getId(), p.getResourceId()))
                    .collect(Collectors.toList());
            resourceDTO.setPermissions(collect);
            resourceDTO.setType(r.getId().split("_")[0]);
            dto.add(resourceDTO);
        }
        return dto;
    }

    private GroupJson loadPermissionJsonFromService() {
        GroupJson groupJson = null;
        List<GroupResource> globalResources = new ArrayList<>();
        Object obj = stringRedisTemplate.opsForHash().get("PERMISSION", "service");
        GroupJson microServiceGroupJson = JsonUtils.parseObject((String) obj, GroupJson.class);
        assert microServiceGroupJson != null;
        List<GroupResource> globalResource = microServiceGroupJson.getResource().stream().filter(gp -> BooleanUtils.isTrue(gp.isGlobal())).toList();
        if (CollectionUtils.isNotEmpty(globalResource)) {
            globalResources.addAll(globalResource);
            microServiceGroupJson.getResource().removeIf(gp -> BooleanUtils.isTrue(gp.isGlobal()));
        }
        groupJson = microServiceGroupJson;
        // 拼装时通用权限Resource放在最后
        if (!globalResources.isEmpty()) {
            groupJson.getResource().addAll(globalResources);
        }
        return groupJson;
    }

    private void checkGroupExist(EditGroupRequest request) {
        boolean exists = queryChain().where(SYSTEM_GROUP.NAME.eq(request.getName()).and(SYSTEM_GROUP.ID.ne(request.getId()))).exists();
        if (exists) {
            BusinessException.throwException("用户组名称已存在");
        }
    }

    private Page<GroupDTO> getGroups(List<String> groupTypeList, EditGroupRequest request) {
        if (groupTypeList.contains(UserGroupType.SYSTEM)) {
            return getUserGroup(UserGroupType.SYSTEM, request);
        }

        if (groupTypeList.contains(UserGroupType.WORKSPACE)) {
            return getUserGroup(UserGroupType.WORKSPACE, request);
        }

        if (groupTypeList.contains(UserGroupType.PROJECT)) {
            return getUserGroup(UserGroupType.PROJECT, request);
        }
        return new Page<>();
    }

    private Page<GroupDTO> getUserGroup(String groupType, EditGroupRequest request) {
        List<String> types;
        String workspaceId = SessionUtils.getCurrentWorkspaceId();
        String projectId = SessionUtils.getCurrentProjectId();
        List<String> scopes = Arrays.asList(GLOBAL, workspaceId, projectId);
        if (StringUtils.equals(groupType, UserGroupType.SYSTEM)) {
            scopes = new ArrayList<>();
        }
        types = MAP.get(groupType);
        request.setTypes(types);
        request.setScopes(scopes);
        QueryChain<SystemGroup> queryChain = queryChain().select("*")
                .from(
                        queryChain().select(SYSTEM_GROUP.ALL_COLUMNS, WORKSPACE.NAME.as("scopeName"))
                                .join(WORKSPACE).on(SYSTEM_GROUP.SCOPE_ID.eq(WORKSPACE.ID))
                                .where(SYSTEM_GROUP.TYPE.in(request.getTypes()).and(SYSTEM_GROUP.SCOPE_ID.in(request.getScopes())))
                                .union(
                                        queryChain().select(SYSTEM_GROUP.ALL_COLUMNS, SYSTEM_GROUP.SCOPE_ID.as("scopeName"))
                                                .from(SYSTEM_GROUP)
                                                .where(SYSTEM_GROUP.SCOPE_ID.eq("global").and(SYSTEM_GROUP.TYPE.in(request.getTypes())))
                                ).union(
                                        queryChain().select(SYSTEM_GROUP.ALL_COLUMNS, SYSTEM_GROUP.SCOPE_ID.as("scopeName"))
                                                .from(SYSTEM_GROUP)
                                                .where(SYSTEM_GROUP.SCOPE_ID.eq("system").and(SYSTEM_GROUP.TYPE.in(request.getTypes())))
                                ).union(
                                        queryChain().select(SYSTEM_GROUP.ALL_COLUMNS, PROJECT.NAME.as("scopeName"))
                                                .from(SYSTEM_GROUP)
                                                .join(PROJECT).on(SYSTEM_GROUP.SCOPE_ID.eq(PROJECT.ID))
                                                .where(SYSTEM_GROUP.TYPE.in(request.getTypes()).and(SYSTEM_GROUP.SCOPE_ID.in(request.getScopes())))
                                )
                ).as("temp")
                .where(SYSTEM_GROUP.NAME.like(request.getName()));
        Page<GroupDTO> page = mapper.paginateAs(Page.of(request.getPageNumber(), request.getPageSize()), queryChain, GroupDTO.class);
        // 人员数量
        List<GroupDTO> records = page.getRecords();
        if (CollectionUtils.isNotEmpty(records)) {
            records.forEach(groupDTO -> {
                        List<UserGroup> userGroups = QueryChain.of(UserGroup.class).select(distinct(USER_GROUP.USER_ID))
                                .where(USER_GROUP.GROUP_ID.eq(groupDTO.getId())).list();
                        groupDTO.setMemberSize(userGroups.size());
                    }
            );
        }
        return page;
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
