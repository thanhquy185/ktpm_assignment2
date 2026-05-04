import { toast } from "react-toastify";
import { Minus, Plus, Trash2 } from "lucide-react";
import type { CartItemType } from "../types/cartItem";
import { formatPrice } from "../utils/priceCalculation";

interface CartItemComponentProps {
  item: CartItemType;
  onUpdate: (productId: string, quantity: number) => void;
  onRemove: (productId: string) => void;
}

const CartItemComponent: React.FC<CartItemComponentProps> = ({
  item,
  onUpdate,
  onRemove,
}) => {
  return (
    <div className="bg-white rounded-lg shadow p-4 flex gap-4">
      {/* Image */}
      <div className="w-32 h-24 bg-gray-200 flex items-center justify-center rounded">
        <span className="text-gray-400 text-xs">No Img</span>
      </div>
      {/* Info */}
      <div className="flex-1">
        <h3 className="text-lg font-semibold text-gray-900">
          {item.product?.name}
        </h3>
        <h3 className="text-xm text-gray-500">
          {item.product?.category?.name}
        </h3>
        <p className="text-lg text-indigo-600 font-bold">
          {formatPrice(item.product?.price || 0)}
        </p>
      </div>
      {/* Quantity */}
      <div className="flex items-center gap-2">
        <button
          type="button"
          className="p-1 hover:bg-gray-100 rounded"
          onClick={async (e) => {
            e.preventDefault();

            if (!item.quantity) return;

            await onUpdate(item.product?.id!, item.quantity - 1);
          }}
        >
          <Minus size={20} />
        </button>
        <input
          min="1"
          value={item.quantity}
          onChange={async (e) => {
            e.preventDefault();

            const newQuantity = parseInt(e.target.value);
            if (isNaN(newQuantity)) {
              toast.warning("Số lượng sản phẩm trong giỏ phải là số hợp lệ!");
            }

            await onUpdate(item.product?.id!, newQuantity);
          }}
          className="w-10 h-10 text-center bg-gray-100 rounded outline-none"
        />
        <button
          type="button"
          className="p-1 hover:bg-gray-100 rounded"
          onClick={async (e) => {
            e.preventDefault();

            if (!item.quantity) return;

            await onUpdate(item.product?.id!, item.quantity + 1);
          }}
        >
          <Plus size={20} />
        </button>
      </div>
      {/* Remove */}
      <button
        onClick={() => onRemove(item.product?.id!)}
        className="text-red-500 hover:text-red-600"
      >
        <Trash2 size={20} />
      </button>
    </div>
  );
};

export default CartItemComponent;
