import instance from "./customize";
import type { AxiosResponse } from "axios";
import type { ProductType } from "../types/product";

export const ProductService = {
  feature: "products",

  async getAllProduct(): Promise<AxiosResponse<ProductType[], any>> {
    return await instance.get<ProductType[]>(`/${this.feature}`);
  },
};
