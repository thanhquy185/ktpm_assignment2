import type { CartItemType } from "../types/cartItem";
import CheckoutItemComponent from "./CheckoutItem";

type CheckoutComponentProps = {
  cartItems: CartItemType[];
};

const CheckoutComponent: React.FC<CheckoutComponentProps> = ({ cartItems }) => {
  return (
    <div className="lg:col-span-2 space-y-4">
      <h1 className="text-3xl font-bold text-gray-900 mb-2">
        Danh sách sản phẩm
      </h1>
      {cartItems?.map((cartItem: CartItemType) => (
        <CheckoutItemComponent key={cartItem?.id} cartItem={cartItem} />
      ))}
    </div>
  );
};

export default CheckoutComponent;
