import api from './api';

export const inventoryService = {
  async checkStock(productId: string, quantity: number): Promise<boolean> {
    const response = await api.get(`/inventory/check/${productId}`, {
      params: { quantity },
    });
    return response.data.available;
  },
};
