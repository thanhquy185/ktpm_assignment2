import React from "react";
import { describe, test, expect, vi, beforeEach } from "vitest";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { toast } from "react-toastify";
import PriceCalculatorComponent from "../../src/components/PriceCalculator";

// mocks

describe("Price Calculator Component Integration Tests", () => {
  test("TC1: Tính tổng tiền đơn hàng thành công", () => {
    render(<PriceCalculatorComponent subtotal={100000} shippingFee={30000} discount={10000} />);
    const totalEl = screen.getByTestId("price-calculator-total-price");

    expect(totalEl).toBeDefined();
    expect(totalEl.textContent).toContain("120"); 
  });
});
