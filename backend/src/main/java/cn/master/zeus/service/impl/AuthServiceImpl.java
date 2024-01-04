package cn.master.zeus.service.impl;

import cn.master.zeus.common.enums.TokenType;
import cn.master.zeus.config.auth.JwtProvider;
import cn.master.zeus.dto.AuthenticationResponse;
import cn.master.zeus.dto.UserDTO;
import cn.master.zeus.dto.UserGroupPermissionDTO;
import cn.master.zeus.dto.request.AuthenticationRequest;
import cn.master.zeus.dto.request.RefreshTokenRequest;
import cn.master.zeus.entity.CustomUserDetails;
import cn.master.zeus.entity.SystemToken;
import cn.master.zeus.entity.SystemUser;
import cn.master.zeus.mapper.SystemTokenMapper;
import cn.master.zeus.service.AuthService;
import cn.master.zeus.service.IUserGroupPermissionService;
import com.mybatisflex.core.query.QueryChain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static cn.master.zeus.entity.table.SystemTokenTableDef.SYSTEM_TOKEN;
import static cn.master.zeus.entity.table.SystemUserTableDef.SYSTEM_USER;

/**
 * @author Created by 11's papa on 12/29/2023
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final IUserGroupPermissionService userGroupPermissionService;
    private final SystemTokenMapper systemTokenMapper;
    private final UserDetailsService userDetailsService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        SystemUser user = QueryChain.of(SystemUser.class).where(SYSTEM_USER.NAME.eq(request.getUsername())).one();
        UserGroupPermissionDTO userGroupPermission = userGroupPermissionService.getUserGroupPermission(user.getId());
        CustomUserDetails customUserDetails = buildCustomUser(user, userGroupPermission);
        val accessToken = jwtProvider.generateAccessToken(customUserDetails);
        val refreshToken = jwtProvider.generateRefreshToken(customUserDetails);
        revokeUserToken(user, Arrays.asList(TokenType.ACCESS_TOKEN, TokenType.REFRESH_TOKEN));
        saveUserToken(user, accessToken, TokenType.ACCESS_TOKEN.name());
        saveUserToken(user, refreshToken, TokenType.REFRESH_TOKEN.name());
        return new AuthenticationResponse(accessToken, refreshToken, buildUserDto(user, userGroupPermission));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        String userName = jwtProvider.extractUserName(refreshToken);
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(userName);
        if (jwtProvider.isTokenValid(refreshToken, userDetails)) {
            SystemUser user = QueryChain.of(SystemUser.class).where(SYSTEM_USER.NAME.eq(userName)).one();
            UserGroupPermissionDTO userGroupPermission = userGroupPermissionService.getUserGroupPermission(user.getId());
            CustomUserDetails customUserDetails = buildCustomUser(user, userGroupPermission);
            val accessToken = jwtProvider.generateAccessToken(customUserDetails);
            revokeUserToken(user, List.of(TokenType.ACCESS_TOKEN));
            saveUserToken(user, accessToken, TokenType.ACCESS_TOKEN.name());
            return new AuthenticationResponse(accessToken, refreshToken, buildUserDto(user, userGroupPermission));
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void logout(String userName) {
        SystemUser user = QueryChain.of(SystemUser.class).where(SYSTEM_USER.NAME.eq(userName)).one();
        revokeUserToken(user, Arrays.asList(TokenType.ACCESS_TOKEN, TokenType.REFRESH_TOKEN));
        SecurityContextHolder.clearContext();
    }

    protected void saveUserToken(SystemUser user, String token, String tokenType) {
        val systemToken = SystemToken.builder().token(token).tokenType(tokenType).userId(user.getId()).revoked(false).build();
        systemTokenMapper.insert(systemToken);
    }

    protected void revokeUserToken(SystemUser user, List<TokenType> tokenTypes) {
        tokenTypes.forEach(tt -> {
            val systemToken = QueryChain.of(SystemToken.class).where(
                    SYSTEM_TOKEN.USER_ID.eq(user.getId())
                            .and(SYSTEM_TOKEN.TOKEN_TYPE.eq(tt.name()))
                            .and(SYSTEM_TOKEN.REVOKED.eq(false))
            ).list();
            if (CollectionUtils.isNotEmpty(systemToken)) {
                systemToken.forEach(st -> {
                    st.setRevoked(true);
                    systemTokenMapper.update(st);
                });
            }
        });
    }

    private UserDTO buildUserDto(SystemUser user, UserGroupPermissionDTO userGroupPermission) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        // 设置其他属性
        userDTO.setUserGroups(userGroupPermission.getUserGroups());
        userDTO.setGroups(userGroupPermission.getGroups());
        userDTO.setGroupPermissions(userGroupPermission.getList());
        return userDTO;
    }

    private CustomUserDetails buildCustomUser(SystemUser user, UserGroupPermissionDTO userGroupPermission) {
        return new CustomUserDetails(user, userGroupPermissionService.buildGrantedAuthority(userGroupPermission));
    }


}
