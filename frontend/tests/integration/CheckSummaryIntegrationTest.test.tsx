import React from "react";
import { describe, test, expect, vi, beforeEach } from "vitest";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { toast } from "react-toastify";
import CheckoutSummaryComponent from "../../src/components/CheckoutSummary";

// mocks

describe("Check Summary Component Integration Tests", () => {
  test("TC1: Tiền hàng là số âm", () => {
    render(<CheckoutSummaryComponent subtotal={-10000} shippingFee={30000} discount={0} />);
    const errorMsg = screen.getByTestId("checkout-summary-subtotal-negative-inform");
    expect(errorMsg).toBeDefined();
    expect(errorMsg.textContent).toContain("Tiền hàng đang là số âm");
  });

  test("TC2: Phí ship là số âm", () => {
    render(<CheckoutSummaryComponent subtotal={100000} shippingFee={-5000} discount={0} />);
    
    const errorMsg = screen.getByTestId("checkout-summary-shipping-fee-negative-inform");
    expect(errorMsg).toBeDefined();
    expect(errorMsg.textContent).toContain("Phí ship đang là số âm");
  });

  test("TC3: Giảm giá là số âm", () => {
    render(<CheckoutSummaryComponent subtotal={100000} shippingFee={30000} discount={-10000} />);
    
    const errorMsg = screen.getByTestId("checkout-summary-discount-negative-inform");
    expect(errorMsg).toBeDefined();
    expect(errorMsg.textContent).toContain("Giảm giá đang là số âm");
  });

  test("TC4: Tiền hàng, phí ship và giảm giá đều hợp lệ", () => {
    render(<CheckoutSummaryComponent subtotal={100000} shippingFee={30000} discount={10000} />);

    expect(screen.queryByTestId("checkout-summary-subtotal-negative-inform")).toBeNull();
    expect(screen.queryByTestId("checkout-summary-shipping-fee-negative-inform")).toBeNull();
    expect(screen.queryByTestId("checkout-summary-discount-negative-inform")).toBeNull();

    expect(screen.getByTestId("checkout-summary-subtotal")).toBeDefined();
    expect(screen.getByTestId("checkout-summary-shipping-fee")).toBeDefined();
    expect(screen.getByTestId("checkout-summary-discount")).toBeDefined();
  });
});
