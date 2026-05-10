import type { CartItemType } from "../types/cartItem";
import { formatPrice } from "../utils/priceCalculation";

interface CheckoutItemComponentProps {
  cartItem: CartItemType;
}

const CheckoutItemComponent: React.FC<CheckoutItemComponentProps> = ({
  cartItem,
}) => {
  const price = cartItem.product?.price || 0;
  const quantity = cartItem.quantity || 0;
  const total = price * quantity;

  return (
    <div className="bg-white rounded-lg shadow p-4 flex gap-4">
      {/* Image */}
      <div className="w-32 h-24 bg-gray-200 flex items-center justify-center rounded">
        <span
          data-testid={`checkout-item-product-image-${cartItem.id}`}
          className="text-gray-400 text-xs"
        >
          No Image
        </span>
      </div>
      {/* Product Info */}
      <div className="flex-1">
        <div className="flex align-center">
          <h3
            data-testid={`checkout-item-product-name-${cartItem.id}`}
            className="text-lg font-semibold text-gray-900"
          >
            {cartItem.product?.name}
          </h3>
          <p
            data-testid={`checkout-item-product-quantity-${cartItem.id}`}
            className="text-lg font-semibold text-gray-900 ml-2"
          >
            x {quantity}
          </p>
        </div>
        <p
          data-testid={`checkout-item-product-category-${cartItem.id}`}
          className="text-sm text-gray-500"
        >
          {cartItem.product?.category?.name}
        </p>
        <p
          data-testid={`checkout-item-product-price-${cartItem.id}`}
          className="font-bold text-indigo-600"
        >
          {formatPrice(price)}
        </p>
      </div>
      {/* Total */}
      <div className="text-right self-end">
        <p className="text-sm text-gray-500">Thành tiền</p>
        <p
          data-testid={`checkout-item-product-total-${cartItem.id}`}
          className="text-xl font-bold text-indigo-600"
        >
          {formatPrice(total)}
        </p>
      </div>
    </div>
  );
};

export default CheckoutItemComponent;
