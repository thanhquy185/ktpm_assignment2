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
  test("TC1: Kiểm tra tồn kho thành công", async () => {
    (instance.get as any).mockResolvedValueOnce({
      data: {
        available: true,
      },
    });

    const result = await inventoryService.checkStock("PROD-1", 2);

    expect(result).toBe(true);

    expect(instance.get).toHaveBeenCalledTimes(1);

    expect(instance.get).toHaveBeenCalledWith("/inventory/check/PROD-1", {
      params: {
        quantity: 2,
      },
    });
  });

  test("TC2: Kiểm tra tồn kho nhưng có 1 sản phẩm có số lượng cần so sánh bé hơn 0", async () => {
    const mockError = {
      response: {
        data: {
          message: "QUANTITY_MUST_BE_GREATER_THAN_ZERO",
        },
      },
    };

    (instance.get as any).mockRejectedValueOnce(mockError);

    await expect(inventoryService.checkStock("PROD-1", -1)).rejects.toEqual(
      mockError,
    );

    expect(instance.get).toHaveBeenCalledTimes(1);

    expect(instance.get).toHaveBeenCalledWith("/inventory/check/PROD-1", {
      params: {
        quantity: -1,
      },
    });
  });

  test("TC3: Kiểm tra tồn kho nhưng có 1 sản phẩm có số lượng cần so sánh bằng 0", async () => {
    const mockError = {
      response: {
        data: {
          message: "QUANTITY_MUST_BE_GREATER_THAN_ZERO",
        },
      },
    };

    (instance.get as any).mockRejectedValueOnce(mockError);

    await expect(inventoryService.checkStock("PROD-1", 0)).rejects.toEqual(
      mockError,
    );

    expect(instance.get).toHaveBeenCalledTimes(1);

    expect(instance.get).toHaveBeenCalledWith("/inventory/check/PROD-1", {
      params: {
        quantity: 0,
      },
    });
  });

  test("TC4: Kiểm tra tồn kho nhưng có 1 sản phẩm không tồn tại trong tồn kho", async () => {
    const mockError = {
      response: {
        data: {
          message: "PRODUCT_NOT_FOUND_IN_INVENTORY",
        },
      },
    };

    (instance.get as any).mockRejectedValueOnce(mockError);

    await expect(inventoryService.checkStock("PROD-999", 1)).rejects.toEqual(
      mockError,
    );

    expect(instance.get).toHaveBeenCalledTimes(1);

    expect(instance.get).toHaveBeenCalledWith("/inventory/check/PROD-999", {
      params: {
        quantity: 1,
      },
    });
  });
  test("TC7: Kiểm tra tồn kho nhưng kho hàng không tồn tại", async () => {
    const mockError = {
      response: { data: { message: "INVENTORY_NOT_FOUND" } },
    };
    (instance.get as any).mockRejectedValueOnce(mockError);

    await expect(inventoryService.checkStock("PROD-1", 1)).rejects.toEqual(
      mockError,
    );
    expect(instance.get).toHaveBeenCalledTimes(1);
  });

  test("TC8: Kiểm tra tồn kho nhưng tồn kho của sản phẩm không tồn tại", async () => {
    const mockError = {
      response: { data: { message: "PRODUCT_INVENTORY_NOT_FOUND" } },
    };
    (instance.get as any).mockRejectedValueOnce(mockError);

    await expect(inventoryService.checkStock("PROD-1", 1)).rejects.toEqual(
      mockError,
    );
  });

  test("TC9: Kiểm tra tồn kho nhưng tồn kho của sản phẩm không đủ", async () => {
    const mockError = {
      response: { data: { message: "INVENTORY_NOT_ENOUGH" } },
    };
    (instance.get as any).mockRejectedValueOnce(mockError);

    await expect(inventoryService.checkStock("PROD-1", 10)).rejects.toEqual(
      mockError,
    );
  });
});
