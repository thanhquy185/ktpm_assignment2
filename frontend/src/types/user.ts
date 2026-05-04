import type { CartType } from "./cart";
import type { OrderType } from "./order";

export interface UserType {
  id?: string;
  role?: string;
  username?: string;
  password?: string;
  cart?: CartType;
  orders?: OrderType[];
}

export interface UserRequest {
  username?: string;
  password?: string;
}
