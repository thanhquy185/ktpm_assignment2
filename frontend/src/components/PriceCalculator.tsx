import { useMemo } from "react";
import { formatPrice } from "../utils/priceCalculation";

type PriceCalculatorComponentProps = {
  subtotal: number;
  shippingFee: number;
  discount: number;
};

const PriceCalculatorComponent: React.FC<PriceCalculatorComponentProps> = ({
  subtotal,
  shippingFee,
  discount,
}) => {
  const totalPrice = useMemo(() => {
    return subtotal + shippingFee - discount;
  }, [subtotal, shippingFee, discount]);

  return (
    <>
      <div className="flex justify-between">
        <span>Tiền hàng</span>
        <span data-testId="checkout-summary-subtotal">
          {formatPrice(subtotal)}
        </span>
      </div>
      {subtotal < 0 && (
        <div className="bg-red-50 border border-red-200 p-2 rounded text-sm text-red-700 flex justify-center items-center">
          <span data-testId="checkout-summary-subtotal-negative-inform">
            Tiền hàng đang là số âm
          </span>
        </div>
      )}
      <div className="flex justify-between">
        <span>Phí ship</span>
        <span data-testId="checkout-summary-shipping-fee">
          {formatPrice(shippingFee)}
        </span>
      </div>
      {shippingFee < 0 && (
        <div className="bg-red-50 border border-red-200 p-2 rounded text-sm text-red-700 flex justify-center items-center">
          <span data-testId="checkout-summary-shipping-fee-negative-inform">
            Phí ship đang là số âm
          </span>
        </div>
      )}
      <div className="flex justify-between text-green-600">
        <span>Giảm giá</span>
        <span data-testId="checkout-summary-discount">
          {formatPrice(discount)}
        </span>
      </div>
      {discount < 0 && (
        <div className="bg-red-50 border border-red-200 p-2 rounded text-sm text-red-700 flex justify-center items-center">
          <span data-testId="checkout-summary-discount-negative-inform">
            Giảm giá đang là số âm
          </span>
        </div>
      )}
      <div className="flex justify-between font-bold text-lg border-t pt-2">
        <span>Tổng tiền</span>
        <span data-testId="checkout-summary-total-price">
          {formatPrice(totalPrice)}
        </span>
      </div>
      {totalPrice < 0 && (
        <div className="bg-red-50 border border-red-200 p-2 rounded text-sm text-red-700 flex justify-center items-center">
          <span data-testId="checkout-summary-total-price-negative-inform">
            Tổng tiền đang là số âm
          </span>
        </div>
      )}
    </>
  );
};

export default PriceCalculatorComponent;
