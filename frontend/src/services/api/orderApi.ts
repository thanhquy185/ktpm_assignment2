import instance from "./customize";
import type { AxiosResponse } from "axios";
import type {
  OrderCancelRequest,
  OrderCreateRequest,
  OrderType,
} from "../../types/order";

export const OrderApi = {
  feature: "orders",

  async getOrdersByUserId(userId: string): Promise<AxiosResponse<OrderType[]>> {
    return instance.get<OrderType[]>(`/${this.feature}/${userId}`);
  },

  async createOrder(
    request: OrderCreateRequest,
  ): Promise<AxiosResponse<OrderType>> {
    return await instance.post<OrderType>(`/${this.feature}`, {
      userId: request.userId,
      couponId: request.couponId,
      shippingAddress: request.shippingAddress,
      shippingMethod: request.shippingMethod,
      shippingFee: request.shippingFee,
      paymentMethod: request.paymentMethod,
      orderItems: request.orderItems,
    });
  },

  async cancelOrder(
    request: OrderCancelRequest,
  ): Promise<AxiosResponse<OrderType>> {
    return await instance.delete<OrderType>(`/${this.feature}`, {
      data: { orderId: request.orderId },
    });
  },
};
