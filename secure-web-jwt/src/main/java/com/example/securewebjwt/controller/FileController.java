package com.example.securewebjwt.controller;

import com.example.securewebjwt.model.FileMeta;
import com.example.securewebjwt.model.User;
import com.example.securewebjwt.repository.UserRepository;
import com.example.securewebjwt.service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
public class FileController {
    private final FileService fileService;
    private final UserRepository userRepository;

    public FileController(FileService fileService, UserRepository userRepository) {
        this.fileService = fileService;
        this.userRepository = userRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(Authentication auth, @RequestParam("file") MultipartFile file) throws Exception {
        String username = (String) auth.getPrincipal();
        User u = userRepository.findByUsername(username).orElseThrow();
        FileMeta meta = fileService.store(file, u);
        return ResponseEntity.ok(meta);
    }
}
