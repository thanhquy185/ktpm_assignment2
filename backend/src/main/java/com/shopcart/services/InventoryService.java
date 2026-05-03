package com.shopcart.services;

import com.shopcart.dtos.request.InventoryCheckStockRequest;
import com.shopcart.entities.Inventory;
import com.shopcart.entities.Product;
import com.shopcart.exceptions.InsufficientStock;
import com.shopcart.exceptions.InvalidInventoryQuantity;
import com.shopcart.exceptions.InventoryItemQuantityGreaterThanZero;
import com.shopcart.exceptions.InventoryNotFound;
import com.shopcart.exceptions.ProductNotFoundInInventory;
import com.shopcart.repositories.InventoryRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class InventoryService {
    private final ProductService productService;
    private final InventoryRepository inventoryRepository;

    public List<Inventory> getAllInventory() {
        return this.inventoryRepository.findAll();
    }

    public Inventory getInventoryById(UUID id) {
        return this.inventoryRepository.findById(id)
                .orElseThrow(() -> new InventoryNotFound(id));
    }

    public Inventory getInventoryByProductId(UUID productId) {
        return this.inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundInInventory(productId));
    }

    public Boolean checkStock(InventoryCheckStockRequest request) {
        return request.getInventoryItems().stream().allMatch(inventoryItem -> {
            if (inventoryItem.getQuantity() <= 0) {
                throw new InventoryItemQuantityGreaterThanZero();
            }

            UUID productId = UUID.fromString(inventoryItem.getProductId());
            Product product = this.productService.getProductById(productId);
            if (product.getInventory() == null) {
                throw new ProductNotFoundInInventory(productId);
            }

            return inventoryItem.getQuantity() <= product.getInventory().getStock();
        });
    }

    public Inventory increaseStock(UUID productId, Long quantity) {
        if (quantity <= 0) {
            throw new InvalidInventoryQuantity();
        }

        Inventory inventory = this.getInventoryByProductId(productId);
        inventory.setStock(inventory.getStock() + quantity);

        return this.inventoryRepository.save(inventory);
    }

    public Inventory decreaseStock(UUID productId, Long quantity) {
        if (quantity <= 0) {
            throw new InvalidInventoryQuantity();
        }

        Inventory inventory = this.getInventoryByProductId(productId);
        if (inventory.getStock() < quantity) {
            throw new InsufficientStock(productId);
        }
        inventory.setStock(inventory.getStock() - quantity);

        return this.inventoryRepository.save(inventory);
    }
}
