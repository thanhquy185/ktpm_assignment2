import { describe, test, expect, vi, beforeEach } from "vitest";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import { CartApi } from "../../src/services/api/cartApi";
import CartComponent from "../../src/components/Cart";
import { formatPrice } from "../../src/utils/priceCalculation";
import { HttpStatusCode } from "axios";
import {
  CartItemAddToCartRequest,
  CartItemType,
} from "../../src/types/cartItem";

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

const navigate = vi.fn();

vi.mock("../../src/services/api/cartApi", () => ({
  CartApi: {
    updateQuantity: vi.fn(),
    removeFromCart: vi.fn(),
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

describe("Cart Component Integration Tests", () => {
  test("TC1: Giỏ hàng rỗng", () => {
    render(
      <CartComponent
        userId="USER-001"
        cartItems={[]}
        fetchCartByUserId={vi.fn()}
      />,
    );

    const emptyCartInform = screen.getByTestId("empty-cart-inform");

    expect(emptyCartInform.textContent).toContain(
      "Giỏ hàng của bạn đang trống",
    );
  });

  test('TC2: Nhấn nút "Tiếp tục mua sắm" khi giỏ hàng rỗng', () => {
    render(
      <CartComponent
        userId="USER-001"
        cartItems={[]}
        fetchCartByUserId={vi.fn()}
      />,
    );

    const goToProductsPageButton = screen.getByTestId(
      "go-to-products-page-button",
    );
    fireEvent.click(goToProductsPageButton);

    expect(navigate).toHaveBeenCalledWith("/products");
  });

  test("TC3: Giỏ hàng có sản phẩm", () => {
    render(
      <CartComponent
        userId="USER-001"
        cartItems={[cartItemFakeData1, cartItemFakeData2]}
        fetchCartByUserId={vi.fn()}
      />,
    );

    // Laptop Dell
    const productName1 = screen.getByTestId("cart-item-product-name-CI-001");
    const productCategory1 = screen.getByTestId(
      "cart-item-product-category-CI-001",
    );
    const productPrice1 = screen.getByTestId("cart-item-product-price-CI-001");
    // Macbook M4 Air
    const productName2 = screen.getByTestId("cart-item-product-name-CI-002");
    const productCategory2 = screen.getByTestId(
      "cart-item-product-category-CI-002",
    );
    const productPrice2 = screen.getByTestId("cart-item-product-price-CI-002");

    // Laptop Dell
    expect(productName1.textContent).toBe("Laptop Dell");
    expect(productCategory1.textContent).toBe("Laptop");
    expect(productPrice1.textContent).toBe(formatPrice(20000));
    // Macbook M4 Air
    expect(productName2.textContent).toBe("Macbook M4 Air");
    expect(productCategory2.textContent).toBe("Macbook");
    expect(productPrice2.textContent).toBe(formatPrice(50000));
  });

  test("TC4: Giỏ hàng có sản phẩm và tăng số lượng 1 sản phẩm thành công", async () => {
    vi.mocked(CartApi.updateQuantity).mockResolvedValue({
      status: HttpStatusCode.Ok,
      error: null,
      message: "Update product quantity in cart is successful!",
      data: {
        id: "CART-001",
      },
    } as any);

    const fetchCartByUserId = vi.fn();

    render(
      <CartComponent
        userId="USER-001"
        cartItems={[cartItemFakeData1]}
        fetchCartByUserId={fetchCartByUserId}
      />,
    );

    const increaseButton = screen.getByTestId(
      "cart-item-increase-quantity-button-CI-001",
    );
    fireEvent.click(increaseButton);

    await waitFor(() => {
      expect(CartApi.updateQuantity).toHaveBeenCalledWith("USER-001", {
        productId: "PRO-001",
        quantity: 3,
      } as CartItemAddToCartRequest);
    });

    expect(fetchCartByUserId).toHaveBeenCalledTimes(1);
    expect(fetchCartByUserId).toHaveBeenCalledWith("USER-001");
    expect(toast.success).toHaveBeenCalledTimes(1);
    expect(toast.success).toHaveBeenCalledWith(
      "Update product quantity in cart is successful!",
    );
  });
});
