package com.fypbackend.spring_boot.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fypbackend.spring_boot.dao.ProductCategoryRepository;
import com.fypbackend.spring_boot.dao.ProductRepository;
import com.fypbackend.spring_boot.dto.admin.ProductRequest;
import com.fypbackend.spring_boot.entity.Product;
import com.fypbackend.spring_boot.entity.ProductCategory;

@Service
public class AdminProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;

    public AdminProductService(ProductRepository productRepository,
            ProductCategoryRepository productCategoryRepository) {
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
    }

    public Product createProduct(ProductRequest request) {
        ProductCategory category = loadCategory(request.getCategoryId());
        Product product = new Product();
        applyRequestToProduct(request, product, category);
        return productRepository.save(product);
    }

    public Page<Product> listProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAll(pageable);
    }

    public Product updateProduct(Long productId, ProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        ProductCategory category = loadCategory(request.getCategoryId());
        applyRequestToProduct(request, product, category);
        return productRepository.save(product);
    }

    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        productRepository.deleteById(productId);
    }

    private ProductCategory loadCategory(Long categoryId) {
        if (categoryId != null) {
            return productCategoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        }

        return productCategoryRepository.findAll(PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No categories configured"));
    }

    private void applyRequestToProduct(ProductRequest request, Product product, ProductCategory category) {
        product.setCategory(category);
        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setUnitPrice(request.getUnitPrice());
        product.setImageUrl(request.getImageUrl());
        product.setActive(request.isActive());
        product.setUnitsInStock(request.getUnitsInStock());
    }
}
