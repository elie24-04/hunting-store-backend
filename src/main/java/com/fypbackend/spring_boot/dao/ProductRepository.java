package com.fypbackend.spring_boot.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.fypbackend.spring_boot.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findAllByCategoryId(@Param("id") Long categoryId, Pageable pageable);

    Page<Product> findByNameContaining(@Param("name") String name, Pageable pageable);

    List<Product> findByActiveTrueAndUnitsInStockLessThanEqualOrderByUnitsInStockAsc(int threshold);
}
