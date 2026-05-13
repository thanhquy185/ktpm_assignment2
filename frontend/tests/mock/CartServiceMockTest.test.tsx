import { describe, test, expect, vi, beforeEach } from "vitest";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import { CartApi } from "../../src/services/api/cartApi";
import { HttpStatusCode } from "axios";
import { CartItemAddToCartRequest } from "../../src/types/cartItem";
import ProductsComponent from "../../src/components/Products";

const productFakeData1 = {
  id: "PRO-001",
  name: "Laptop Dell",
  category: { id: "CAT-001", name: "Laptop" },
  inventory: { id: "INV-001", stock: 5 },
  price: 20000,
  status: "Đang bán",
  description: "Laptop sinh viên",
};

const navigate = vi.fn();

vi.mock("../../src/services/api/cartApi", () => ({
  CartApi: {
    addToCart: vi.fn(),
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
  test("TC1: Thêm sản phẩm thành công", async () => {
    vi.mocked(CartApi.addToCart).mockResolvedValue({
      status: HttpStatusCode.Created,
      error: null,
      message: "Add product to cart is successful!",
      data: {
        id: "CART-001",
      },
    } as any);

    render(
      <ProductsComponent
        userId="USER-001"
        products={[productFakeData1]}
        fetchProducts={vi.fn()}
      />,
    );

    const addButton = screen.getByTestId("product-card-button-PRO-001");
    fireEvent.click(addButton);

    await waitFor(() => {
      expect(CartApi.addToCart).toHaveBeenCalledTimes(1);
      expect(CartApi.addToCart).toHaveBeenCalledWith("USER-001", {
        productId: "PRO-001",
        quantity: 1,
      });

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

    render(
      <ProductsComponent
        userId="USER-001"
        products={[productFakeData1]}
        fetchProducts={vi.fn()}
      />,
    );

    const addButton = screen.getByTestId("product-card-button-PRO-001");

    fireEvent.click(addButton);

    await waitFor(() => {
      expect(CartApi.addToCart).toHaveBeenCalledTimes(1);
      expect(toast.error).toHaveBeenCalledTimes(1);

      expect(toast.error).toHaveBeenCalledWith("Product not found!");
    });
  });

  test("TC3: Thêm sản phẩm nhưng số lượng sản phẩm bé hơn 0", async () => {
    vi.mocked(CartApi.addToCart).mockResolvedValue({
      status: HttpStatusCode.BadRequest,
      error: "CART_ITEM_QUANTITY_GREATER_THAN_ZERO",
      message: "Quantity must be greater than 0!",
      data: null,
    } as any);

    render(
      <ProductsComponent
        userId="USER-001"
        products={[productFakeData1]}
        fetchProducts={vi.fn()}
      />,
    );

    const addButton = screen.getByTestId("product-card-button-PRO-001");

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

    render(
      <ProductsComponent
        userId="USER-001"
        products={[productFakeData1]}
        fetchProducts={vi.fn()}
      />,
    );

    const addButton = screen.getByTestId("product-card-button-PRO-001");

    fireEvent.click(addButton);

    await waitFor(() => {
      expect(CartApi.addToCart).toHaveBeenCalledTimes(1);
      expect(toast.error).toHaveBeenCalledWith("Quantity cannot be 0!");
    });
  });

  test("TC5: Thêm sản phẩm nhưng tồn kho của sản phẩm không tồn tại", async () => {
    vi.mocked(CartApi.addToCart).mockResolvedValue({
      status: HttpStatusCode.NotFound,
      error: "PRODUCT_NOT_FOUND_IN_INVENTORY",
      message: "Product not found in inventory!",
      data: null,
    } as any);

    render(
      <ProductsComponent
        userId="USER-001"
        products={[productFakeData1]}
        fetchProducts={vi.fn()}
      />,
    );

    const addButton = screen.getByTestId("product-card-button-PRO-001");

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

    render(
      <ProductsComponent
        userId="USER-001"
        products={[productFakeData1]}
        fetchProducts={vi.fn()}
      />,
    );

    const addButton = screen.getByTestId("product-card-button-PRO-001");

    fireEvent.click(addButton);

    await waitFor(() => {
      expect(CartApi.addToCart).toHaveBeenCalledTimes(1);
      expect(toast.error).toHaveBeenCalledWith("Insufficient stock!");
    });
  });

  test("TC7: Thêm sản phẩm nhưng người dùng không tồn tại", async () => {
    vi.mocked(CartApi.addToCart).mockResolvedValue({
      status: HttpStatusCode.BadRequest,
      error: "USER_NOT_FOUND",
      message: "User ID USER-001 not found!",
      data: {
        id: "CART-001",
      },
    } as any);

    const fetchProductsByUserId = vi.fn();

    render(
      <ProductsComponent
        userId="USER-001"
        products={[productFakeData1]}
        fetchProducts={fetchProductsByUserId}
      />,
    );

    const addButton = screen.getByTestId("product-card-button-PRO-001");
    fireEvent.click(addButton);

    await waitFor(() => {
      expect(CartApi.addToCart).toHaveBeenCalledTimes(1);
      expect(CartApi.addToCart).toHaveBeenCalledWith("USER-001", {
        productId: "PRO-001",
        quantity: 1,
      } as CartItemAddToCartRequest);
    });

    expect(fetchProductsByUserId).not.toHaveBeenCalled();
    expect(toast.error).toHaveBeenCalledTimes(1);
    expect(toast.error).toHaveBeenCalledWith("User ID USER-001 not found!");
  });

  // test("TC8: Thêm sản phẩm đã có trong giỏ (cộng dồn số lượng)", async () => {
  //   vi.mocked(CartApi.addToCart).mockResolvedValue({
  //     status: HttpStatusCode.Ok,
  //     error: null,
  //     message: "Update product quantity in cart is successful!",
  //     data: {
  //       id: "CART-001",
  //     },
  //   } as any);

  //   const fetchProductsByUserId = vi.fn();

  //   render(
  //     <ProductsComponent
  //       userId="USER-001"
  //       products={[productFakeData1]}
  //       fetchProducts={fetchProductsByUserId}
  //     />,
  //   );

  //   const addButton = screen.getByTestId("product-card-button-PRO-001");
  //   fireEvent.click(addButton);

  //   await waitFor(() => {
  //     expect(CartApi.addToCart).toHaveBeenCalledTimes(1);
  //     expect(CartApi.addToCart).toHaveBeenCalledWith("USER-001", {
  //       productId: "PRO-001",
  //       quantity: 1,
  //     } as CartItemAddToCartRequest);
  //   });

  //   expect(fetchProductsByUserId).toHaveBeenCalledTimes(1);
  //   expect(fetchProductsByUserId).toHaveBeenCalledWith("USER-001");
  //   expect(toast.success).toHaveBeenCalledTimes(1);
  //   expect(toast.success).toHaveBeenCalledWith(
  //     "Update product quantity in cart is successful!",
  //   );
  // });

  test("TC8: Thêm sản phẩm đã có trong giỏ nhưng tồn kho của sản phẩm không đủ", async () => {
    vi.mocked(CartApi.addToCart).mockResolvedValue({
      status: HttpStatusCode.BadRequest,
      error: "INSUFFICIENT_STOCK",
      message: "Insufficient stock!",
      data: null,
    } as any);

    const fetchProductsByUserId = vi.fn();

    render(
      <ProductsComponent
        userId="USER-001"
        products={[productFakeData1]}
        fetchProducts={fetchProductsByUserId}
      />,
    );

    const addButton = screen.getByTestId("product-card-button-PRO-001");
    fireEvent.click(addButton);

    await waitFor(() => {
      expect(CartApi.addToCart).toHaveBeenCalledTimes(1);
      expect(CartApi.addToCart).toHaveBeenCalledWith("USER-001", {
        productId: "PRO-001",
        quantity: 1,
      } as CartItemAddToCartRequest);
    });

    expect(fetchProductsByUserId).not.toHaveBeenCalled();
    expect(toast.error).toHaveBeenCalledTimes(1);
    expect(toast.error).toHaveBeenCalledWith("Insufficient stock!");
  });
});
