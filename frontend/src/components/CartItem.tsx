import { Minus, Plus, Trash2 } from "lucide-react";
import type { CartItemType } from "../types/cartItem";
import { formatPrice } from "../utils/priceCalculation";

interface CartItemComponentProps {
  cartItem: CartItemType;
  onUpdateQuantity: (productId: string, quantity: number) => void;
  onRemoveItem: (productId: string) => void;
}

const CartItemComponent: React.FC<CartItemComponentProps> = ({
  cartItem,
  onUpdateQuantity,
  onRemoveItem,
}) => {
  return (
    <div className="bg-white rounded-lg shadow p-4 flex gap-4">
      {/* Image */}
      <div className="w-32 h-24 bg-gray-200 flex items-center justify-center rounded">
        <span
          data-testId={`cart-item-product-image-${cartItem.id}`}
          className="text-gray-400 text-xs"
        >
          No Image
        </span>
      </div>
      {/* Info */}
      <div className="flex-1">
        <h3
          data-testId={`cart-item-product-name-${cartItem.id}`}
          className="text-lg font-semibold text-gray-900"
        >
          {cartItem.product?.name}
        </h3>
        <h3
          data-testId={`cart-item-product-category-${cartItem.id}`}
          className="text-xm text-gray-500"
        >
          {cartItem.product?.category?.name}
        </h3>
        <p
          data-testId={`cart-item-product-price-${cartItem.id}`}
          className="text-lg text-indigo-600 font-bold"
        >
          {formatPrice(cartItem.product?.price || 0)}
        </p>
      </div>
      {/* Quantity */}
      <div className="flex items-center gap-2">
        <button
          type="button"
          data-testId={`cart-item-decrease-quantity-button-${cartItem.id}`}
          className="p-1 hover:bg-gray-100 rounded"
          onClick={async (e) => {
            e.preventDefault();

            if (!cartItem.quantity) return;

            await onUpdateQuantity(
              cartItem.product?.id!,
              cartItem.quantity - 1,
            );
          }}
        >
          <Minus size={20} />
        </button>
        {/* <input
          min="1"
          value={cartItem.quantity}
          onChange={async (e) => {
            e.preventDefault();

            const newQuantity = parseInt(e.target.value);
            if (isNaN(newQuantity)) {
              toast.warning("Số lượng sản phẩm trong giỏ phải là số hợp lệ!");
            }

            await onUpdateQuantity(cartItem.product?.id!, newQuantity);
          }}
          className="w-10 h-10 text-center bg-gray-100 rounded outline-none"
        /> */}
        <span
          data-testId={`cart-item-quantity-${cartItem.id}`}
          className="w-10 h-10 leading-10 text-center bg-gray-100 rounded outline-none"
        >
          {cartItem.quantity}
        </span>
        <button
          type="button"
          data-testId={`cart-item-increase-quantity-button-${cartItem.id}`}
          className="p-1 hover:bg-gray-100 rounded"
          onClick={async (e) => {
            e.preventDefault();

            if (!cartItem.quantity) return;

            await onUpdateQuantity(
              cartItem.product?.id!,
              cartItem.quantity + 1,
            );
          }}
        >
          <Plus size={20} />
        </button>
      </div>
      {/* Remove */}
      <button
        data-testId={`cart-item-remove-button-${cartItem.id}`}
        className="text-red-500 hover:text-red-600"
        onClick={() => onRemoveItem(cartItem.product?.id!)}
      >
        <Trash2 size={20} />
      </button>
    </div>
  );
};

export default CartItemComponent;
