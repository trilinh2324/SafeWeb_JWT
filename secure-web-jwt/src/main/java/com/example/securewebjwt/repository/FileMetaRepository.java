package com.example.securewebjwt.repository;

import com.example.securewebjwt.model.FileMeta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileMetaRepository extends JpaRepository<FileMeta, Long> {
}
