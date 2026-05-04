import type { UserType } from "./user";
import type { CouponType } from "./coupon";
import type { OrderItemRequest, OrderItemType } from "./orderItem";

export interface OrderType {
  id?: string;
  user?: UserType;
  coupon?: CouponType;
  createdAt?: string;
  shippingAddress?: string;
  subtotal?: number;
  discount?: number;
  shippingFee?: number;
  totalPrice?: number;
  status?: string;
  orderItems?: OrderItemType[];
}

export interface OrderRequest {
  userId?: string;
  couponId?: string;
  shippingAddress?: string;
  shippingMethod?: string;
  shippingFee?: number;
  paymentMethod?: string;
  orderItems?: OrderItemRequest[];
}
