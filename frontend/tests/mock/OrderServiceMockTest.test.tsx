import { describe, test, expect, vi, beforeEach } from "vitest";
import { OrderApi } from "../../src/services/api/orderApi";
import instance from "../../src/services/api/customize";

vi.mock("../../src/services/api/customize", () => {
  return {
    default: {
      post: vi.fn(),
    },
  };
});

describe("Order API Mock Tests", () => {
  const mockOrderRequest = {
    userId: "3edeba20-2be4-4725-9197-9b55d824942b",
    couponId: "coupon-456",
    shippingAddress: "123 Le Loi, HCM",
    shippingMethod: "FAST",
    shippingFee: 30000,
    paymentMethod: "COD",
    orderItems: [{ productId: "prod-1", quantity: 2, price: 50000 }],
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  test("TC9: Tạo đơn hàng nhưng người dùng không tồn tại", async () => {
    const mockError = { response: { data: { message: "USER_NOT_FOUND" } } };
    (instance.post as any).mockRejectedValueOnce(mockError);

    await expect(OrderApi.createOrder(mockOrderRequest)).rejects.toEqual(
      mockError,
    );
    expect(instance.post).toHaveBeenCalledTimes(1);
  });

  test("TC10: Tạo đơn hàng nhưng mã giảm giá không tồn tại", async () => {
    const mockError = { response: { data: { message: "COUPON_NOT_FOUND" } } };
    (instance.post as any).mockRejectedValueOnce(mockError);

    await expect(OrderApi.createOrder(mockOrderRequest)).rejects.toEqual(
      mockError,
    );
  });

  test("TC11: Tạo đơn hàng nhưng mã giảm giá hết hạn", async () => {
    const mockError = { response: { data: { message: "COUPON_EXPIRED" } } };
    (instance.post as any).mockRejectedValueOnce(mockError);

    await expect(OrderApi.createOrder(mockOrderRequest)).rejects.toEqual(
      mockError,
    );
  });
});
