import instance from "./customize";
import type { AxiosResponse } from "axios";
import type { OrderRequest, OrderType } from "../types/order";

export const OrderService = {
  feature: "orders",

  async createOrder(request: OrderRequest): Promise<AxiosResponse<OrderType>> {
    return await instance.post(`/${this.feature}`, {
      userId: request.userId,
      couponId: request.couponId,
      shippingAddress: request.shippingAddress,
      shippingMethod: request.shippingMethod,
      shippingFee: request.shippingFee,
      paymentMethod: request.paymentMethod,
      orderItems: request.orderItems,
    });
  },

  async cancelOrder(orderId: string): Promise<AxiosResponse<OrderType>> {
    return await instance.delete(`/${this.feature}`, {
      data: { orderId: orderId },
    });
  },
};
