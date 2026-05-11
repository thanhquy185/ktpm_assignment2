import { describe, test, expect } from "vitest";
import { CartValidation } from "../../src/services/validation/cartValidation";
import type { CartItemTestRequest } from "../../src/types/cartItem";

describe("Cart Validation Unit Tests", () => {
  const baseCartItem: Omit<CartItemTestRequest, "quantity"> = {
    productId: "1",
    productStock: 10,
    productPrice: 100,
  };

  test("TC1: Test trường hợp quantity là null", () => {
    const result = CartValidation.validateCartItem({
      ...baseCartItem,
      quantity: null as any,
    });
    expect(result.error).toBe("QUANTITY_MUST_NOT_BE_NULL_OR_UNDEFINED");
  });

  test("TC2: Test trường hợp quantity là undefined", () => {
    const result = CartValidation.validateCartItem({
      ...baseCartItem,
      quantity: undefined as any,
    });
    expect(result.error).toBe("QUANTITY_MUST_NOT_BE_NULL_OR_UNDEFINED");
  });

  test("TC3: Test trường hợp quantity là số âm", () => {
    const result = CartValidation.validateCartItem({
      ...baseCartItem,
      quantity: -1,
    });
    expect(result.error).toBe("QUANTITY_MUST_BE_GREATER_THAN_ZERO");
  });

  test("TC4: Test trường hợp quantity bằng 0", () => {
    const result = CartValidation.validateCartItem({
      ...baseCartItem,
      quantity: 0,
    });
    expect(result.error).toBe("QUANTITY_MUST_BE_GREATER_THAN_ZERO");
  });

  test("TC5: Test trường hợp quantity vượt quá số lượng tồn kho (stock)", () => {
    const result = CartValidation.validateCartItem({
      ...baseCartItem,
      quantity: 11,
    });
    expect(result.error).toBe("INSUFFICIENT_STOCK");
  });

  test("TC6: Test trường hợp quantity hợp lệ", () => {
    const result = CartValidation.validateCartItem({
      ...baseCartItem,
      quantity: 5,
    });
    expect(result.error).toBeUndefined();
  });
});
