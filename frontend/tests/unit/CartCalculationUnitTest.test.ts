import { describe, test, expect } from "vitest";
import { CartCalculation } from "../../src/services/calculation/CartCalculation";

describe("Cart Calculation Unit Tests", () => {
  test("TC1: Test giỏ hàng rỗng", () => {
    const result = CartCalculation.calculateCartTotal({ cartItems: [] });

    expect(result.error).toBe("CART_ITEMS_MUST_NOT_BE_EMPTY");
    expect(result.message).toBe("Cart items is empty!");
    expect(result.cartItems).toEqual([]);
    expect(result.subtotal).toBe(0);
    expect(result.discount).toBe(0);
    expect(result.subtotalAfterDiscount).toBe(0);
  });

  test("TC2: Test tính tổng giá đúng với nhiều sản phẩm", () => {
    const result = CartCalculation.calculateCartTotal({
      cartItems: [
        {
          productId: "PRO-001",
          productPrice: 20000,
          productStock: 5,
          quantity: 2,
        },
        {
          productId: "PRO-002",
          productPrice: 50000,
          productStock: 5,
          quantity: 1,
        },
        {
          productId: "PRO-003",
          productPrice: 10000,
          productStock: 6,
          quantity: 2,
        },
      ],
    });

    expect(result.error).toBe(undefined);
    expect(result.message).toBe("Calculate cart total is successful!");
    expect(result.subtotal).toBe(110000);
    expect(result.discount).toBe(0);
    expect(result.subtotalAfterDiscount).toBe(110000);
  });

  test("TC3: Test áp dụng mã giảm giá", () => {
    const result = CartCalculation.calculateCartTotal({
      cartItems: [
        {
          productId: "PRO-001",
          productPrice: 20000,
          productStock: 5,
          quantity: 2,
        },
        {
          productId: "PRO-002",
          productPrice: 50000,
          productStock: 5,
          quantity: 1,
        },
        {
          productId: "PRO-003",
          productPrice: 10000,
          productStock: 6,
          quantity: 2,
        },
      ],
      coupon: {
        id: "COP-001",
        name: "Khuyến mãi 10% tổng tiền sản phẩm",
        code: "SALE10%",
        type: "Giảm theo phần trăm",
        discount: 10,
      },
    });

    expect(result.error).toBe(undefined);
    expect(result.message).toBe("Calculate cart total is successful!");
    expect(result.subtotal).toBe(110000);
    expect(result.discount).toBe(11000);
    expect(result.subtotalAfterDiscount).toBe(99000);
  });

  test("TC4: Test tổng giá sau khi xóa sản phẩm", () => {
    const result = CartCalculation.calculateCartTotal({
      cartItems: [
        {
          productId: "PRO-001",
          productPrice: 20000,
          productStock: 5,
          quantity: 2,
        },
        {
          productId: "PRO-002",
          productPrice: 50000,
          productStock: 5,
          quantity: 1,
        },
        {
          productId: "PRO-003",
          productPrice: 10000,
          productStock: 6,
          quantity: 2,
        },
      ],
      handleCartItem: {
        productId: "PRO-002",
        productPrice: 10000,
        productStock: 6,
        quantity: 0,
      },
      handleType: "remove",
    });

    expect(result.error).toBe(undefined);
    expect(result.message).toBe("Calculate cart total is successful!");
    expect(result.subtotal).toBe(60000);
    expect(result.discount).toBe(0);
    expect(result.subtotalAfterDiscount).toBe(60000);
  });
});
