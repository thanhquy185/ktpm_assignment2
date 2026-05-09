import type { ProductType } from "./product";

export interface CartItemType {
  id?: string;
  product?: ProductType;
  quantity?: number;
}

export interface CartItemAddToCartRequest {
  productId: string;
  quantity: number;
}

export interface CartItemUpdateQuantityRequest {
  productId: string;
  quantity: number;
}

export interface CartItemRemoveFromCartRequest {
  productId: string;
}

export interface CartItemTestRequest {
  productId: string;
  productPrice: number;
  productStock?: number;
  quantity: number;
}
