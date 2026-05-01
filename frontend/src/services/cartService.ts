import api from './api';
import { CartResponse, CartItemRequest } from '../types';

export const cartService = {
  async addToCart(request: CartItemRequest): Promise<CartResponse> {
    const response = await api.post('/cart/add', request);
    return response.data;
  },

  async getCart(): Promise<CartResponse> {
    const response = await api.get('/cart');
    return response.data;
  },

  async removeFromCart(productId: string): Promise<CartResponse> {
    const response = await api.delete(`/cart/remove/${productId}`);
    return response.data;
  },

  async updateQuantity(productId: string, quantity: number): Promise<CartResponse> {
    const response = await api.put(`/cart/update/${productId}`, null, {
      params: { quantity },
    });
    return response.data;
  },
};
