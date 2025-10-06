package com.example.securewebjwt.controller;

import com.example.securewebjwt.model.User;
import com.example.securewebjwt.service.AuditService;
import com.example.securewebjwt.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final UserService userService;
    private final AuditService auditService;

    public AdminController(UserService userService, AuditService auditService) {
        this.userService = userService;
        this.auditService = auditService;
    }

    @GetMapping("/users")
    public ResponseEntity<?> users() {
        return ResponseEntity.ok(userService.allUsers());
    }

    @PostMapping("/users/{id}/lock")
    public ResponseEntity<?> lockUser(@PathVariable Long id, @RequestParam boolean lock) {
        userService.lockUser(id, lock);
        return ResponseEntity.ok(java.util.Map.of("ok", true));
    }
    @PostMapping("/users/{id}/unlock")
    public ResponseEntity<?> unlockUser(@PathVariable Long id, @RequestParam boolean lock) {
        userService.lockUser(id, lock);
        return ResponseEntity.ok(java.util.Map.of("ok", true));
    }
    @GetMapping("/audit")
    public ResponseEntity<?> audit() {
        return ResponseEntity.ok(auditService.all());
    }
}
