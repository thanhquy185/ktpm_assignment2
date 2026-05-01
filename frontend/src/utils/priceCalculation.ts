/**
 * Calculate total price including discount and shipping
 */
export const calculateOrderPrice = (
  items: Array<{ price: number; quantity: number }>,
  couponCode?: string | null,
  shippingFee: number = 0
): {
  subtotal: number;
  discount: number;
  total: number;
  shipping: number;
} => {
  const subtotal = items.reduce((sum, item) => sum + item.price * item.quantity, 0);

  // Apply coupon discount (simplified logic)
  let discount = 0;
  if (couponCode === 'SALE10') {
    discount = subtotal * 0.1; // 10% discount
  } else if (couponCode === 'SALE20') {
    discount = subtotal * 0.2; // 20% discount
  }

  const total = subtotal - discount + shippingFee;

  return {
    subtotal,
    discount,
    shipping: shippingFee,
    total,
  };
};

/**
 * Format price to Vietnamese currency
 */
export const formatPrice = (price: number): string => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
  }).format(price);
};

/**
 * Check inventory availability
 */
export const checkInventoryAvailability = (
  items: Array<{ quantity: number; stock: number }>
): boolean => {
  return items.every((item) => item.quantity <= item.stock);
};
