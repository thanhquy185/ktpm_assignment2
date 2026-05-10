import React from "react";
import { describe, test, expect, beforeEach, vi } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import PriceCalculatorComponent from "../../src/components/PriceCalculator";
import { formatPrice } from "../../src/utils/priceCalculation";

beforeEach(() => {
  vi.clearAllMocks();
});

describe("Price Calculator Component Integration Tests", () => {
  test("TC1: Tiền hàng là số âm", async () => {
    const { rerender } = render(
      <PriceCalculatorComponent subtotal={100000} shippingFee={30000} discount={0} />
    );

    rerender(
      <PriceCalculatorComponent subtotal={-10000} shippingFee={30000} discount={0} />
    );

    const totalEl = screen.getByTestId("checkout-summary-total-price");
    await waitFor(() => {
      expect(totalEl.textContent).toBe(formatPrice(20000));
    });
  });

  test("TC2: Phí ship là số âm", async () => {
    const { rerender } = render(
      <PriceCalculatorComponent subtotal={100000} shippingFee={30000} discount={0} />
    );

    rerender(
      <PriceCalculatorComponent subtotal={100000} shippingFee={-5000} discount={0} />
    );

    const totalEl = screen.getByTestId("checkout-summary-total-price");
    await waitFor(() => {
      expect(totalEl.textContent).toBe(formatPrice(95000));
    });
  });

  test("TC3: Giảm giá là số âm", async () => {
    const { rerender } = render(
      <PriceCalculatorComponent subtotal={100000} shippingFee={30000} discount={0} />
    );

    rerender(
      <PriceCalculatorComponent subtotal={100000} shippingFee={30000} discount={-10000} />
    );

    const totalEl = screen.getByTestId("checkout-summary-total-price");
    await waitFor(() => {
      // 100k + 30k - (-10k) = 140k
      expect(totalEl.textContent).toBe(formatPrice(140000));
    });
  });

  test("TC4: Tiền hàng, phí ship và giảm giá đều hợp lệ", async () => {
    render(
      <PriceCalculatorComponent subtotal={100000} shippingFee={30000} discount={10000} />
    );

    const totalEl = screen.getByTestId("checkout-summary-total-price");
    await waitFor(() => {
      expect(totalEl.textContent).toBe(formatPrice(120000));
    });

    expect(screen.queryByTestId("checkout-summary-total-price-negative-inform")).toBeNull();
  });

  test("TC5: Tính tổng tiền đơn hàng thành công (Cập nhật Real-time)", async () => {
    const { rerender } = render(
      <PriceCalculatorComponent subtotal={100000} shippingFee={30000} discount={10000} />
    );

    const totalEl = screen.getByTestId("checkout-summary-total-price");
    await waitFor(() => {
      expect(totalEl.textContent).toBe(formatPrice(120000));
    });

    rerender(
      <PriceCalculatorComponent subtotal={200000} shippingFee={30000} discount={10000} />
    );

    await waitFor(() => {
      expect(totalEl.textContent).toBe(formatPrice(220000));
    });
  });

  test("TC6: Tính tổng tiền đơn hàng là số âm", async () => {
    render(
      <PriceCalculatorComponent subtotal={10000} shippingFee={0} discount={50000} />
    );

    const totalEl = screen.getByTestId("checkout-summary-total-price");
    await waitFor(() => {
      expect(totalEl.textContent).toBe(formatPrice(-40000));
    });

    const errorMsg = screen.getByTestId("checkout-summary-total-price-negative-inform");
    expect(errorMsg).not.toBeNull();
    await waitFor(() => {
      expect(errorMsg.textContent).toBe("Tổng tiền đang là số âm");
    });
  });
});