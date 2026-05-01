package com.shopcart.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopcart.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

}
