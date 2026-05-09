import type { ProductType } from "./product";

export interface OrderItemType {
  id?: string;
  product?: ProductType;
  quantity?: number;
  price?: number;
}

export interface OrderItemRequest {
  productId: string;
  quantity: number;
  price: number;
}
