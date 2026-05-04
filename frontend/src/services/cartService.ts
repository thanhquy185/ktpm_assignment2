import instance from "./customize";
import type { AxiosResponse } from "axios";
import type { CartItemRequest, CartItemType } from "../types/cartItem";
import type { CouponType } from "../types/coupon";
import type {
  CalculateCartTotalUnitTestResponse,
  ValidateCartItemUnitTestResponse,
} from "../types/test";

export const CartService = {
  feature: "carts",

  async addToCart(
    userId: string,
    request: CartItemRequest,
  ): Promise<AxiosResponse<CartItemType, any>> {
    return await instance.post<CartItemType>(
      `/${this.feature}/user/${userId}`,
      {
        productId: request.productId,
        quantity: request.quantity,
      },
    );
  },

  async updateQuantity(
    userId: string,
    request: CartItemRequest,
  ): Promise<AxiosResponse<CartItemType, any>> {
    return await instance.put<CartItemType>(`/${this.feature}/user/${userId}`, {
      productId: request.productId,
      quantity: request.quantity,
    });
  },

  async removeToCart(
    userId: string,
    request: CartItemRequest,
  ): Promise<AxiosResponse<CartItemType, any>> {
    return await instance.delete<CartItemType>(
      `/${this.feature}/user/${userId}`,
      {
        data: {
          productId: request.productId,
        },
      },
    );
  },

  validateCartItem(param: CartItemRequest): ValidateCartItemUnitTestResponse {
    if (!param.quantity) {
      return {
        error: "QUANTITY_IS_NOT_NULL_OR_UNDEFINED",
        message: "Quantity is not null or undefined!",
      };
    }

    if (param.quantity <= 0) {
      return {
        error: "QUANTITY_MUST_BE_GREATER_THAN_ZERO",
        message: "Quantity must be greater than zero!",
      };
    }

    if (param.quantity > param.productStock!) {
      return { error: "INSUFFICIENT_STOCK", message: "Insufficient stock!" };
    }

    return { error: undefined, message: "No errors" };
  },

  calculateCartTotal({
    cartItems,
    handleCartItem,
    handleType,
    coupon,
  }: {
    cartItems?: CartItemRequest[];
    handleCartItem?: CartItemType;
    handleType?: "add" | "update" | "remove";
    coupon?: CouponType;
  }): CalculateCartTotalUnitTestResponse {
    console.log(cartItems);
    let discount = 0,
      newTotalPriceBeforeDiscount = 0,
      newTotalPriceAfterDiscount = 0;

    return {
      discount,
      newTotalPriceBeforeDiscount,
      newTotalPriceAfterDiscount,
    };
  },
};
