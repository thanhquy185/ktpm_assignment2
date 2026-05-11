import { describe, test, expect, vi, beforeEach } from "vitest";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import { CartApi } from "../../src/services/api/cartApi";
import { ProductApi } from "../../src/services/api/productApi";
import { formatPrice } from "../../src/utils/priceCalculation";
import { HttpStatusCode } from "axios";
import {
  CartItemAddToCartRequest,
  CartItemType,
} from "../../src/types/cartItem";
import CartComponent from "../../src/components/Cart";
import ProductsPage from "../../src/pages/ProductsPage"
const productFakeData1 = {
  id: "PRO-001",
  name: "Laptop Dell",
  category: { id: "CAT-001", name: "Laptop" },
  inventory: { id: "INV-001", stock: 5 },
  price: 20000,
  status: "Đang bán",
  description: "Laptop sinh viên",
};

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
    addToCart: vi.fn(),
    updateQuantity: vi.fn(),
    removeFromCart: vi.fn(),
  },
}));
vi.mock("../../src/services/api/productApi", () => ({
  ProductApi: {
    getAllProduct: vi.fn(),
  },
}));
vi.mock("../../src/contexts/AuthContext", () => ({
  useAuth: () => ({
    user: {
      id: "USER-001",
    },
  }),
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
  vi.mocked(ProductApi.getAllProduct).mockResolvedValue({
    status: HttpStatusCode.Ok,
    data: [productFakeData1],
  } as any);
});

describe("Cart Component Integration Tests", () => {
  test("TC1: Thêm sản phẩm thành công", async () => {
    vi.mocked(CartApi.addToCart).mockResolvedValue({
      status: HttpStatusCode.Created,
      error: null,
      message: "Add product to cart is successful!",
      data: {
        id: "CART-001",
      },
    } as any);

    render(<ProductsPage />);

    const addButton = await screen.findByTestId(
      "add-to-cart-btn-PRO-001",
    );

    fireEvent.click(addButton);

    await waitFor(() => {
      expect(CartApi.addToCart).toHaveBeenCalledTimes(1);

      expect(CartApi.addToCart).toHaveBeenCalledWith(
        "USER-001",
        {
          productId: "PRO-001",
          quantity: 1,
        },
      );
      expect(toast.success).toHaveBeenCalledTimes(1);

      expect(toast.success).toHaveBeenCalledWith(
        "Thêm sản phẩm vào giỏ hàng thành công!",
      );
    });
  });

  test("TC2: Thêm sản phẩm nhưng sản phẩm không tồn tại", async () => {
    vi.mocked(CartApi.addToCart).mockResolvedValue({
      status: HttpStatusCode.NotFound,
      error: "PRODUCT_NOT_FOUND",
      message: "Product not found!",
      data: null,
    } as any);

    render(<ProductsPage />);

    const addButton = await screen.findByTestId(
      "add-to-cart-btn-PRO-001",
    );

    fireEvent.click(addButton);

    await waitFor(() => {
      expect(CartApi.addToCart).toHaveBeenCalledTimes(1);
      expect(toast.error).toHaveBeenCalledTimes(1);

      expect(toast.error).toHaveBeenCalledWith(
        "Product not found!",
      );
    });
  });

  test("TC3: Thêm sản phẩm nhưng số lượng sản phẩm bé hơn 0", async () => {
    vi.mocked(CartApi.addToCart).mockResolvedValue({
      status: HttpStatusCode.BadRequest,
      error: "CART_ITEM_QUANTITY_GREATER_THAN_ZERO",
      message: "Quantity must be greater than 0!",
      data: null,
    } as any);

    render(<ProductsPage />);

    const addButton = await screen.findByTestId(
      "add-to-cart-btn-PRO-001",
    );

    fireEvent.click(addButton);

    await waitFor(() => {
      expect(CartApi.addToCart).toHaveBeenCalledTimes(1);
      expect(toast.error).toHaveBeenCalledWith(
        "Quantity must be greater than 0!",
      );
    });
  });

  test("TC4: Thêm sản phẩm nhưng số lượng sản phẩm bằng 0", async () => {
    vi.mocked(CartApi.addToCart).mockResolvedValue({
      status: HttpStatusCode.BadRequest,
      error: "CART_ITEM_QUANTITY_GREATER_THAN_ZERO",
      message: "Quantity cannot be 0!",
      data: null,
    } as any);

    render(<ProductsPage />);

    const addButton = await screen.findByTestId(
      "add-to-cart-btn-PRO-001",
    );

    fireEvent.click(addButton);

    await waitFor(() => {
      expect(CartApi.addToCart).toHaveBeenCalledTimes(1);
      expect(toast.error).toHaveBeenCalledWith(
        "Quantity cannot be 0!",
      );
    });
  });

  test("TC5: Thêm sản phẩm nhưng tồn kho của sản phẩm không tồn tại", async () => {
    vi.mocked(CartApi.addToCart).mockResolvedValue({
      status: HttpStatusCode.NotFound,
      error: "PRODUCT_NOT_FOUND_IN_INVENTORY",
      message: "Product not found in inventory!",
      data: null,
    } as any);

    render(<ProductsPage />);

    const addButton = await screen.findByTestId(
      "add-to-cart-btn-PRO-001",
    );

    fireEvent.click(addButton);

    await waitFor(() => {
      expect(CartApi.addToCart).toHaveBeenCalledTimes(1);
      expect(toast.error).toHaveBeenCalledWith(
        "Product not found in inventory!",
      );
    });
  });

  test("TC6: Thêm sản phẩm nhưng tồn kho của sản phẩm không đủ", async () => {
    vi.mocked(CartApi.addToCart).mockResolvedValue({
      status: HttpStatusCode.BadRequest,
      error: "INSUFFICIENT_STOCK",
      message: "Insufficient stock!",
      data: null,
    } as any);

    render(<ProductsPage />);

    const addButton = await screen.findByTestId(
      "add-to-cart-btn-PRO-001",
    );

    fireEvent.click(addButton);

    await waitFor(() => {
      expect(CartApi.addToCart).toHaveBeenCalledTimes(1);
      expect(toast.error).toHaveBeenCalledWith(
        "Insufficient stock!",
      );
    });

  });
  test("TC7: Thêm sản phẩm nhưng người dùng không tồn tại", async () => {
    vi.mocked(CartApi.updateQuantity).mockResolvedValue({
      status: HttpStatusCode.BadRequest,
      error: "USER_NOT_FOUND",
      message: "User ID USER-001 not found!",
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
      expect(CartApi.updateQuantity).toHaveBeenCalledTimes(1);
      expect(CartApi.updateQuantity).toHaveBeenCalledWith("USER-001", {
        productId: "PRO-001",
        quantity: 3,
      } as CartItemAddToCartRequest);
    });

    expect(fetchCartByUserId).not.toHaveBeenCalled();
    expect(toast.error).toHaveBeenCalledTimes(1);
    expect(toast.error).toHaveBeenCalledWith("User ID USER-001 not found!");
  });

  test("TC8: Thêm sản phẩm đã có trong giỏ (cộng dồn số lượng)", async () => {
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
      expect(CartApi.updateQuantity).toHaveBeenCalledTimes(1);
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

  test("TC9: Thêm sản phẩm đã có trong giỏ nhưng tồn kho của sản phẩm không đủ", async () => {
    vi.mocked(CartApi.updateQuantity).mockResolvedValue({
      status: HttpStatusCode.BadRequest,
      error: "INSUFFICIENT_STOCK",
      message: "Insufficient stock!",
      data: null,
    } as any);

    const fetchCartByUserId = vi.fn();

    render(
      <CartComponent
        userId="USER-001"
        cartItems={[
          {
            ...cartItemFakeData1,
            quantity: 5, // Ban đầu là 2, thay đổi thành 5 khi tăng lên 6 sẽ lỗi
          },
        ]}
        fetchCartByUserId={fetchCartByUserId}
      />,
    );

    const increaseButton = screen.getByTestId(
      "cart-item-increase-quantity-button-CI-001",
    );
    fireEvent.click(increaseButton);

    await waitFor(() => {
      expect(CartApi.updateQuantity).toHaveBeenCalledTimes(1);
      expect(CartApi.updateQuantity).toHaveBeenCalledWith("USER-001", {
        productId: "PRO-001",
        quantity: 6,
      } as CartItemAddToCartRequest);
    });

    expect(fetchCartByUserId).not.toHaveBeenCalled();
    expect(toast.error).toHaveBeenCalledTimes(1);
    expect(toast.error).toHaveBeenCalledWith("Insufficient stock!");
  });
});
