import instance from "./customize";
import type { AxiosResponse } from "axios";
import type { CouponType } from "../../types/coupon";

export const CouponApi = {
  feature: "coupons",

  async getCouponByCode(code: string): Promise<AxiosResponse<CouponType, any>> {
    return await instance.get<CouponType>(
      `/${this.feature}/code/${encodeURIComponent(code)}`,
    );
  },

  calculateDiscount(type: string, discount: number, subtotal: number) {
    if (type === "Giảm tiền cố định") {
      return discount;
    } else if (type === "Giảm theo phần trăm") {
      return (1.0 * subtotal * discount) / 100;
    }
  },
};
