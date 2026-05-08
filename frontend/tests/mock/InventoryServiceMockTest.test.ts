import { describe, test, expect, vi, beforeEach } from "vitest";
import { inventoryService } from "../../src/services/api/inventoryApi";
import instance from "../../src/services/api/customize";

vi.mock("../../src/services/api/customize", () => {
  return {
    default: {
      get: vi.fn(),
    },
  };
});

describe("Inventory Service Mock Tests", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  test("TC7: Kiểm tra tồn kho nhưng kho hàng không tồn tại", async () => {
    const mockError = { response: { data: { message: "INVENTORY_NOT_FOUND" } } };
    (instance.get as any).mockRejectedValueOnce(mockError);

    await expect(inventoryService.checkStock("PROD-1", 1)).rejects.toEqual(mockError);
    expect(instance.get).toHaveBeenCalledTimes(1);
  });

  test("TC8: Kiểm tra tồn kho nhưng tồn kho của sản phẩm không tồn tại", async () => {
    const mockError = { response: { data: { message: "PRODUCT_INVENTORY_NOT_FOUND" } } };
    (instance.get as any).mockRejectedValueOnce(mockError);

    await expect(inventoryService.checkStock("PROD-1", 1)).rejects.toEqual(mockError);
  });

  test("TC9: Kiểm tra tồn kho nhưng tồn kho của sản phẩm không đủ", async () => {
    const mockError = { response: { data: { message: "INVENTORY_NOT_ENOUGH" } } };
    (instance.get as any).mockRejectedValueOnce(mockError);

    await expect(inventoryService.checkStock("PROD-1", 10)).rejects.toEqual(mockError);
  });
});