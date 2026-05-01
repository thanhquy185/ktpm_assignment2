package com.shopcart.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.shopcart.entities.Product;
import com.shopcart.exceptions.ProductNotFound;
import com.shopcart.repositories.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<Product> getAllProduct() {
        return this.productRepository.findAll();
    }

    public Product getProductById(UUID id) {
        return this.productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFound(id));
    }
}
