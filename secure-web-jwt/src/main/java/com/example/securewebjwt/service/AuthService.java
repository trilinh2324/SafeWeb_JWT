package com.example.securewebjwt.service;

import com.example.securewebjwt.model.*;
import com.example.securewebjwt.repository.RefreshTokenRepository;
import com.example.securewebjwt.repository.UserRepository;
import com.example.securewebjwt.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuditService auditService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, RefreshTokenRepository refreshTokenRepository, AuditService auditService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
        this.auditService = auditService;
    }

    public User register(String username, String password, String fullName) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username exists");
        }
        User u = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .fullName(fullName)
                .roles(Set.of(Role.ROLE_USER))
                .locked(false)
                .build();
        User saved = userRepository.save(u);
        auditService.log(username, "REGISTER");
        return saved;
    }

    public Map<String,String> login(String username, String password) {
        User u = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Invalid credentials"));
        if (u.isLocked()) throw new RuntimeException("Account locked");
        if (!passwordEncoder.matches(password, u.getPassword())) throw new RuntimeException("Invalid credentials");
        String access = jwtUtil.generateAccessToken(username, u.getRoles());
        String refresh = jwtUtil.generateRefreshToken(username);
        RefreshToken rt = RefreshToken.builder()
                .token(refresh)
                .user(u)
                .expiryDate(Instant.now().plusMillis(604800000))
                .build();
        refreshTokenRepository.save(rt);
        auditService.log(username, "LOGIN");
        Map<String,String> tokens = new HashMap<>();
        tokens.put("accessToken", access);
        tokens.put("refreshToken", refresh);
        return tokens;
    }

    public Map<String,String> refresh(String refreshToken) {
        var found = refreshTokenRepository.findByToken(refreshToken).orElseThrow(() -> new RuntimeException("Invalid refresh token"));
        if (found.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token expired");
        }
        String username = found.getUser().getUsername();
        String access = jwtUtil.generateAccessToken(username, found.getUser().getRoles());
        auditService.log(username, "REFRESH_TOKEN");
        return Map.of("accessToken", access);
    }

    public void logout(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken).ifPresent(rt -> {
            auditService.log(rt.getUser().getUsername(), "LOGOUT");
            refreshTokenRepository.delete(rt);
        });
    }
}
