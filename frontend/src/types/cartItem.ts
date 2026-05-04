import type { ProductType } from "./product";

export interface CartItemType {
  id?: string;
  product?: ProductType;
  quantity?: number;
}

export interface CartItemRequest {
  productId?: string;
  productPrice?: number;
  productStock?: number;
  quantity?: number;
}
