package com.example.securewebjwt.controller;

import com.example.securewebjwt.model.Product;
import com.example.securewebjwt.model.User;
import com.example.securewebjwt.repository.UserRepository;
import com.example.securewebjwt.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    private final UserRepository userRepository;

    public ProductController(ProductService productService, UserRepository userRepository) {
        this.productService = productService; this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> create(Authentication auth, @RequestBody Product p) {
        String username = (String) auth.getPrincipal();
        User u = userRepository.findByUsername(username).orElseThrow();
        p.setOwnerId(u.getId());
        Product saved = productService.create(p);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/my")
    public ResponseEntity<?> myProducts(Authentication auth) {
        String username = (String) auth.getPrincipal();
        User u = userRepository.findByUsername(username).orElseThrow();
        return ResponseEntity.ok(productService.findByOwner(u.getId()));
    }

    @GetMapping
    public ResponseEntity<?> all(Authentication auth) {
        return ResponseEntity.ok(productService.all());
    }

    @PutMapping
    public ResponseEntity<?> update(Authentication auth, @RequestBody Product p) {

        String username = (String) auth.getPrincipal();
        User u = userRepository.findByUsername(username).orElseThrow();
        if (!u.getId().equals(p.getOwnerId()) && u.getRoles().stream().noneMatch(r -> r.name().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(403).body(java.util.Map.of("error","forbidden"));
        }
        Product updated = productService.update(p);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(Authentication auth, @PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.ok(java.util.Map.of("ok", true));
    }
}
