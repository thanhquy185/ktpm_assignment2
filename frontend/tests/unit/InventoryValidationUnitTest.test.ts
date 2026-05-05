import { describe, test, expect } from "vitest";
import { InventoryValidation } from "../../src/services/validation/InventoryValidation";

describe("Inventory Validation Unit Tests", () => {
  test("TC1: Test danh sách sản phẩm đều có thể sử dụng", () => {});

  test("TC2: Test danh sách sẩn phẩm rỗng", () => {});

  test("TC3: Test quantity của 1 sản phẩm là null", () => {});

  test("TC4: Test quantity của 1 sản phẩm bé hơn hoặc bằng 0", () => {});

  test("TC5: Test stock của 1 sản phẩm là null", () => {});

  test("TC6: Test quantity lớn hơn stock trên cùng 1 sản phẩm", () => {});
});
