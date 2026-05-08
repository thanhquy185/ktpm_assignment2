import { describe, test, expect } from "vitest";
import { InventoryValidation } from "../../src/services/validation/inventoryValidation";

describe("Inventory Validation Unit Tests", () => {
  test("TC1: Test danh sách sản phẩm đều có thể sử dụng", () => {
    const data = {
      inventoryItems: [
        { productId: "PROD-1", quantity: 2, productStock: 5 },
        { productId: "PROD-2", quantity: 10, productStock: 20 },
      ],
    };
    const result = InventoryValidation.checkInventoryAvailability(data);
    expect(result.available).toBe(true);
    expect(result.error).toBeUndefined();
  });

  test("TC2: Test danh sách sẩn phẩm rỗng", () => {
    const data = {
      inventoryItems: [],
    };
    const result = InventoryValidation.checkInventoryAvailability(data);
    expect(result.available).toBe(false);
    expect(result.error).toBe("INVENTORY_ITEMS_ARE_EMPTY");
  });

  test("TC3: Test quantity của 1 sản phẩm là null", () => {
    const data = {
      inventoryItems: [{ productId: "PROD-1", quantity: null as any, productStock: 5 }],
    };
    const result = InventoryValidation.checkInventoryAvailability(data);
    expect(result.available).toBe(false);
    expect(result.error).toBe("INVENTORY_ITEM_IS_INVALID_OR_OUT_OF_STOCK");
  });

  test("TC4: Test quantity của 1 sản phẩm bé hơn 0", () => {
    const data = {
      inventoryItems: [{ productId: "PROD-1", quantity: -2, productStock: 5 }],
    };
    const result = InventoryValidation.checkInventoryAvailability(data);
    expect(result.available).toBe(false);
  });

  test("TC5: Test quantity của 1 sản phẩm bằng 0", () => {
    const data = {
      inventoryItems: [{ productId: "PROD-1", quantity: 0, productStock: 5 }],
    };
    const result = InventoryValidation.checkInventoryAvailability(data);
    expect(result.available).toBe(false);
  });

  test("TC6: Test stock của 1 sản phẩm là null", () => {
    const data = {
      inventoryItems: [{ productId: "PROD-1", quantity: 2, productStock: null as any }],
    };
    const result = InventoryValidation.checkInventoryAvailability(data);
    expect(result.available).toBe(false);
  });

  test("TC7: Test quantity lớn hơn stock trên cùng 1 sản phẩm", () => {
    const data = {
      inventoryItems: [{ productId: "PROD-1", quantity: 6, productStock: 5 }],
    };
    const result = InventoryValidation.checkInventoryAvailability(data);
    expect(result.available).toBe(false);
  });
});