import type { CategoryType } from "./category";
import type { InventoryType } from "./inventory";

export interface ProductType {
  id?: string;
  image?: string;
  category?: CategoryType;
  inventory?: InventoryType;
  name?: string;
  price?: number;
  description?: string;
  status?: string;
}
