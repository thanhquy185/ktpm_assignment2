import api from './api';
import { OrderRequest, OrderResponse } from '../types';

export const orderService = {
  async createOrder(request: OrderRequest): Promise<OrderResponse> {
    const response = await api.post('/orders', request);
    return response.data;
  },

  async getOrderById(orderId: string): Promise<OrderResponse> {
    const response = await api.get(`/orders/${orderId}`);
    return response.data;
  },

  async getOrdersByUserId(userId: string): Promise<OrderResponse[]> {
    const response = await api.get(`/orders/user/${userId}`);
    return response.data;
  },

  async cancelOrder(orderId: string): Promise<OrderResponse> {
    const response = await api.put(`/orders/${orderId}/cancel`);
    return response.data;
  },
};
