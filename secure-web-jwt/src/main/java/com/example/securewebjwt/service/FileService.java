package com.example.securewebjwt.service;

import com.example.securewebjwt.model.FileMeta;
import com.example.securewebjwt.model.User;
import com.example.securewebjwt.repository.FileMetaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.Formatter;

@Service
public class FileService {
    private final String uploadDir;
    private final FileMetaRepository fileMetaRepository;
    private final AuditService auditService;

    public FileService(@Value("${file.upload-dir}") String uploadDir, FileMetaRepository fileMetaRepository, AuditService auditService) {
        this.uploadDir = uploadDir;
        this.fileMetaRepository = fileMetaRepository;
        this.auditService = auditService;
        new File(uploadDir).mkdirs();
    }

    public FileMeta store(MultipartFile mf, User uploader) throws Exception {
        File out = new File(uploadDir, System.currentTimeMillis() + "_" + mf.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(out)) {
            fos.write(mf.getBytes());
        }
        byte[] data = Files.readAllBytes(out.toPath());
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(data);
        String sha = bytesToHex(digest);
        FileMeta meta = FileMeta.builder()
                .filename(out.getName())
                .sha256(sha)
                .size(out.length())
                .uploaderId(uploader.getId())
                .build();
        fileMetaRepository.save(meta);
        auditService.log(uploader.getUsername(), "UPLOAD_FILE:" + meta.getId());
        return meta;
    }

    private String bytesToHex(byte[] bytes) {
        try (Formatter fmt = new Formatter()) {
            for (byte b : bytes) fmt.format("%02x", b);
            return fmt.toString();
        }
    }
}
