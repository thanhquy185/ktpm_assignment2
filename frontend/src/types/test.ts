import type { CartItemRequest } from "./cartItem";
import type { OrderItemRequest } from "./orderItem";

export interface ValidateCartItemUnitTestResponse {
  error?: string;
  message: string;
}

export interface CalculateCartTotalUnitTestResponse {
  error?: string;
  message: string;
  cartItems: CartItemRequest[];
  subtotal: number;
  discount: number;
  subtotalAfterDiscount: number;
}

export interface CalculateOrderTotalUnitTestResponse {
  error?: string;
  message: string;
  orderItems: OrderItemRequest[];
  subtotal: number;
  shippingFee: number;
  discount: number;
  totalPriceBeforeDiscount: number;
  totalPriceAfterDiscount: number;
}

export interface CheckInventoryAvailabilityUnitTestResponse {
  error?: string;
  message: string;
  available: boolean;
}
