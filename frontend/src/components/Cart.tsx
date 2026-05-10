import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import CartItemComponent from "./CartItem";
import { CartApi } from "../services/api/cartApi";
import type { CartItemType } from "../types/cartItem";

type CartComponentProps = {
  userId: string;
  cartItems: CartItemType[];
  fetchCartByUserId: (userId: string) => Promise<void>;
};

const CartComponent: React.FC<CartComponentProps> = ({
  userId,
  cartItems,
  fetchCartByUserId,
}) => {
  const navigate = useNavigate();

  const updateQuantity = async (productId: string, quantity: number) => {
    if (!userId) return;

    const confirmCancel = window.confirm(
      "Bạn có chắc muốn cập nhật số lượng sản phẩm này trong giỏ hàng không?",
    );
    if (!confirmCancel) return;

    if (quantity <= 0) {
      toast.warning("Không thể giảm số lượng nếu đang là 1 !");

      return;
    }

    const response = await CartApi.updateQuantity(userId, {
      productId: productId,
      quantity: quantity,
    });
    if (response.status === 200 && response.data) {
      await fetchCartByUserId(userId);
      toast.success((response as any).message);
    } else if ((response as any).error) {
      toast.error((response as any).message);
    }
  };
  const removeItem = async (productId: string) => {
    if (!userId) return;

    const confirmCancel = window.confirm(
      "Bạn có chắc muốn xoá sản phẩm này khỏi giỏ hàng này không?",
    );
    if (!confirmCancel) return;

    const response = await CartApi.removeFromCart(userId, {
      productId: productId,
    });
    if (response.status === 200 && response.data) {
      await fetchCartByUserId(userId);

      toast.success("Xoá sản phẩm trong giỏ thành công !");
    } else if ((response as any).error) {
      toast.error((response as any).message);
    }
  };

  return (
    <div className="lg:col-span-2 space-y-4">
      <h1 className="text-3xl font-bold text-gray-900 mb-2">Giỏ hàng</h1>
      {cartItems?.length === 0 ? (
        <div className="bg-white rounded-lg shadow p-8 text-center">
          <div className="text-5xl mb-3">🛒</div>
          <p
            data-testId="empty-cart-inform"
            className="text-gray-600 text-lg mb-4"
          >
            Giỏ hàng của bạn đang trống
          </p>
          <button
            data-testId="go-to-products-page-button"
            className="inline-block bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-2 px-6 rounded-lg transition"
            onClick={() => navigate("/products")}
          >
            Tiếp tục mua sắm
          </button>
        </div>
      ) : (
        cartItems?.map((cartItem: CartItemType) => (
          <CartItemComponent
            key={cartItem?.id}
            cartItem={cartItem}
            onUpdateQuantity={updateQuantity}
            onRemoveItem={removeItem}
          />
        ))
      )}
    </div>
  );
};

export default CartComponent;
