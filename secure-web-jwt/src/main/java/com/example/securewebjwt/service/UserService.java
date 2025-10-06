package com.example.securewebjwt.service;

import com.example.securewebjwt.model.User;
import com.example.securewebjwt.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final AuditService auditService;

    public UserService(UserRepository userRepository, AuditService auditService) {
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User updateProfile(String username, String fullName) {
        User u = getByUsername(username);
        u.setFullName(fullName);
        User saved = userRepository.save(u);
        auditService.log(username, "UPDATE_PROFILE");
        return saved;
    }

    public List<User> allUsers() {
        return userRepository.findAll();
    }

    public void lockUser(Long userId, boolean lock) {
        User u = userRepository.findById(userId).orElseThrow();
        u.setLocked(lock);
        userRepository.save(u);
        auditService.log(u.getUsername(), lock ? "LOCKED" : "UNLOCKED");
    }
}
