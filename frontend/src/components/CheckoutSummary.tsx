import { useMemo, useState } from "react";
import { toast } from "react-toastify";
import { HttpStatusCode } from "axios";
import PriceCalculatorComponent from "./PriceCalculator";
import { CouponApi } from "../services/api/couponApi";
import { OrderApi } from "../services/api/orderApi";
import { OrderCalculation } from "../services/calculation/orderCalculation";
import type { CartItemType } from "../types/cartItem";
import type { OrderCreateRequest } from "../types/order";
import type { OrderItemRequest } from "../types/orderItem";
import type { CouponType } from "../types/coupon";
import { formatPrice } from "../utils/priceCalculation";
import { useNavigate } from "react-router-dom";

const shippingFeeMap: any = {
  standard: 20000,
  fast: 40000,
  express: 60000,
};

type CheckoutSummaryComponentProps = {
  userId: string;
  subtotal: number;
  cartItems: CartItemType[];
  fetchCartByUserId: (userId: string) => Promise<void>;
};

const CheckoutSummaryComponent: React.FC<CheckoutSummaryComponentProps> = ({
  userId,
  subtotal,
  cartItems,
  fetchCartByUserId,
}) => {
  const navigate = useNavigate();

  // States
  const [shippingAddress, setShippingAddress] = useState<string>("");
  const [shippingMethod, setShippingMethod] = useState<string>("standard");
  const [paymentMethod, setPaymentMethod] = useState<string>("cod");
  const [couponCode, setCouponCode] = useState<string>("");
  const [coupon, setCoupon] = useState<CouponType>();
  // Memos
  const orderItemRequests = useMemo(() => {
    return cartItems?.map(
      (cartItem) =>
        ({
          productId: cartItem.product?.id,
          quantity: cartItem.quantity,
          price: cartItem.product?.price,
        }) as OrderItemRequest,
    );
  }, [cartItems]);
  const orderTotal = useMemo(() => {
    return OrderCalculation.calculateOrderTotal({
      orderItems: orderItemRequests,
      shippingFee: shippingFeeMap[shippingMethod],
      coupon: coupon,
    });
  }, [orderItemRequests, shippingMethod, coupon]);

  const applyCoupon = async () => {
    if (coupon) {
      toast.warning(
        "Chỉ được áp dụng 1 mã giảm giá. Hãy xóa mã hiện tại trước.",
      );

      return;
    }

    const response = await CouponApi.getCouponByCode(couponCode);
    if (response.status === 200 && response.data) {
      setCoupon(response.data);

      toast.success("Áp dụng mã giảm giá thành công!");
    } else if ((response as any).error) {
      toast.error((response as any).message);
    }
  };
  const handleCheckout = async () => {
    if (!shippingAddress) {
      toast.warning("Cần nhập địa chỉ giao hàng!");

      return;
    }

    const confirmCancel = window.confirm(
      "Bạn có chắc muốn xoá sản phẩm này khỏi giỏ hàng này không?",
    );
    if (!confirmCancel) return;

    const response = await OrderApi.createOrder({
      userId: userId,
      couponId: coupon?.id || null,
      shippingAddress: shippingAddress,
      shippingMethod:
        shippingMethod === "standard"
          ? "Tiêu chuẩn"
          : shippingMethod === "fast"
            ? "Nhanh"
            : "Hoả tốc",
      shippingFee: orderTotal.shippingFee,
      paymentMethod:
        paymentMethod === "cod"
          ? "Thanh toán khi nhận hàng"
          : "Thanh toán chuyển khoản ngân hàng",
      orderItems: orderItemRequests,
    } as OrderCreateRequest);
    if (response.status === HttpStatusCode.Created && response.data) {
      await fetchCartByUserId(userId);

      setShippingAddress("");
      setShippingMethod("standard");
      setPaymentMethod("cod");
      setCouponCode("");
      setCoupon(undefined);

      toast.success((response as any).message);

      navigate("/products");
    } else if ((response as any).error) {
      toast.error((response as any).message);
    }
  };

  return (
    <div className="bg-white rounded-lg shadow p-6 space-y-4 h-fit sticky top-4">
      <h2 className="text-xl font-bold">Thanh toán</h2>
      {/* Shipping Address */}
      <div className="space-y-1">
        <label className="text-sm font-medium text-gray-700">
          Địa chỉ giao hàng
        </label>
        <input
          type="text"
          placeholder="Nhập địa chỉ nhận hàng"
          value={shippingAddress}
          data-testid="checkout-summary-shipping-address-input"
          onChange={(e) => setShippingAddress(e.target.value)}
          className="w-full border border-gray-300 p-2 rounded focus:ring-2 focus:ring-indigo-500 outline-none"
        />
      </div>
      {/* Shipping Method */}
      <div className="space-y-1">
        <label className="text-sm font-medium text-gray-700">
          Phương thức vận chuyển
        </label>
        <select
          value={shippingMethod}
          onChange={(e) => setShippingMethod(e.target.value)}
          className="w-full border border-gray-300 p-2 rounded focus:ring-2 focus:ring-indigo-500 outline-none"
        >
          <option value="standard">Tiêu chuẩn (20k)</option>
          <option value="fast">Nhanh (40k)</option>
          <option value="express">Hỏa tốc (60k)</option>
        </select>
      </div>
      {/* Payment Method */}
      <div className="space-y-1">
        <label className="text-sm font-medium text-gray-700">
          Phương thức thanh toán
        </label>
        <select
          value={paymentMethod}
          onChange={(e) => setPaymentMethod(e.target.value)}
          className="w-full border border-gray-300 p-2 rounded focus:ring-2 focus:ring-indigo-500 outline-none"
        >
          <option value="cod">Thanh toán khi nhận hàng</option>
          <option value="bank">Thanh toán chuyển khoản ngân hàng</option>
        </select>
      </div>
      {/* Coupon */}
      <div className="space-y-1">
        <label className="text-sm font-medium text-gray-700">Mã giảm giá</label>
        <div className="flex gap-2">
          <input
            value={couponCode}
            onChange={(e) => setCouponCode(e.target.value)}
            placeholder="Nhập mã (nếu có)"
            data-testid="checkout-summary-coupon-input"
            className="flex-1 border border-gray-300 p-2 rounded focus:ring-2 focus:ring-indigo-500 outline-none"
          />
          <button
            onClick={applyCoupon}
            data-testid="checkout-summary-coupon-button"
            className="bg-gray-200 hover:bg-gray-300 px-3 rounded font-medium"
          >
            Áp dụng
          </button>
        </div>
        <div className="bg-green-50 border border-green-200 p-2 rounded text-sm text-green-700 flex justify-between items-center">
          {/* <span data-testid="checkout-summary-coupon-result">
            {coupon ? (
              <>
                {coupon.code} -{" "}
                {coupon.type === "Giảm tiền cố định"
                  ? `Giảm ${formatPrice(coupon.discount || 0)}`
                  : `Giảm ${coupon.discount}%`}
              </>
            ) : (
              ""
            )}
          </span> */}
          {coupon && (
          <div className="bg-green-50 border border-green-200 p-2 rounded text-sm text-green-700 flex justify-between items-center">
            <span data-testid="checkout-summary-coupon-result">
              {coupon.code} -{" "}
              {coupon.type === "Giảm tiền cố định"
                ? `Giảm ${formatPrice(coupon.discount || 0)}`
                : `Giảm ${coupon.discount}%`}
            </span>
            <button
              onClick={() => setCoupon(undefined)}
              className="text-red-500 text-xs font-medium hover:underline"
            >
              Xóa
            </button>
          </div>
        )}
          <button
            onClick={() => setCoupon(undefined)}
            className="text-red-500 text-xs font-medium hover:underline"
          >
            Xóa
          </button>
        </div>
      </div>
      {/* Summary */}
      <div className="space-y-2 border-t pt-3">
        <PriceCalculatorComponent
          subtotal={subtotal}
          shippingFee={orderTotal.shippingFee}
          discount={orderTotal.discount}
        />
      </div>
      {/* Button */}
      <button
        onClick={handleCheckout}
        data-testid="checkout-summary-order-button"
        className="w-full bg-indigo-600 text-white font-bold py-3 rounded"
      >
        Đặt hàng
      </button>
    </div>
  );
};

export default CheckoutSummaryComponent;
