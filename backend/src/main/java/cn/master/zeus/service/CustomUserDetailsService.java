package cn.master.zeus.service;

import cn.master.zeus.dto.UserGroupPermissionDTO;
import cn.master.zeus.entity.CustomUserDetails;
import cn.master.zeus.entity.SystemUser;
import com.mybatisflex.core.query.QueryChain;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static cn.master.zeus.entity.table.SystemUserTableDef.SYSTEM_USER;

/**
 * @author Created by 11's papa on 12/29/2023
 **/
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final IUserGroupPermissionService userGroupPermissionService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SystemUser user = QueryChain.of(SystemUser.class).where(SYSTEM_USER.NAME.eq(username)).one();
        if (Objects.isNull(user)) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return createLocalUser(user);
    }

    private CustomUserDetails createLocalUser(SystemUser user) {
        UserGroupPermissionDTO userGroupPermission = userGroupPermissionService.getUserGroupPermission(user.getId());
        List<GrantedAuthority> authorities = userGroupPermissionService.buildGrantedAuthority(userGroupPermission);
        return new CustomUserDetails(user, authorities);
    }
}
