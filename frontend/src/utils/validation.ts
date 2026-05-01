/**
 * Validate cart item before adding to cart
 */
export const validateCartItem = (
  quantity: number | null | undefined,
  stock: number
): { error?: string } => {
  if (quantity === null || quantity === undefined) {
    return { error: 'Số lượng là bắt buộc' };
  }

  if (typeof quantity !== 'number' || !Number.isInteger(quantity)) {
    return { error: 'Số lượng phải là số nguyên' };
  }

  if (quantity <= 0) {
    return { error: 'Số lượng phải lớn hơn 0' };
  }

  if (quantity > stock) {
    return { error: 'Số lượng vượt quá tồn kho' };
  }

  return {};
};

/**
 * Validate order price calculation
 */
export const validateOrderPrice = (
  subtotal: number,
  discount: number = 0,
  shippingFee: number = 0
): { error?: string } => {
  if (subtotal < 0) {
    return { error: 'Tổng giá không thể âm' };
  }

  if (discount < 0) {
    return { error: 'Giảm giá không thể âm' };
  }

  if (shippingFee < 0) {
    return { error: 'Phí vận chuyển không thể âm' };
  }

  if (discount > subtotal) {
    return { error: 'Giảm giá không thể lớn hơn tổng giá' };
  }

  return {};
};

/**
 * Validate inventory before checkout
 */
export const validateInventoryAvailability = (
  requestedQuantity: number,
  availableStock: number
): { available: boolean; message?: string } => {
  if (requestedQuantity > availableStock) {
    return {
      available: false,
      message: `Tồn kho không đủ. Chỉ còn ${availableStock} sản phẩm.`,
    };
  }

  return { available: true };
};
