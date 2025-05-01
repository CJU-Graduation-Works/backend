package com.example.login;

import com.example.login.entity.User;
import com.example.login.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DbTestRunner implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        String testEmail = "test@example.com";

        System.out.println("✅ DB Test 시작");
        // 이미 존재하면 삽입 건너뛰기
        if (userRepository.existsByEmail(testEmail)) {
            System.out.println("ℹ️ " + testEmail + " 이미 존재 — 삽입 건너뜀");
            return;
        }

        User user = new User();
        user.setEmail(testEmail);
        user.setPassword("password123");
        user.setRole("USER");

        try {
            userRepository.save(user);
            System.out.println("✅ DB에 사용자 저장 완료: " + user.getEmail());
        } catch (DataIntegrityViolationException e) {
            // 혹시 다른 제약 오류가 나도 무시
            System.out.println("⚠️ 사용자 삽입 중 오류 발생, 무시함: " + e.getMessage());
        }
    }
}
