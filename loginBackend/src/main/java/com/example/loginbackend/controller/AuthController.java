package com.example.loginbackend.controller;

import com.example.loginbackend.dto.AuthRequest;
import com.example.loginbackend.dto.ChangePasswordRequest;
import com.example.loginbackend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// UserInfoResponse DTO 클래스 추가
class UserInfoResponse {
    private String name;
    private String phone;
    private String email;

    // 생성자
    public UserInfoResponse(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    // Getter 메서드
    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }
}

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody AuthRequest request) {
        if (authService.signup(request)) {
            return ResponseEntity.ok("회원가입 성공");
        } else {
            return ResponseEntity.badRequest().body("이미 존재하는 이메일입니다.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest request) {
        if (authService.login(request)) {
            return ResponseEntity.ok("로그인 성공");
        } else {
            return ResponseEntity.status(401).body("로그인 실패");
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
        if (authService.changePassword(request)) {
            return ResponseEntity.ok("비밀번호 변경 성공");
        } else {
            return ResponseEntity.badRequest().body("비밀번호 변경 실패: 현재 비밀번호 불일치 또는 사용자 없음");
        }
    }

    @GetMapping("/find-email")
    public ResponseEntity<String> findEmailByName(@RequestParam String name) {
        return authService.findEmailByName(name)
                .map(email -> ResponseEntity.ok(email))
                .orElse(ResponseEntity.status(404).body("해당 이름의 사용자를 찾을 수 없습니다."));
    }

    @GetMapping("/find-emails")
    public ResponseEntity<List<String>> findEmailsByNameAndPhone(@RequestParam String name, @RequestParam String phone) {
        List<String> emails = authService.findEmailsByNameAndPhone(name, phone);
        if (emails.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        } else {
            return ResponseEntity.ok(emails);
        }
    }

    @PostMapping("/send-verification-code")
    public ResponseEntity<String> sendVerificationCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("이메일을 입력해주세요.");
        }
        if (authService.sendVerificationCode(email)) {
            String verificationCode = authService.getVerificationCode(email);
            if (verificationCode != null) {
                return ResponseEntity.ok("{\"code\":\"" + verificationCode + "\"}");
            } else {
                return ResponseEntity.ok("인증 코드가 발송되었습니다.");
            }
        } else {
            return ResponseEntity.status(404).body("해당 이메일의 사용자를 찾을 수 없습니다.");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        String newPassword = request.get("newPassword");

        if (email == null || email.isEmpty() || code == null || code.isEmpty() || newPassword == null || newPassword.isEmpty()) {
            return ResponseEntity.badRequest().body("이메일, 인증 코드 및 새 비밀번호를 모두 입력해주세요.");
        }

        if (authService.verifyVerificationCode(email, code)) {
            if (authService.resetPassword(email, newPassword)) {
                return ResponseEntity.ok("비밀번호가 성공적으로 재설정되었습니다.");
            } else {
                return ResponseEntity.status(500).body("비밀번호 재설정 중 오류가 발생했습니다.");
            }
        } else {
            return ResponseEntity.status(400).body("인증 코드가 유효하지 않습니다.");
        }
    }

    @GetMapping("/user-info") // 사용자 정보 가져오는 엔드포인트 추가
    public ResponseEntity<UserInfoResponse> getUserInfo(@RequestParam String email) {
        return authService.findUserByEmail(email) // AuthService에 findUserByEmail 메서드 추가 필요
                .map(user -> ResponseEntity.ok(new UserInfoResponse(user.getName(), user.getPhone(), user.getEmail())))
                .orElse(ResponseEntity.status(404).body(null)); // 사용자 없을 경우 404 응답
    }

    @PutMapping("/user-info") // 사용자 정보 업데이트 엔드포인트 추가
    public ResponseEntity<String> updateUserInfo(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String name = request.get("name");
        String phone = request.get("phone");

        if (email == null || email.isEmpty() || name == null || name.isEmpty() || phone == null || phone.isEmpty()) {
            return ResponseEntity.badRequest().body("이메일, 이름, 전화번호를 모두 입력해주세요.");
        }

        // AuthService에 updateUserInfo 메서드 추가 필요
        if (authService.updateUserInfo(email, name, phone)) {
            return ResponseEntity.ok("사용자 정보가 성공적으로 업데이트되었습니다.");
        } else {
            return ResponseEntity.status(404).body("해당 이메일의 사용자를 찾을 수 없습니다.");
        }
    }
}
