import { describe, test, expect } from "vitest";
import { OrderCalculation } from "../../src/services/calculation/OrderCalculation";

describe("Order Calculation Unit Tests", () => {
  test("TC1: Test tính tổng giá trước giảm giá", () => {
    const result = OrderCalculation.calculateOrderTotal({
      orderItems: [{ productId: "P00001", quantity: 2, price: 50000 }],
      shippingFee: 20000,
      coupon: {
        id: "C00001",
        name: "Giảm giá 50000đ cho đơn hàng!",
        code: "SALE50K",
        type: "Giảm tiền cố định",
        discount: 50000,
      },
    });

    expect(result.totalPriceBeforeDiscount).toBe(120000);
  });

  test("TC2: Test áp dụng coupon giảm % (ví dụ: 10%, 20%)", () => {
    const result = OrderCalculation.calculateOrderTotal({
      orderItems: [{ productId: "P00001", quantity: 2, price: 50000 }],
      shippingFee: 20000,
      coupon: {
        id: "C00001",
        name: "Giảm giá 10% cho đơn hàng!",
        code: "SALE10",
        type: "Giảm theo phần trăm",
        discount: 10,
      },
    });

    expect(result.totalPriceAfterDiscount).toBe(110000);
  });

  test("TC3: Test áp dụng coupon giảm số tiền cố định", () => {
    const result = OrderCalculation.calculateOrderTotal({
      orderItems: [{ productId: "P00001", quantity: 2, price: 50000 }],
      shippingFee: 20000,
      coupon: {
        id: "C00001",
        name: "Giảm giá 50000đ cho đơn hàng!",
        code: "SALE50K",
        type: "Giảm tiền cố định",
        discount: 50000,
      },
    });

    expect(result.totalPriceAfterDiscount).toBe(70000);
  });

  test("TC4: Test tính phí vận chuyển", () => {
    const result = OrderCalculation.calculateOrderTotal({
      orderItems: [{ productId: "P00001", quantity: 2, price: 50000 }],
      shippingFee: 20000,
      coupon: {
        id: "C00001",
        name: "Giảm giá 50000đ cho đơn hàng!",
        code: "SALE50K",
        type: "Giảm tiền cố định",
        discount: 50000,
      },
    });

    expect(result.shippingFee).toBe(20000);
  });

  test("TC5: Test tổng cuối cùng (subtotal + shipping - discount)", () => {
    const result = OrderCalculation.calculateOrderTotal({
      orderItems: [{ productId: "P00001", quantity: 2, price: 50000 }],
      shippingFee: 20000,
      coupon: {
        id: "C00001",
        name: "Giảm giá 50000đ cho đơn hàng!",
        code: "SALE50K",
        type: "Giảm tiền cố định",
        discount: 50000,
      },
    });

    expect(result.totalPriceAfterDiscount).toBe(70000);
  });
});
