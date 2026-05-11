import { describe, test, expect, vi, beforeEach } from "vitest";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import CheckoutSummaryComponent from "../../src/components/CheckoutSummary";
import { formatPrice } from "../../src/utils/priceCalculation";
import { CartItemType } from "../../src/types/cartItem";
import { OrderApi } from "../../src/services/api/orderApi";
import { HttpStatusCode } from "axios";
import { OrderCreateRequest } from "../../src/types/order";
import { toast } from "react-toastify";
import { useNavigate } from "react-router-dom";

const cartItemFakeData1: CartItemType = {
  id: "CI-001",
  product: {
    id: "PRO-001",
    name: "Laptop Dell",
    category: { id: "CAG-001", name: "Laptop" },
    inventory: { id: "INV-001", stock: 5 },
    price: 20000,
    description: "Laptop sinh viên",
  },
  quantity: 2,
};
const cartItemFakeData2: CartItemType = {
  id: "CI-002",
  product: {
    id: "PRO-002",
    name: "Macbook M4 Air",
    category: { id: "CAG-002", name: "Macbook" },
    inventory: { id: "INV-002", stock: 5 },
    price: 50000,
    description: "Macbook sinh viên",
  },
  quantity: 1,
};
const subtotalFakeData: number = 90000;

const navigate = vi.fn();

vi.mock("../../src/services/api/orderApi", () => ({
  OrderApi: {
    createOrder: vi.fn(),
  },
}));
vi.mock("react-router-dom", async () => {
  const actual = await vi.importActual("react-router-dom");
  return {
    ...actual,
    useNavigate: vi.fn(),
  };
});
vi.mock("react-toastify", () => ({
  toast: {
    success: vi.fn(),
    warning: vi.fn(),
    error: vi.fn(),
  },
}));
vi.stubGlobal("confirm", vi.fn());

beforeEach(() => {
  vi.clearAllMocks();
  vi.mocked(useNavigate).mockReturnValue(navigate);
  vi.mocked(window.confirm).mockReturnValue(true);
});

describe("Checkout Summary Component Integration Tests", () => {
  test("TC1: Thông tin thanh toán với dữ liệu giỏ hàng", () => {
    render(
      <CheckoutSummaryComponent
        userId="USER-001"
        cartItems={[cartItemFakeData1, cartItemFakeData2]}
        subtotal={subtotalFakeData}
        fetchCartByUserId={vi.fn()}
      />,
    );

    const subtotal = screen.getByTestId("checkout-summary-subtotal");

    waitFor(() => {
      expect(subtotal.textContent).toBe(formatPrice(90000));
    });
  });

  test("TC2: Thanh toán thành công", async () => {
    vi.mocked(OrderApi.createOrder).mockResolvedValue({
      status: HttpStatusCode.Created,
      error: null,
      message: "Create order is successful!",
      data: {
        id: "ORD-001",
      },
    } as any);

    render(
      <CheckoutSummaryComponent
        userId="USER-001"
        cartItems={[cartItemFakeData1, cartItemFakeData2]}
        subtotal={subtotalFakeData}
        fetchCartByUserId={vi.fn()}
      />,
    );

    const addressInput = screen.getByTestId(
      "checkout-summary-shipping-address-input",
    );
    const orderButton = screen.getByTestId("checkout-summary-order-button");
    fireEvent.change(addressInput, {
      target: {
        value: "Đại học Sài Gòn",
      },
    });
    fireEvent.click(orderButton);

    await waitFor(() => {
      expect(OrderApi.createOrder).toHaveBeenCalledWith({
        userId: "USER-001",
        couponId: null,
        shippingAddress: "Đại học Sài Gòn",
        shippingMethod: "Tiêu chuẩn",
        paymentMethod: "Thanh toán khi nhận hàng",
        shippingFee: 20000,
        orderItems: [
          { productId: "PRO-001", price: 20000, quantity: 2 },
          { productId: "PRO-002", price: 50000, quantity: 1 },
        ],
      } as OrderCreateRequest);

      expect(toast.success).toHaveBeenCalledTimes(1);
      expect(toast.success).toHaveBeenCalledWith("Create order is successful!");
      expect(navigate).toHaveBeenCalledWith("/products");
    });
  });

  test("TC3: Thanh toán thành công và có sử dụng mã giảm giá", async () => {
    vi.mocked(OrderApi.createOrder).mockResolvedValue({
      status: HttpStatusCode.Created,
      error: null,
      message: "Create order is successful!",
      data: {
        id: "ORD-001",
      },
    } as any);

    render(
      <CheckoutSummaryComponent
        userId="USER-001"
        cartItems={[cartItemFakeData1, cartItemFakeData2]}
        subtotal={subtotalFakeData}
        fetchCartByUserId={vi.fn()}
      />,
    );

    const addressInput = screen.getByTestId(
      "checkout-summary-shipping-address-input",
    );
    const couponInput = screen.getByTestId("checkout-summary-coupon-input");
    const couponButton = screen.getByTestId("checkout-summary-coupon-button");
    const orderButton = screen.getByTestId("checkout-summary-order-button");

    fireEvent.change(addressInput, {
      target: {
        value: "Đại học Sài Gòn",
      },
    });
    fireEvent.change(couponInput, {
      target: {
        value: "SALE10",
      },
    });
    fireEvent.click(couponButton);

    await waitFor(() => {
      expect(
        screen.getByTestId("checkout-summary-coupon-result"),
      ).toBeDefined();
    });

    fireEvent.click(orderButton);

    await waitFor(() => {
      expect(OrderApi.createOrder).toHaveBeenCalledWith({
        userId: "USER-001",
        couponId: "a702859e-a0a3-4363-aa72-fdeb90498ab7",
        shippingAddress: "Đại học Sài Gòn",
        shippingMethod: "Tiêu chuẩn",
        paymentMethod: "Thanh toán khi nhận hàng",
        shippingFee: 20000,
        orderItems: [
          {
            productId: "PRO-001",
            price: 20000,
            quantity: 2,
          },
          {
            productId: "PRO-002",
            price: 50000,
            quantity: 1,
          },
        ],
      } as OrderCreateRequest);

      expect(toast.success).toHaveBeenCalledTimes(2);
      expect(toast.success).toHaveBeenCalledWith(
        "Áp dụng mã giảm giá thành công!",
      );
      expect(toast.success).toHaveBeenCalledWith("Create order is successful!");
      expect(navigate).toHaveBeenCalledWith("/products");
    });
  });

  test("TC4: Thanh toán nhưng chưa nhập địa chỉ giao hàng", () => {
    render(
      <CheckoutSummaryComponent
        userId="USER-001"
        cartItems={[cartItemFakeData1, cartItemFakeData2]}
        subtotal={subtotalFakeData}
        fetchCartByUserId={vi.fn()}
      />,
    );

    const orderButton = screen.getByTestId("checkout-summary-order-button");
    fireEvent.click(orderButton);

    waitFor(() => {
      expect(toast.warning).toHaveBeenCalledTimes(1);
      expect(toast.warning).toHaveBeenCalledWith("Cần nhập địa chỉ giao hàng!");
    });
  });
});
