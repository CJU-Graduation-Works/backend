package com.example.login.controller;

import com.example.login.dto.LoginRequest;
import com.example.login.dto.SignupRequest;
import com.example.login.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest request) {
        authService.signup(request);
        return ResponseEntity.ok("회원가입 성공");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // JWT는 서버에 저장하지 않는 Stateless 방식이라
        // 클라이언트(Flutter) 쪽에서 토큰을 삭제하면 됩니다.
        return ResponseEntity.ok("로그아웃 성공");
    }
}
