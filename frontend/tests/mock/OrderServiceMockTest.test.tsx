import React from "react";
import { describe, test, expect, vi, beforeEach } from "vitest";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import { OrderApi } from "../../src/services/api/orderApi";
import { CouponApi } from "../../src/services/api/couponApi";
import { HttpStatusCode } from "axios";
import CheckoutSummaryComponent from "../../src/components/CheckoutSummary";

const navigate = vi.fn();
vi.mock("../../src/services/api/orderApi", () => ({
  OrderApi: { createOrder: vi.fn() },
}));

vi.mock("../../src/services/api/couponApi", () => ({
  CouponApi: { getCouponByCode: vi.fn() },
}));

vi.mock("react-router-dom", async () => {
  const actual = await vi.importActual("react-router-dom");
  return { ...actual, useNavigate: vi.fn() };
});

vi.mock("react-toastify", () => ({
  toast: { success: vi.fn(), warning: vi.fn(), error: vi.fn() },
}));

vi.stubGlobal("confirm", vi.fn());

const mockCartItems = [
  {
    id: "CI-001",
    product: {
      id: "1cce346b-1c79-4074-8946-9e5b5f911497",
      name: "MacBook Pro M3",
      price: 50000,
      inventory: { stock: 10 },
    },
    quantity: 2,
  },
];

beforeEach(() => {
  vi.clearAllMocks();
  vi.mocked(useNavigate).mockReturnValue(navigate);
  vi.mocked(window.confirm).mockReturnValue(true);
});

describe("Order Component Integration Tests", () => {
  const setupUI = () => {
    render(
      <CheckoutSummaryComponent
        userId="USER-001"
        subtotal={100000}
        cartItems={mockCartItems as any}
        fetchCartByUserId={vi.fn()}
      />
    );
    
    const addressInput = screen.getByTestId("checkout-summary-shipping-address-input");
    fireEvent.change(addressInput, { target: { value: "97 Man Thiện, Quận 9" } });

    return {
      orderButton: screen.getByTestId("checkout-summary-order-button"),
      couponInput: screen.getByTestId("checkout-summary-coupon-input"),
      couponButton: screen.getByTestId("checkout-summary-coupon-button"),
    };
  };

  test("TC7: Tạo đơn hàng nhưng tồn kho của sản phẩm không tồn tại", async () => {
    vi.mocked(OrderApi.createOrder).mockResolvedValue({
      status: HttpStatusCode.NotFound,
      error: "INVENTORY_NOT_FOUND",
      message: "Inventory not found for product ID 1cce346b-1c79-4074-8946-9e5b5f911497",
      data: null,
    } as any);

    const { orderButton } = setupUI();
    fireEvent.click(orderButton);

    await waitFor(() => {
      expect(toast.error).toHaveBeenCalledWith(
        "Inventory not found for product ID 1cce346b-1c79-4074-8946-9e5b5f911497"
      );
    });
  });

  test("TC8: Tạo đơn hàng nhưng tồn kho của sản phẩm không đủ", async () => {
    vi.mocked(OrderApi.createOrder).mockResolvedValue({
      status: HttpStatusCode.BadRequest,
      error: "INSUFFICIENT_STOCK",
      message: "Insufficient stock for product ID 1cce346b-1c79-4074-8946-9e5b5f911497",
      data: null,
    } as any);

    const { orderButton } = setupUI();
    fireEvent.click(orderButton);

    await waitFor(() => {
      expect(toast.error).toHaveBeenCalledWith(
        "Insufficient stock for product ID 1cce346b-1c79-4074-8946-9e5b5f911497"
      );
    });
  });

  test("TC9: Tạo đơn hàng nhưng người dùng không tồn tại", async () => {
    vi.mocked(OrderApi.createOrder).mockResolvedValue({
      status: HttpStatusCode.NotFound,
      error: "USER_NOT_FOUND",
      message: "User not found!",
      data: null,
    } as any);

    const { orderButton } = setupUI();
    fireEvent.click(orderButton);

    await waitFor(() => {
      expect(toast.error).toHaveBeenCalledWith("User not found!");
    });
  });

  test("TC10: Tạo đơn hàng nhưng mã giảm giá không tồn tại", async () => {
    vi.mocked(CouponApi.getCouponByCode).mockResolvedValue({
      status: HttpStatusCode.NotFound,
      error: "COUPON_NOT_FOUND_BY_CODE",
      message: "Coupon not found by code MAGIAMGIA222",
      data: null,
    } as any);

    const { couponInput, couponButton } = setupUI();
    fireEvent.change(couponInput, { target: { value: "MAGIAMGIA222" } });
    fireEvent.click(couponButton);

    await waitFor(() => {
      expect(toast.error).toHaveBeenCalledWith("Coupon not found by code MAGIAMGIA222");
    });
  });

  test("TC11: Tạo đơn hàng nhưng mã giảm giá hết hạn", async () => {
    vi.mocked(CouponApi.getCouponByCode).mockResolvedValue({
      status: HttpStatusCode.BadRequest,
      error: "COUPON_EXPIRED",
      message: "Coupon MAGIAMGIA222 has expired",
      data: null,
    } as any);

    const { couponInput, couponButton } = setupUI();
    fireEvent.change(couponInput, { target: { value: "MAGIAMGIA222" } });
    fireEvent.click(couponButton);

    await waitFor(() => {
      expect(toast.error).toHaveBeenCalledWith("Coupon MAGIAMGIA222 has expired");
    });
  });
});