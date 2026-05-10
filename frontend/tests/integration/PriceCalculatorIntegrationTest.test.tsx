import { describe, test, expect } from "vitest";
import { render, screen } from "@testing-library/react";
import PriceCalculatorComponent from "../../src/components/PriceCalculator";
import { formatPrice } from "../../src/utils/priceCalculation";

describe("Price Calculator Component Integration Tests", () => {
  test("TC1: Tiền hàng là số âm", () => {
    render(
      <PriceCalculatorComponent
        subtotal={-10000}
        shippingFee={30000}
        discount={0}
      />,
    );
    const errorMsg = screen.getByTestId(
      "checkout-summary-subtotal-negative-inform",
    );
    expect(errorMsg).toBeDefined();
    expect(errorMsg.textContent).toContain("Tiền hàng đang là số âm");
  });

  test("TC2: Phí ship là số âm", () => {
    render(
      <PriceCalculatorComponent
        subtotal={100000}
        shippingFee={-5000}
        discount={0}
      />,
    );

    const errorMsg = screen.getByTestId(
      "checkout-summary-shipping-fee-negative-inform",
    );
    expect(errorMsg).toBeDefined();
    expect(errorMsg.textContent).toContain("Phí ship đang là số âm");
  });

  test("TC3: Giảm giá là số âm", () => {
    render(
      <PriceCalculatorComponent
        subtotal={100000}
        shippingFee={30000}
        discount={-10000}
      />,
    );

    const errorMsg = screen.getByTestId(
      "checkout-summary-discount-negative-inform",
    );
    expect(errorMsg).toBeDefined();
    expect(errorMsg.textContent).toContain("Giảm giá đang là số âm");
  });

  test("TC4: Tiền hàng, phí ship và giảm giá đều hợp lệ", () => {
    render(
      <PriceCalculatorComponent
        subtotal={100000}
        shippingFee={30000}
        discount={10000}
      />,
    );

    const subtotalEl = screen.getByTestId("checkout-summary-subtotal");
    const shippingFeeEl = screen.getByTestId("checkout-summary-shipping-fee");
    const discountEl = screen.getByTestId("checkout-summary-discount");

    expect(
      screen.queryByTestId("checkout-summary-subtotal-negative-inform"),
    ).toBeNull();
    expect(
      screen.queryByTestId("checkout-summary-shipping-fee-negative-inform"),
    ).toBeNull();
    expect(
      screen.queryByTestId("checkout-summary-discount-negative-inform"),
    ).toBeNull();

    expect(screen.getByTestId("checkout-summary-subtotal")).toBeDefined();
    expect(subtotalEl.textContent).toContain(formatPrice(100000));
    expect(screen.getByTestId("checkout-summary-shipping-fee")).toBeDefined();
    expect(shippingFeeEl.textContent).toContain(formatPrice(30000));
    expect(screen.getByTestId("checkout-summary-discount")).toBeDefined();
    expect(discountEl.textContent).toContain(formatPrice(10000));
  });

  test("TC5: Tính tổng tiền đơn hàng thành công", () => {
    render(
      <PriceCalculatorComponent
        subtotal={100000}
        shippingFee={30000}
        discount={10000}
      />,
    );
    const totalEl = screen.getByTestId("checkout-summary-total-price");

    expect(
      screen.queryByTestId("checkout-summary-total-price-negative-inform"),
    ).toBeNull();

    expect(totalEl).toBeDefined();
    expect(totalEl.textContent).toContain(formatPrice(120000));
  });

  test("TC6: Tính tổng tiền đơn hàng là số âm", () => {
    render(
      <PriceCalculatorComponent subtotal={-1} shippingFee={-1} discount={-1} />,
    );
    const totalEl = screen.getByTestId(
      "checkout-summary-total-price-negative-inform",
    );

    expect(totalEl).toBeDefined();
    expect(totalEl.textContent).toContain("Tổng tiền đang là số âm");
  });
});
