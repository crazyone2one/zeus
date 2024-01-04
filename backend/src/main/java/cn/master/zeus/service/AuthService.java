package cn.master.zeus.service;

import cn.master.zeus.dto.AuthenticationResponse;
import cn.master.zeus.dto.request.AuthenticationRequest;
import cn.master.zeus.dto.request.RefreshTokenRequest;

/**
 * @author Created by 11's papa on 12/29/2023
 **/
public interface AuthService {

    AuthenticationResponse authenticate(AuthenticationRequest request);

    AuthenticationResponse refreshToken(RefreshTokenRequest request);

    void logout(String userName);
}
