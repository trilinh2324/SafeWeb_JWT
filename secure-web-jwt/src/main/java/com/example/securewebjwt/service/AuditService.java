package com.example.securewebjwt.service;

import com.example.securewebjwt.model.AuditLog;
import com.example.securewebjwt.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class AuditService {
    private final AuditLogRepository repo;

    public AuditService(AuditLogRepository repo) { this.repo = repo; }

    public void log(String username, String action) {
        repo.save(AuditLog.builder().username(username).action(action).whenTime(Instant.now()).build());
    }

    public List<AuditLog> all() { return repo.findAll(); }
}
