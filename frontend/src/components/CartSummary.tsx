import type { CartType } from "../types/cart";
import { formatPrice } from "../utils/priceCalculation";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";

type CartSummaryComponentProps = {
  cart: CartType;
};

const CartSummaryComponent: React.FC<CartSummaryComponentProps> = ({
  cart,
}) => {
  const navigate = useNavigate();

  return (
    <div className="bg-white rounded-lg shadow p-6 space-y-4 h-fit sticky top-4">
      {/* Title */}
      <h2 className="text-xl font-bold mb-6">Tóm tắt đơn hàng</h2>
      {/* Total quantity */}
      <div className="flex items-center justify-between mb-4">
        <span className="text-gray-500">Tổng số lượng:</span>
        <span
          data-testid="cart-summary-total-quantity"
          className="font-semibold text-gray-800"
        >
          {cart.totalQuantity || 0} sản phẩm
        </span>
      </div>
      {/* Total price */}
      <div className="flex items-center justify-between border-b border-dashed pb-5 mb-6">
        <span className="text-gray-500">Tổng tiền:</span>
        <span
          data-testid="cart-summary-total-price"
          className="text-2xl font-bold text-indigo-500"
        >
          {formatPrice(cart.totalPrice || 0)}
        </span>
      </div>
      {/* Checkout button */}
      <button
        type="button"
        className="w-full bg-indigo-600 text-white font-bold py-3 rounded"
        onClick={(e) => {
          e.preventDefault();

          if (cart.totalQuantity === 0) {
            toast.warning("Không thể thanh toán khi giỏ hàng rỗng!");
            return;
          }

          navigate("/checkout");
        }}
      >
        Thanh toán ngay
      </button>
    </div>
  );
};

export default CartSummaryComponent;
