import type { CartItemTestRequest } from "../../types/cartItem";
import type { ValidateCartItemUnitTestResponse } from "../../types/test";

export const CartValidation = {
  validateCartItem({
    productId,
    productStock,
    productPrice,
    quantity,
  }: CartItemTestRequest): ValidateCartItemUnitTestResponse {
    if (productId === null || productId === undefined) {
      return {
        error: "PRODUCT_ID_MUST_NOT_BE_NULL_OR_UNDEFINED",
        message: "Product ID must not be null or undefined!",
      };
    }
    if (productStock === null || productStock === undefined) {
      return {
        error: "PRODUCT_STOCK_MUST_NOT_BE_NULL_OR_UNDEFINED",
        message: "Product stock must not be null or undefined!",
      };
    }
    if (productStock < 0) {
      return {
        error: "QUANTITY_MUST_BE_GREATER_THAN_OR_EQUAL_ZERO",
        message: "Quantity must be greater than or equal zero!",
      };
    }
    if (productPrice === null || productPrice === undefined) {
      return {
        error: "PRODUCT_PRICE_MUST_NOT_BE_NULL_OR_UNDEFINED",
        message: "Product price must not be null or undefined!",
      };
    }
    if (productPrice <= 0) {
      return {
        error: "PRODUCT_PRICE_MUST_BE_GREATER_THAN_ZERO",
        message: "Product price must be greater than zero!",
      };
    }
    if (quantity === null || quantity === undefined) {
      return {
        error: "QUANTITY_MUST_NOT_BE_NULL_OR_UNDEFINED",
        message: "Quantity must not be null or undefined!",
      };
    }
    if (quantity <= 0) {
      return {
        error: "QUANTITY_MUST_BE_GREATER_THAN_ZERO",
        message: "Quantity must be greater than zero!",
      };
    }
    if (quantity > productStock) {
      return { error: "INSUFFICIENT_STOCK", message: "Insufficient stock!" };
    }

    return { error: undefined, message: "No errors" };
  },
};
