import { describe, test, expect, vi, beforeEach } from "vitest";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import { CartApi } from "../../src/services/api/cartApi";
import CartComponent from "../../src/components/Cart";
import CartSummaryComponent from "../../src/components/CartSummary";
import { formatPrice } from "../../src/utils/priceCalculation";
import { HttpStatusCode } from "axios";
import {
  CartItemAddToCartRequest,
  CartItemType,
} from "../../src/types/cartItem";
import { CartType } from "../../src/types/cart";

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

  test("TC5: Giỏ hàng có sản phẩm và giảm số lượng 1 sản phẩm thành công", async () => {
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

    const decreaseButton = screen.getByTestId(
      "cart-item-decrease-quantity-button-CI-001",
    );

    fireEvent.click(decreaseButton);

    await waitFor(() => {
      expect(CartApi.updateQuantity).toHaveBeenCalledWith("USER-001", {
        productId: "PRO-001",
        quantity: 1,
      } as CartItemAddToCartRequest);
      expect(fetchCartByUserId).toHaveBeenCalledTimes(1);
      expect(fetchCartByUserId).toHaveBeenCalledWith("USER-001");

      expect(toast.success).toHaveBeenCalledTimes(1);
      expect(toast.success).toHaveBeenCalledWith(
        "Update product quantity in cart is successful!",
      );
    });
  });

  test("TC6: Giỏ hàng có sản phẩm và giảm số lượng 1 sản phẩm nhưng số lượng trong giỏ đang là 1", async () => {
    const fetchCartByUserId = vi.fn();

    render(
      <CartComponent
        userId="USER-001"
        cartItems={[cartItemFakeData2]} // quantity = 1
        fetchCartByUserId={fetchCartByUserId}
      />,
    );

    const decreaseButton = screen.getByTestId(
      "cart-item-decrease-quantity-button-CI-002",
    );

    fireEvent.click(decreaseButton);

    await waitFor(() => {
      expect(CartApi.updateQuantity).not.toHaveBeenCalled();
      expect(fetchCartByUserId).not.toHaveBeenCalled();

      expect(toast.warning).toHaveBeenCalledTimes(1);
      expect(toast.warning).toHaveBeenCalledWith(
        "Không thể giảm số lượng nếu đang là 1 !",
      );
    });
  });

  test("TC7: Giỏ hàng có sản phẩm và xoá 1 sản phẩm thành công", async () => {
    vi.mocked(CartApi.removeFromCart).mockResolvedValue({
      status: HttpStatusCode.Ok,
      error: null,
      message: "Remove product from cart is successful!",
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

    const removeButton = screen.getByTestId("cart-item-remove-button-CI-001");

    fireEvent.click(removeButton);

    await waitFor(() => {
      expect(CartApi.removeFromCart).toHaveBeenCalledWith("USER-001", {
        productId: "PRO-001",
      });
      expect(fetchCartByUserId).toHaveBeenCalledTimes(1);
      expect(fetchCartByUserId).toHaveBeenCalledWith("USER-001");

      expect(toast.success).toHaveBeenCalledTimes(1);
      expect(toast.success).toHaveBeenCalledWith(
        "Xoá sản phẩm trong giỏ thành công !",
      );
    });
  });

  test('TC8: Nhấn nút "Thanh toán ngay" khi giỏ hàng rỗng', () => {
    const emptyCart: CartType = {
      id: "CART-001",
      user: {
        id: "USER-001",
        username: "nhathuy",
        role: "USER",
      },
      totalQuantity: 0,
      totalPrice: 0,
      cartItems: [],
    };

    render(<CartSummaryComponent cart={emptyCart} />);

    const checkoutButton = screen.getByRole("button", {
      name: "Thanh toán ngay",
    });

    fireEvent.click(checkoutButton);

    expect(toast.warning).toHaveBeenCalledTimes(1);
    expect(toast.warning).toHaveBeenCalledWith(
      "Không thể thanh toán khi giỏ hàng rỗng!",
    );

    expect(navigate).not.toHaveBeenCalled();
  });

  test('TC9: Nhấn nút "Thanh toán ngay" khi giỏ hàng có sản phẩm', () => {
    const cartWithItems: CartType = {
      id: "CART-001",
      user: {
        id: "USER-001",
        username: "nhathuy",
        role: "USER",
      },
      totalQuantity: 3,
      totalPrice: 90000,
      cartItems: [cartItemFakeData1, cartItemFakeData2],
    };

    render(<CartSummaryComponent cart={cartWithItems} />);

    const checkoutButton = screen.getByRole("button", {
      name: "Thanh toán ngay",
    });

    fireEvent.click(checkoutButton);

    expect(toast.warning).not.toHaveBeenCalled();

    expect(navigate).toHaveBeenCalledTimes(1);
    expect(navigate).toHaveBeenCalledWith("/checkout");
  });
});
