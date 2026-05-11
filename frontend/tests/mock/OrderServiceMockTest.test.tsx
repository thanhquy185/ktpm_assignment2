import { describe, test, expect, vi, beforeEach } from "vitest";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { HttpStatusCode } from "axios";
import { OrderApi } from "../../src/services/api/orderApi";
import CheckoutSummaryComponent from "../../src/components/CheckoutSummary";
import { CartItemType } from "../../src/types/cartItem";
import { OrderCreateRequest } from "../../src/types/order";
import { toast } from "react-toastify";
import { useNavigate } from "react-router-dom";
import { CouponApi } from "../../src/services/api/couponApi";

const cartItemFakeData: CartItemType = {
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

const navigate = vi.fn();

vi.mock("../../src/services/api/couponApi", () => ({
  CouponApi: { getCouponByCode: vi.fn() },
}));
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

describe("Order API Mock Tests", () => {
  const setupUI = () => {
    render(
      <CheckoutSummaryComponent
        userId="USER-001"
        subtotal={100000}
        cartItems={mockCartItems as any}
        fetchCartByUserId={vi.fn()}
      />,
    );

    const addressInput = screen.getByTestId(
      "checkout-summary-shipping-address-input",
    );
    fireEvent.change(addressInput, {
      target: { value: "97 Man Thiện, Quận 9" },
    });

    return {
      orderButton: screen.getByTestId("checkout-summary-order-button"),
      couponInput: screen.getByTestId("checkout-summary-coupon-input"),
      couponButton: screen.getByTestId("checkout-summary-coupon-button"),
    };
  };

  test("TC1: Tạo đơn hàng thành công", async () => {
    const fetchCartByUserId = vi.fn();

    vi.mocked(OrderApi.createOrder).mockResolvedValue({
      status: HttpStatusCode.Created,
      error: null,
      message: "Order created successfully",
      data: { id: "ORDER-001" },
    } as any);

    render(
      <CheckoutSummaryComponent
        userId="USER-001"
        cartItems={[cartItemFakeData]}
        subtotal={40000}
        fetchCartByUserId={fetchCartByUserId}
      />,
    );

    const addressInput = screen.getByTestId(
      "checkout-summary-shipping-address-input",
    );
    const orderButton = screen.getByTestId("checkout-summary-order-button");

    fireEvent.change(addressInput, {
      target: {
        value: "123 Nguyễn Huệ",
      },
    });
    fireEvent.click(orderButton);

    await waitFor(() => {
      expect(OrderApi.createOrder).toHaveBeenCalledWith({
        userId: "USER-001",
        couponId: null,
        shippingAddress: "123 Nguyễn Huệ",
        shippingMethod: "Tiêu chuẩn",
        shippingFee: 20000,
        paymentMethod: "Thanh toán khi nhận hàng",
        orderItems: [{ productId: "PRO-001", quantity: 2, price: 20000 }],
      } as OrderCreateRequest);

      expect(toast.success).toHaveBeenCalledWith("Order created successfully");
      expect(navigate).toHaveBeenCalledWith("/products");
      expect(fetchCartByUserId).toHaveBeenCalledWith("USER-001");
    });
  });

  test("TC2: Tạo đơn hàng nhưng sản phẩm không tồn tại", async () => {
    const fetchCartByUserId = vi.fn();

    vi.mocked(OrderApi.createOrder).mockResolvedValue({
      status: HttpStatusCode.BadRequest,
      error: "PRODUCT_NOT_FOUND",
      message: "Product not found",
      data: null,
    } as any);

    render(
      <CheckoutSummaryComponent
        userId="USER-001"
        cartItems={[
          {
            ...cartItemFakeData,
            product: { ...cartItemFakeData.product, id: "PRO-INVALID" },
          },
        ]}
        subtotal={40000}
        fetchCartByUserId={fetchCartByUserId}
      />,
    );

    const addressInput = screen.getByTestId(
      "checkout-summary-shipping-address-input",
    );
    const orderButton = screen.getByTestId("checkout-summary-order-button");

    fireEvent.change(addressInput, {
      target: {
        value: "123 Nguyễn Huệ",
      },
    });
    fireEvent.click(orderButton);

    await waitFor(() => {
      expect(OrderApi.createOrder).toHaveBeenCalledWith({
        userId: "USER-001",
        couponId: null,
        shippingAddress: "123 Nguyễn Huệ",
        shippingMethod: "Tiêu chuẩn",
        shippingFee: 20000,
        paymentMethod: "Thanh toán khi nhận hàng",
        orderItems: [{ productId: "PRO-INVALID", quantity: 2, price: 20000 }],
      } as OrderCreateRequest);

      expect(toast.error).toHaveBeenCalledWith("Product not found");
      expect(navigate).not.toHaveBeenCalled();
      expect(fetchCartByUserId).not.toHaveBeenCalled();
    });
  });

  test("TC3: Tạo đơn hàng nhưng số lượng sản phẩm bé hơn 0", async () => {
    const fetchCartByUserId = vi.fn();

    vi.mocked(OrderApi.createOrder).mockResolvedValue({
      status: HttpStatusCode.BadRequest,
      error: "INVALID_QUANTITY",
      message: "Quantity must be greater than 0",
      data: null,
    } as any);

    render(
      <CheckoutSummaryComponent
        userId="USER-001"
        cartItems={[{ ...cartItemFakeData, quantity: -1 }]}
        subtotal={-20000}
        fetchCartByUserId={fetchCartByUserId}
      />,
    );

    const addressInput = screen.getByTestId(
      "checkout-summary-shipping-address-input",
    );
    const orderButton = screen.getByTestId("checkout-summary-order-button");

    fireEvent.change(addressInput, {
      target: {
        value: "123 Nguyễn Huệ",
      },
    });
    fireEvent.click(orderButton);

    await waitFor(() => {
      expect(OrderApi.createOrder).toHaveBeenCalledWith({
        userId: "USER-001",
        couponId: null,
        shippingAddress: "123 Nguyễn Huệ",
        shippingMethod: "Tiêu chuẩn",
        shippingFee: 20000,
        paymentMethod: "Thanh toán khi nhận hàng",
        orderItems: [{ productId: "PRO-001", quantity: -1, price: 20000 }],
      } as OrderCreateRequest);

      expect(toast.error).toHaveBeenCalledWith(
        "Quantity must be greater than 0",
      );
      expect(navigate).not.toHaveBeenCalled();
      expect(fetchCartByUserId).not.toHaveBeenCalled();
    });
  });

  test("TC4: Tạo đơn hàng nhưng số lượng sản phẩm bằng 0", async () => {
    const fetchCartByUserId = vi.fn();

    vi.mocked(OrderApi.createOrder).mockResolvedValue({
      status: HttpStatusCode.BadRequest,
      error: "INVALID_QUANTITY",
      message: "Quantity must be greater than 0",
      data: null,
    } as any);

    render(
      <CheckoutSummaryComponent
        userId="USER-001"
        cartItems={[{ ...cartItemFakeData, quantity: 0 }]}
        subtotal={0}
        fetchCartByUserId={fetchCartByUserId}
      />,
    );

    const addressInput = screen.getByTestId(
      "checkout-summary-shipping-address-input",
    );
    const orderButton = screen.getByTestId("checkout-summary-order-button");

    fireEvent.change(addressInput, {
      target: {
        value: "123 Nguyễn Huệ",
      },
    });
    fireEvent.click(orderButton);

    await waitFor(() => {
      expect(OrderApi.createOrder).toHaveBeenCalledWith({
        userId: "USER-001",
        couponId: null,
        shippingAddress: "123 Nguyễn Huệ",
        shippingMethod: "Tiêu chuẩn",
        shippingFee: 20000,
        paymentMethod: "Thanh toán khi nhận hàng",
        orderItems: [{ productId: "PRO-001", quantity: 0, price: 20000 }],
      } as OrderCreateRequest);

      expect(toast.error).toHaveBeenCalledWith(
        "Quantity must be greater than 0",
      );
      expect(navigate).not.toHaveBeenCalled();
      expect(fetchCartByUserId).not.toHaveBeenCalled();
    });
  });

  test("TC5: Tạo đơn hàng nhưng giá bán sản phẩm bé hơn 0", async () => {
    const fetchCartByUserId = vi.fn();

    vi.mocked(OrderApi.createOrder).mockResolvedValue({
      status: HttpStatusCode.BadRequest,
      error: "INVALID_PRICE",
      message: "Price must be greater than or equal to 0",
      data: null,
    } as any);

    render(
      <CheckoutSummaryComponent
        userId="USER-001"
        cartItems={[
          {
            ...cartItemFakeData,
            product: { ...cartItemFakeData.product, price: -10000 },
          },
        ]}
        subtotal={-20000}
        fetchCartByUserId={fetchCartByUserId}
      />,
    );

    const addressInput = screen.getByTestId(
      "checkout-summary-shipping-address-input",
    );
    const orderButton = screen.getByTestId("checkout-summary-order-button");

    fireEvent.change(addressInput, {
      target: {
        value: "123 Nguyễn Huệ",
      },
    });
    fireEvent.click(orderButton);

    await waitFor(() => {
      expect(OrderApi.createOrder).toHaveBeenCalledWith({
        userId: "USER-001",
        couponId: null,
        shippingAddress: "123 Nguyễn Huệ",
        shippingMethod: "Tiêu chuẩn",
        shippingFee: 20000,
        paymentMethod: "Thanh toán khi nhận hàng",
        orderItems: [{ productId: "PRO-001", quantity: 2, price: -10000 }],
      } as OrderCreateRequest);

      expect(toast.error).toHaveBeenCalledWith(
        "Price must be greater than or equal to 0",
      );
      expect(navigate).not.toHaveBeenCalled();
      expect(fetchCartByUserId).not.toHaveBeenCalled();
    });
  });

  test("TC6: Tạo đơn hàng nhưng giá bán sản phẩm bằng 0", async () => {
    const fetchCartByUserId = vi.fn();

    vi.mocked(OrderApi.createOrder).mockResolvedValue({
      status: HttpStatusCode.Created,
      error: null,
      message: "Order created successfully",
      data: { id: "ORDER-002" },
    } as any);

    render(
      <CheckoutSummaryComponent
        userId="USER-001"
        cartItems={[
          {
            ...cartItemFakeData,
            product: { ...cartItemFakeData.product, price: 0 },
          },
        ]}
        subtotal={0}
        fetchCartByUserId={fetchCartByUserId}
      />,
    );

    const addressInput = screen.getByTestId(
      "checkout-summary-shipping-address-input",
    );
    const orderButton = screen.getByTestId("checkout-summary-order-button");

    fireEvent.change(addressInput, {
      target: {
        value: "123 Nguyễn Huệ",
      },
    });
    fireEvent.click(orderButton);

    await waitFor(() => {
      expect(OrderApi.createOrder).toHaveBeenCalledWith({
        userId: "USER-001",
        couponId: null,
        shippingAddress: "123 Nguyễn Huệ",
        shippingMethod: "Tiêu chuẩn",
        shippingFee: 20000,
        paymentMethod: "Thanh toán khi nhận hàng",
        orderItems: [{ productId: "PRO-001", quantity: 2, price: 0 }],
      } as OrderCreateRequest);

      expect(toast.success).toHaveBeenCalledWith("Order created successfully");
      expect(navigate).toHaveBeenCalledWith("/products");
      expect(fetchCartByUserId).toHaveBeenCalledWith("USER-001");
    });
  });

  test("TC7: Tạo đơn hàng nhưng tồn kho của sản phẩm không tồn tại", async () => {
    vi.mocked(OrderApi.createOrder).mockResolvedValue({
      status: HttpStatusCode.NotFound,
      error: "INVENTORY_NOT_FOUND",
      message:
        "Inventory not found for product ID 1cce346b-1c79-4074-8946-9e5b5f911497",
      data: null,
    } as any);

    const { orderButton } = setupUI();
    fireEvent.click(orderButton);

    await waitFor(() => {
      expect(toast.error).toHaveBeenCalledWith(
        "Inventory not found for product ID 1cce346b-1c79-4074-8946-9e5b5f911497",
      );
    });
  });

  test("TC8: Tạo đơn hàng nhưng tồn kho của sản phẩm không đủ", async () => {
    vi.mocked(OrderApi.createOrder).mockResolvedValue({
      status: HttpStatusCode.BadRequest,
      error: "INSUFFICIENT_STOCK",
      message:
        "Insufficient stock for product ID 1cce346b-1c79-4074-8946-9e5b5f911497",
      data: null,
    } as any);

    const { orderButton } = setupUI();
    fireEvent.click(orderButton);

    await waitFor(() => {
      expect(toast.error).toHaveBeenCalledWith(
        "Insufficient stock for product ID 1cce346b-1c79-4074-8946-9e5b5f911497",
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
      expect(toast.error).toHaveBeenCalledWith(
        "Coupon not found by code MAGIAMGIA222",
      );
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
      expect(toast.error).toHaveBeenCalledWith(
        "Coupon MAGIAMGIA222 has expired",
      );
    });
  });
});
