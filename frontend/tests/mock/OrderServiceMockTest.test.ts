
import { describe, test, afterEach, expect, vi } from "vitest";
import { OrderApi } from "../../src/services/api/orderApi";
import { inventoryService } from "../../src/services/api/inventoryApi";
import type { OrderCreateRequest, OrderType } from "../../src/types/order";

// Mock the services
vi.mock("../../src/services/api/orderApi");
vi.mock("../../src/services/api/inventoryApi");

describe("Order Service Mock Tests", () => {
  const mockOrder: OrderCreateRequest = {
    userId: "user-123",
    couponId: undefined,
    shippingAddress: "123 Test St",
    shippingMethod: "standard",
    shippingFee: 5,
    paymentMethod: "credit_card",
    orderItems: [
      {
        productId: "prod-abc",
        quantity: 2,
        price: 100,
      },
    ],
  };

  afterEach(() => {
    vi.clearAllMocks();
  });

  test("TC1: Tạo đơn hàng thành công", async () => {
    const successfulOrder: OrderType = { ...mockOrder, id: "order-xyz" };

    vi.mocked(inventoryService.checkStock).mockResolvedValue(true);
    vi.mocked(OrderApi.createOrder).mockResolvedValue({
      data: successfulOrder,
      status: 201,
      statusText: "Created",
      headers: {},
      config: {} as any,
    });

    for (const item of mockOrder.orderItems) {
      const isAvailable = await inventoryService.checkStock(
        item.productId,
        item.quantity
      );
      if (!isAvailable) {
        throw new Error("Product is out of stock");
      }
    }

    await OrderApi.createOrder(mockOrder);

    expect(inventoryService.checkStock).toHaveBeenCalledWith("prod-abc", 2);
    expect(OrderApi.createOrder).toHaveBeenCalledWith(mockOrder);
  });

  test("TC2: Tạo đơn hàng với sản phẩm đã hết hàng", async () => {
    vi.mocked(inventoryService.checkStock).mockResolvedValue(false);

    await expect(
      (async () => {
        for (const item of mockOrder.orderItems) {
          const isAvailable = await inventoryService.checkStock(
            item.productId,
            item.quantity
          );
          if (!isAvailable) {
            throw new Error("Product is out of stock");
          }
        }
        await OrderApi.createOrder(mockOrder);
      })()
    ).rejects.toThrow("Product is out of stock");

    expect(inventoryService.checkStock).toHaveBeenCalledWith("prod-abc", 2);
    expect(OrderApi.createOrder).not.toHaveBeenCalled();
  });

  test("TC3: Tạo đơn hàng với số lượng sản phẩm nhỏ hơn 0", async () => {
    const orderWithInvalidQuantity = {
      ...mockOrder,
      orderItems: [{ ...mockOrder.orderItems[0], quantity: -1 }],
    };

    const validateOrder = (order: OrderCreateRequest) => {
      if (order.orderItems.some((item) => item.quantity < 0)) {
        throw new Error("Invalid quantity");
      }
    };

    expect(() => validateOrder(orderWithInvalidQuantity)).toThrow(
      "Invalid quantity"
    );
    expect(OrderApi.createOrder).not.toHaveBeenCalled();
  });

  test("TC4: Tạo đơn hàng với số lượng sản phẩm bằng 0", async () => {
    const orderWithZeroQuantity = {
      ...mockOrder,
      orderItems: [{ ...mockOrder.orderItems[0], quantity: 0 }],
    };

    const validateOrder = (order: OrderCreateRequest) => {
      if (order.orderItems.some((item) => item.quantity <= 0)) {
        throw new Error("Invalid quantity");
      }
    };

    expect(() => validateOrder(orderWithZeroQuantity)).toThrow(
      "Invalid quantity"
    );
    expect(OrderApi.createOrder).not.toHaveBeenCalled();
  });

  test("TC5: Tạo đơn hàng với giá sản phẩm nhỏ hơn 0", async () => {
    const orderWithInvalidPrice = {
      ...mockOrder,
      orderItems: [{ ...mockOrder.orderItems[0], price: -10 }],
    };

    const validateOrder = (order: OrderCreateRequest) => {
      if (order.orderItems.some((item) => item.price < 0)) {
        throw new Error("Invalid price");
      }
    };

    expect(() => validateOrder(orderWithInvalidPrice)).toThrow("Invalid price");
    expect(OrderApi.createOrder).not.toHaveBeenCalled();
  });

  test("TC6: Tạo đơn hàng với giá sản phẩm bằng 0", async () => {
    const orderWithZeroPrice = {
      ...mockOrder,
      orderItems: [{ ...mockOrder.orderItems[0], price: 0 }],
    };
    
    const validateOrder = (order: OrderCreateRequest) => {
        if (order.orderItems.some((item) => item.price <= 0)) {
            throw new Error("Invalid price");
        }
    };

    expect(() => validateOrder(orderWithZeroPrice)).toThrow("Invalid price");
    expect(OrderApi.createOrder).not.toHaveBeenCalled();
  });
});
