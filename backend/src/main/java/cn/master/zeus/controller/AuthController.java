package cn.master.zeus.controller;

import cn.master.zeus.dto.AuthenticationResponse;
import cn.master.zeus.dto.request.AuthenticationRequest;
import cn.master.zeus.dto.request.RefreshTokenRequest;
import cn.master.zeus.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * @author Created by 11's papa on 12/29/2023
 **/
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest userLogin) {
        AuthenticationResponse response = authService.authenticate(userLogin);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public String logout(Principal principal) {
        authService.logout(principal.getName());
        return "User logged out successfully";
    }

    @PostMapping("/refresh")
    public AuthenticationResponse refresh(@RequestBody @Valid RefreshTokenRequest request) {
        return authService.refreshToken(request);
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok("hello world");
    }
}
