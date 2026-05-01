export interface Product {
  id: string;
  name: string;
  description: string;
  price: number;
  stock: number;
  status: 'ACTIVE' | 'INACTIVE' | 'OUT_OF_STOCK';
}

export interface CartItem {
  id: string;
  productId: string;
  productName: string;
  quantity: number;
  price: number;
  subtotal: number;
}

export interface CartItemRequest {
  productId: string;
  quantity: number;
}

export interface CartResponse {
  cartId: string;
  items: CartItem[];
  cartTotal: number;
  success: boolean;
  message?: string;
}

export interface OrderItem {
  productId: string;
  quantity: number;
  price: number;
}

export interface OrderRequest {
  userId: string;
  items: OrderItem[];
  couponCode?: string;
  shippingFee: number;
  shippingAddress: string;
  subtotal?: number;
  discount?: number;
}

export interface OrderResponse {
  orderId: string;
  userId: string;
  items: CartItem[];
  subtotal: number;
  discount: number;
  shippingFee: number;
  totalPrice: number;
  status: string;
  couponCode?: string;
  shippingAddress: string;
  createdAt: string;
}
