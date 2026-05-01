import { Minus, Plus, Trash2 } from "lucide-react";

interface CartItemProps {
  item: any;
  onUpdate: (productId: string, quantity: number) => void;
  onRemove: (productId: string) => void;
}

export function CartItem({ item, onUpdate, onRemove }: CartItemProps) {
  return (
    <div className="bg-white rounded-lg shadow p-4 flex gap-4">
      {/* Image */}
      <div className="w-32 h-24 bg-gray-200 flex items-center justify-center rounded">
        <span className="text-gray-400 text-xs">No Img</span>
      </div>
      {/* Info */}
      <div className="flex-1">
        <h3 className="text-lg font-semibold text-gray-900">
          {item.productName}
        </h3>
        <h3 className="text-xm text-gray-500">{item.productCategory}</h3>
        <p className="text-lg text-indigo-600 font-bold">
          {item.price.toLocaleString("vi-VN")} đ
        </p>
      </div>
      {/* Quantity */}
      <div className="flex items-center gap-2">
        <button
          onClick={() => onUpdate(item.productId, item.quantity - 1)}
          className="p-1 hover:bg-gray-100 rounded"
        >
          <Minus size={20} />
        </button>
        <input
          // type="number"
          min="1"
          value={item.quantity}
          onChange={(e) => {
            const value = parseInt(e.target.value);
            if (!isNaN(value) && value >= 1) {
              onUpdate(item.productId, value);
            }
          }}
          className="w-10 h-10 text-center bg-gray-100 rounded outline-none"
        />
        <button
          onClick={() => onUpdate(item.productId, item.quantity + 1)}
          className="p-1 hover:bg-gray-100 rounded"
        >
          <Plus size={20} />
        </button>
      </div>
      {/* Remove */}
      <button
        onClick={() => onRemove(item.productId)}
        className="text-red-500 hover:text-red-600"
      >
        <Trash2 size={20} />
      </button>
    </div>
  );
}
