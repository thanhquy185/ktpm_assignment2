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

    const invalidItem = inventoryItems.find((item) => {
      if (item.quantity == null) return true;
      if (item.quantity <= 0) return true;
      if (item.productStock == null) return true;
      if (item.quantity > item.productStock) return true;

      return false;
    });
    if (invalidItem) {
      return {
        error: "INVENTORY_ITEM_IS_INVALID_OR_OUT_OF_STOCK",
        message: "Some inventory items are invalid or out of stock!",
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
