package com.shopcart.services;

import com.shopcart.entities.Inventory;
import com.shopcart.exceptions.InsufficientStock;
import com.shopcart.exceptions.InvalidInventoryQuantity;
import com.shopcart.exceptions.InventoryNotFound;
import com.shopcart.exceptions.ProductNotFoundInInventory;
import com.shopcart.repositories.InventoryRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public List<Inventory> getAllOrder() {
        return this.inventoryRepository.findAll();
    }

    public Inventory getOrderById(String id) {
        return this.inventoryRepository.findById(id)
                .orElseThrow(() -> new InventoryNotFound(id));
    }

    public Inventory getOrderByProductId(String productId) {
        return this.inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundInInventory(productId));
    }

    public boolean isAvailable(String productId, Long quantity) {
        Inventory inventory = this.getOrderByProductId(productId);
        if (quantity <= 0) {
            throw new InvalidInventoryQuantity();
        }

        return inventory.getStock() >= quantity;
    }

    public Inventory increaseStock(String productId, Long quantity) {
        Inventory inventory = this.getOrderByProductId(productId);
        if (quantity <= 0) {
            throw new InvalidInventoryQuantity();
        }
        inventory.setStock(inventory.getStock() + quantity);

        return this.inventoryRepository.save(inventory);
    }

    public Inventory decreaseStock(String productId, Long quantity) {
        Inventory inventory = this.getOrderByProductId(productId);
        if (quantity <= 0) {
            throw new InvalidInventoryQuantity();
        }
        if (inventory.getStock() < quantity) {
            throw new InsufficientStock(productId);
        }
        inventory.setStock(inventory.getStock() - quantity);

        return this.inventoryRepository.save(inventory);
    }
}
