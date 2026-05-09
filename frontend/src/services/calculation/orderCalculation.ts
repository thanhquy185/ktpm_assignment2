import type { OrderItemRequest } from "../../types/orderItem";
import type { CouponType } from "../../types/coupon";
import type { CalculateOrderTotalUnitTestResponse } from "../../types/test";

export const OrderCalculation = {
  calculateOrderTotal({
    orderItems,
    shippingFee,
    coupon,
  }: {
    orderItems: OrderItemRequest[];
    shippingFee: number;
    coupon?: CouponType;
  }): CalculateOrderTotalUnitTestResponse {
    // - Tổng tiền sản phẩm
    const newSubtotal = orderItems.reduce(
      (sum, orderItem) => sum + orderItem.quantity * orderItem.price,
      0,
    );
    // - Tiền giảm giá
    let newDiscount = 0;
    if (coupon) {
      if (coupon.type === "Giảm tiền cố định") {
        newDiscount = coupon.discount;
      } else if (coupon.type === "Giảm theo phần trăm") {
        newDiscount = (1.0 * newSubtotal * coupon.discount) / 100;
      }
    }
    // - Tổng tiền đơn hàng (trước - sau giảm giá)
    const newTotalPriceBeforeDiscount = newSubtotal + shippingFee;
    const newTotalPriceAfterDiscount = newSubtotal + shippingFee - newDiscount;

    return {
      error: undefined,
      message: "Calculate order total is successful!",
      orderItems: orderItems,
      subtotal: newSubtotal,
      shippingFee: shippingFee,
      discount: newDiscount,
      totalPriceBeforeDiscount: newTotalPriceBeforeDiscount,
      totalPriceAfterDiscount: newTotalPriceAfterDiscount,
    };
  },
};
