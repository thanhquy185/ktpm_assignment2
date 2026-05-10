import type { InventoryCheckStockRequest } from "../../types/inventory";
import type { CheckInventoryAvailabilityUnitTestResponse } from "../../types/test";

export const InventoryValidation = {
  checkInventoryAvailability({
    inventoryItems,
  }: {
    inventoryItems: InventoryCheckStockRequest[];
  }): CheckInventoryAvailabilityUnitTestResponse {
    if (!inventoryItems || inventoryItems.length === 0) {
      return {
        error: "INVENTORY_ITEMS_ARE_EMPTY",
        message: "Inventory items are empty!",
        available: false,
      };
    }

    // -
    const inventoryItemQuantityNullOrUndefined = inventoryItems.some(
      (inventoryItem) =>
        inventoryItem.quantity == null || inventoryItem.quantity == undefined,
    );
    if (inventoryItemQuantityNullOrUndefined) {
      return {
        error: "INVENTORY_ITEM_QUANTITY_MUST_NOT_BE_NULL_OR_UNDEFINED",
        message: "Inventory item quantity must not be null or undefined!",
        available: false,
      };
    }
    // -
    const inventoryItemQuantityLessThanOrEqualZero = inventoryItems.some(
      (inventoryItem) => inventoryItem.quantity <= 0,
    );
    if (inventoryItemQuantityLessThanOrEqualZero) {
      return {
        error: "INVENTORY_ITEM_QUANTITY_MUST_BE_GREATER_THAN_ZERO",
        message: "Inventory item quantity must be greater than zero!",
        available: false,
      };
    }
    // -
    const inventoryItemProductStockNullOrUndefined = inventoryItems.some(
      (inventoryItem) =>
        inventoryItem.productStock == null ||
        inventoryItem.productStock == undefined,
    );
    if (inventoryItemProductStockNullOrUndefined) {
      return {
        error: "INVENTORY_ITEM_PRODUCT_STOCK_MUST_NOT_BE_NULL_OR_UNDEFINED",
        message: "Inventory item product stock must not be null or undefined!",
        available: false,
      };
    }
    // -
    const inventoryItemProductStockLessThanOrEqualZero = inventoryItems.some(
      (inventoryItem) => inventoryItem.productStock < 0,
    );
    if (inventoryItemProductStockLessThanOrEqualZero) {
      return {
        error:
          "INVENTORY_ITEM_PRODUCT_STOCK_MUST_BE_GREATER_THAN_OR_EQUAL_ZERO",
        message:
          "Inventory item product stock must be greater than or eual zero!",
        available: false,
      };
    }
    // -
    const inventoryItemInsufficientStock = inventoryItems.some(
      (inventoryItem) => inventoryItem.quantity > inventoryItem.productStock,
    );
    if (inventoryItemInsufficientStock) {
      return {
        error: "INVENTORY_ITEM_INSUFFICIENT_STOCK",
        message: "Inventory item insufficient stock!",
        available: false,
      };
    }

    return {
      error: undefined,
      message: "Inventory items are available!",
      available: true,
    };
  },
};
