package com.example.backend.modules.products.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.modules.products.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
}
