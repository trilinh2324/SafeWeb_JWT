package com.example.securewebjwt.service;

import com.example.securewebjwt.model.Product;
import com.example.securewebjwt.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final AuditService auditService;

    public ProductService(ProductRepository productRepository, AuditService auditService) {
        this.productRepository = productRepository;
        this.auditService = auditService;
    }

    public Product create(Product p) {
        Product saved = productRepository.save(p);
        auditService.log("system", "CREATE_PRODUCT:" + saved.getId());
        return saved;
    }

    public List<Product> findByOwner(Long ownerId) {
        return productRepository.findByOwnerId(ownerId);
    }

    public List<Product> all() { return productRepository.findAll(); }

    public Product update(Product p) {
        Product saved = productRepository.save(p);
        auditService.log("system", "UPDATE_PRODUCT:" + saved.getId());
        return saved;
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
        auditService.log("system", "DELETE_PRODUCT:" + id);
    }
}
