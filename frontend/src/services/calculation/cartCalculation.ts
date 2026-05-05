import type { CartItemTestRequest } from "../../types/cartItem";
import type { CouponType } from "../../types/coupon";
import type { CalculateCartTotalUnitTestResponse } from "../../types/test";

export const CartCalculation = {
  calculateCartTotal({
    cartItems,
    handleCartItem,
    handleType,
    coupon,
  }: {
    cartItems: CartItemTestRequest[];
    handleCartItem?: CartItemTestRequest;
    handleType?: "add" | "update" | "remove";
    coupon?: CouponType;
  }): CalculateCartTotalUnitTestResponse {
    let cartItemsUpdated = [...cartItems];

    if (handleCartItem && handleType) {
      // if (handleType === "add") {
      //   const cartItemExisted = cartItemsUpdated.find(
      //     (cartItem) => cartItem.productId === handleCartItem.productId,
      //   );
      //   if (cartItemExisted) {
      //     cartItemsUpdated = cartItemsUpdated?.map((cartItem) => {
      //       if (cartItemExisted.productId === cartItem.productId) {
      //         return {
      //           ...cartItem,
      //           quantity: cartItemExisted.quantity + cartItem.quantity,
      //         };
      //       }

      //       return cartItem;
      //     });
      //   } else {
      //     cartItemsUpdated.push(handleCartItem);
      //   }
      // }
      // if (handleType === "update") {
      //   cartItemsUpdated = cartItemsUpdated.map((cartItem) =>
      //     cartItem.productId === handleCartItem.productId
      //       ? { ...cartItem, quantity: handleCartItem.quantity }
      //       : cartItem,
      //   );
      // }
      if (handleType === "remove") {
        cartItemsUpdated = cartItemsUpdated.filter(
          (cartItem) => cartItem.productId !== handleCartItem.productId,
        );
      }
    }

    // - Tổng tiền sản phẩm
    const newSubtotal = cartItemsUpdated.reduce(
      (sum, cartItem) =>
        sum + (cartItem.quantity || 0) * (cartItem.productPrice || 0),
      0,
    );
    // - Tiền giảm giá
    let newDiscount = 0;
    if (coupon) {
      if (coupon.type === "Giảm tiền cố định") {
        newDiscount = coupon.discount || 0;
      } else if (coupon.type === "Giảm theo phần trăm") {
        newDiscount = (1.0 * newSubtotal * (coupon.discount || 0)) / 100;
      }
    }
    // - Tổng tiền đơn hàng
    const newSubtotalAfterDiscount = newSubtotal - newDiscount;

    return {
      error: undefined,
      message: "Calculate cart total is successful!",
      cartItems: cartItemsUpdated,
      subtotal: newSubtotal,
      discount: newDiscount,
      subtotalAfterDiscount: newSubtotalAfterDiscount,
    };
  },
};
