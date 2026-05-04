import type { UserType } from "./user";
import type { CartItemType } from "./cartItem";

export interface CartType {
  id?: string;
  user?: UserType;
  totalQuantity?: number;
  totalPrice?: number;
  cartItems?: CartItemType[];
}