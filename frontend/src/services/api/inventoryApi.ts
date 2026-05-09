import instance from './customize';

export const inventoryService = {
  async checkStock(productId: string, quantity: number): Promise<boolean> {
    const response = await instance.get(`/inventory/check/${productId}`, {
      params: { quantity },
    });
    return response.data.available;
  },
};
