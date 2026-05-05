import instance from "./customize";
import type { AxiosResponse } from "axios";
import type { CartType } from "../../types/cart";
import type {
  CartItemType,
  CartItemAddToCartRequest,
  CartItemUpdateQuantityRequest,
  CartItemRemoveFromCartRequest,
} from "../../types/cartItem";

export const CartApi = {
  feature: "carts",

  async getCartByUserId(userId: string): Promise<AxiosResponse<CartType>> {
    return instance.get<CartType>(`/${this.feature}/user/${userId}`);
  },

  async addToCart(
    userId: string,
    request: CartItemAddToCartRequest,
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
    request: CartItemUpdateQuantityRequest,
  ): Promise<AxiosResponse<CartItemType, any>> {
    return await instance.put<CartItemType>(`/${this.feature}/user/${userId}`, {
      productId: request.productId,
      quantity: request.quantity,
    });
  },

  async removeFromCart(
    userId: string,
    request: CartItemRemoveFromCartRequest,
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
};
