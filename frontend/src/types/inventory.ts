export interface InventoryType {
  id?: string;
  stock?: number;
}

export interface InventoryCheckStockRequest {
  productId: string;
  productStock: number;
  quantity: number;
}
