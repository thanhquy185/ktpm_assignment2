import { describe, test, expect, vi, beforeEach } from "vitest";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { MemoryRouter, useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import { CartApi } from "../../src/services/api/cartApi";
import CartComponent from "../../src/components/Cart";

// mocks
vi.mock("../services/api/cartApi");
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
    error: vi.fn(),
  },
}));

describe("Cart Component Integration Tests", () => {
  // test("TC1: Giỏ hàng rỗng", () => {
  //   render(
  //     <MemoryRouter>
  //       <CartComponent
  //         userId="user01"
  //         cartItems={[]}
  //         fetchCartByUserId={vi.fn()}
  //       />
  //     </MemoryRouter>,
  //   );
  //   expect(screen.getByTestId("empty-cart-inform")).toBeInTheDocument();
  // });
  // test("TC2: Click 'tiếp tục mua sắm' khi giỏ hàng rỗng", () => {
  //   const navigate = vi.fn();
  //   vi.mocked(useNavigate).mockReturnValue(navigate);
  //   render(
  //     <MemoryRouter>
  //       <CartComponent
  //         userId="user01"
  //         cartItems={[]}
  //         fetchCartByUserId={vi.fn()}
  //       />
  //     </MemoryRouter>,
  //   );
  //   fireEvent.click(screen.getByTestId("go-to-products-page-button"));
  //   expect(navigate).toHaveBeenCalledWith("/products");
  // });
});
