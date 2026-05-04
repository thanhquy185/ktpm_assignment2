import instance from "./customize";
import type { AxiosResponse } from "axios";
import type { CouponType } from "../types/coupon";

export const CouponService = {
  async getCouponByCode(code: string): Promise<AxiosResponse<CouponType, any>> {
    return await instance.get<CouponType>(`/coupons/code/${code}`);
  },
};
