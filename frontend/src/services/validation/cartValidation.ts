import type { CartItemTestRequest } from "../../types/cartItem";
import type { ValidateCartItemUnitTestResponse } from "../../types/test";

export const CartValidation = {
  validateCartItem({
    productId,
    productStock,
    productPrice,
    quantity,
  }: CartItemTestRequest): ValidateCartItemUnitTestResponse {
    if (quantity === null || quantity === undefined) {
      return {
        error: "QUANTITY_IS_NOT_NULL_OR_UNDEFINED",
        message: "Quantity is not null or undefined!",
      };
    }

    if (quantity <= 0) {
      return {
        error: "QUANTITY_MUST_BE_GREATER_THAN_ZERO",
        message: "Quantity must be greater than zero!",
      };
    }

    if (quantity > productStock!) {
      return { error: "INSUFFICIENT_STOCK", message: "Insufficient stock!" };
    }

    return { error: undefined, message: "No errors" };
  },
};
