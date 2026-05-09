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
  return (
    <>
      <div className="flex justify-between font-bold text-lg border-t pt-2">
        <span>Tổng tiền</span>
        <span data-testId="price-calculator-total-price">
          {formatPrice(subtotal + shippingFee - discount)}
        </span>
      </div>
    </>
  );
};

export default PriceCalculatorComponent;
