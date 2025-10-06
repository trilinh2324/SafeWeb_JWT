package com.example.securewebjwt.controller;

import com.example.securewebjwt.model.User;
import com.example.securewebjwt.service.UserService;
import com.example.securewebjwt.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) { this.userService = userService; this.userRepository = userRepository; }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication auth) {
        String username = (String) auth.getPrincipal();
        User u = userService.getByUsername(username);
        return ResponseEntity.ok(u);
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateMe(Authentication auth, @RequestBody Map<String,String> body) {
        String username = (String) auth.getPrincipal();
        User u = userService.updateProfile(username, body.getOrDefault("fullName", ""));
        return ResponseEntity.ok(u);
    }
}
