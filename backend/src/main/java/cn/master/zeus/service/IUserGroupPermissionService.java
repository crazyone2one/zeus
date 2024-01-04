package cn.master.zeus.service;

import cn.master.zeus.dto.UserGroupPermissionDTO;
import cn.master.zeus.entity.UserGroupPermission;
import com.mybatisflex.core.service.IService;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

/**
 *  服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface IUserGroupPermissionService extends IService<UserGroupPermission> {

    UserGroupPermissionDTO getUserGroupPermission(String userId);

    List<GrantedAuthority> buildGrantedAuthority(UserGroupPermissionDTO groupPermissions);
}
