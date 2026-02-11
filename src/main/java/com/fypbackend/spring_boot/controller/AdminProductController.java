package com.fypbackend.spring_boot.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fypbackend.spring_boot.dto.admin.ProductAdminResponse;
import com.fypbackend.spring_boot.dto.admin.ProductRequest;
import com.fypbackend.spring_boot.entity.Product;
import com.fypbackend.spring_boot.service.AdminProductService;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/admin/products")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {

    private final AdminProductService productService;

    public AdminProductController(AdminProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductAdminResponse createProduct(@Valid @RequestBody ProductRequest request) {
        Product saved = productService.createProduct(request);
        return toResponse(saved);
    }

    @GetMapping
    public Page<ProductAdminResponse> listProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return productService.listProducts(page, size).map(this::toResponse);
    }

    @PatchMapping("/{productId}")
    public ProductAdminResponse updateProduct(@PathVariable Long productId, @Valid @RequestBody ProductRequest request) {
        Product saved = productService.updateProduct(productId, request);
        return toResponse(saved);
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
    }

    private ProductAdminResponse toResponse(Product product) {
        return new ProductAdminResponse(
                product.getId(),
                product.getCategory().getId(),
                product.getCategory().getCategoryName(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getUnitPrice(),
                product.getImageUrl(),
                product.isActive(),
                product.getUnitsInStock());
    }
}
