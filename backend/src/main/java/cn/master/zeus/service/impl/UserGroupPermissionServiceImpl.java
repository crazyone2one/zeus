package cn.master.zeus.service.impl;

import cn.master.zeus.dto.GroupResourceDTO;
import cn.master.zeus.dto.UserGroupPermissionDTO;
import cn.master.zeus.entity.SystemGroup;
import cn.master.zeus.entity.UserGroup;
import cn.master.zeus.entity.UserGroupPermission;
import cn.master.zeus.mapper.UserGroupPermissionMapper;
import cn.master.zeus.service.IUserGroupPermissionService;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.val;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cn.master.zeus.entity.table.SystemGroupTableDef.SYSTEM_GROUP;
import static cn.master.zeus.entity.table.UserGroupPermissionTableDef.USER_GROUP_PERMISSION;
import static cn.master.zeus.entity.table.UserGroupTableDef.USER_GROUP;

/**
 *  服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
public class UserGroupPermissionServiceImpl extends ServiceImpl<UserGroupPermissionMapper, UserGroupPermission> implements IUserGroupPermissionService {

    @Override
    public UserGroupPermissionDTO getUserGroupPermission(String userId) {
        UserGroupPermissionDTO permissionDTO = new UserGroupPermissionDTO();
        List<GroupResourceDTO> list = new ArrayList<>();
        val userGroups = QueryChain.of(UserGroup.class).where(USER_GROUP.USER_ID.eq(userId)).list();
        if (CollectionUtils.isEmpty(userGroups)) {
            return permissionDTO;
        }
        permissionDTO.setUserGroups(userGroups);
        List<String> groupList = userGroups.stream().map(UserGroup::getGroupId).toList();
        val groups = QueryChain.of(SystemGroup.class).where(SYSTEM_GROUP.ID.in(groupList)).list();
        permissionDTO.setGroups(groups);
        for (SystemGroup gp : groups) {
            GroupResourceDTO dto = new GroupResourceDTO();
            dto.setGroup(gp);
            val userGroupPermissions = QueryChain.of(UserGroupPermission.class).where(USER_GROUP_PERMISSION.GROUP_ID.eq(gp.getId())).list();
            dto.setUserGroupPermissions(userGroupPermissions);
            list.add(dto);
        }
        permissionDTO.setList(list);
        return permissionDTO;
    }

    @Override
    public List<GrantedAuthority> buildGrantedAuthority(UserGroupPermissionDTO groupPermissions) {
        List<String> roles = new ArrayList<>();
        List<GroupResourceDTO> permissionsList = groupPermissions.getList();
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(permissionsList)) {
            List<List<UserGroupPermission>> list = permissionsList.stream().map(GroupResourceDTO::getUserGroupPermissions).toList();
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(list)) {
                list.forEach(p -> {
                            List<String> list1 = p.stream().map(UserGroupPermission::getPermissionId).toList();
                            roles.addAll(list1);
                        }
                );
            }
        }
        return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }
}
