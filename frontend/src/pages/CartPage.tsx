import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import { useAuth } from "../contexts/AuthContext";
import CartItemComponent from "../components/CartItem";
import type { CartType } from "../types/cart";
import type { CartItemType } from "../types/cartItem";
import type { CouponType } from "../types/coupon";
import { CartService } from "../services/cartService";
import { CouponService } from "../services/couponService";
import { formatPrice } from "../utils/priceCalculation";
import { OrderService } from "../services/orderService";
import type { OrderRequest } from "../types/order";
import { HttpStatusCode } from "axios";
import type { OrderItemRequest } from "../types/orderItem";

const shippingFeeMap: any = {
  standard: 20000,
  fast: 40000,
  express: 60000,
};

const CartPage: React.FC = () => {
  const navigate = useNavigate();
  const { user, fetchUser } = useAuth();

  const [cart, setCart] = useState<CartType>();
  const [shippingAddress, setShippingAddress] = useState<string>("");
  const [shippingMethod, setShippingMethod] = useState<string>("standard");
  const [paymentMethod, setPaymentMethod] = useState<string>("cod");
  const [couponCode, setCouponCode] = useState<string>("");
  const [coupon, setCoupon] = useState<CouponType>();
  const [loading, setLoading] = useState<boolean>(true);

  const shippingFee = shippingFeeMap[shippingMethod];
  const discount = coupon?.discount || 0;
  const finalTotal = (cart?.totalPrice || 0) + shippingFee - discount;

  const updateQuantity = async (productId: string, quantity: number) => {
    if (!user?.id) return;

    const response = await CartService.updateQuantity(user.id, {
      productId: productId,
      quantity: quantity,
    });
    if (response.status === 200 && response.data) {
      await fetchUser();

      // const cartItemUpdated = response.data;
      // const newCartItems = cart?.cartItems?.map((cartItem: CartItemType) =>
      //   cartItem.product?.id === productId ? cartItemUpdated : cartItem,
      // );
      // const newTotalPrice = newCartItems?.reduce(
      //   (total: number, cartItem: CartItemType) =>
      //     total + (cartItem.product?.price || 0) * (cartItem.quantity || 0),
      //   0,
      // );
      // setCart({ cartItems: newCartItems, totalPrice: newTotalPrice });

      toast.success("Cập nhật số lượng sản phẩm trong giỏ thành công !");
    } else if ((response as any).error) {
      toast.error((response as any).message);
    }
  };
  const removeItem = async (id: string) => {
    if (!user?.id) return;

    const response = await CartService.removeToCart(user.id, {
      productId: id,
    });
    if (response.status === 200 && response.data) {
      await fetchUser();

      // const newCartItems = cart?.cartItems?.filter(
      //   (cartItem: CartItemType) => cartItem.product?.id !== id,
      // );
      // const newTotalPrice = newCartItems?.reduce(
      //   (total: number, cartItem: CartItemType) =>
      //     total + (cartItem.product?.price || 0) * (cartItem.quantity || 0),
      //   0,
      // );
      // setCart({ cartItems: newCartItems, totalPrice: newTotalPrice });

      toast.success("Xoá sản phẩm trong giỏ thành công !");
    } else if ((response as any).error) {
      toast.error((response as any).message);
    }
  };
  const applyCoupon = async () => {
    if (coupon) {
      toast.warning(
        "Chỉ được áp dụng 1 mã giảm giá. Hãy xóa mã hiện tại trước.",
      );

      return;
    }

    const response = await CouponService.getCouponByCode(couponCode);
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

    const response = await OrderService.createOrder({
      userId: user?.id,
      couponId: coupon?.id || null,
      shippingAddress: shippingAddress,
      shippingMethod:
        shippingMethod === "standard"
          ? "Tiêu chuẩn"
          : shippingMethod === "fast"
            ? "Nhanh"
            : "Hoả tốc",
      shippingFee: shippingFee,
      paymentMethod:
        paymentMethod === "cod"
          ? "Thanh toán khi nhận hàng"
          : "Thanh toán chuyển khoản ngân hàng",
      orderItems: cart?.cartItems?.map(
        (cartItem) =>
          ({
            productId: cartItem.product?.id,
            quantity: cartItem.quantity,
            price: cartItem.product?.price,
          }) as OrderItemRequest,
      ),
    } as OrderRequest);
    if (response.status === HttpStatusCode.Created && response.data) {
      fetchUser();
      toast.success("Đặt hàng thành công!");
    } else if ((response as any).error) {
      toast.error((response as any).message);
    }
  };

  useEffect(() => {
    setLoading(true);

    if (user && user.cart) {
      setCart(user.cart);
    }

    setLoading(false);
  }, [user]);

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        Đang tải giỏ hàng...
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 py-8 grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* LEFT - CART */}
        <div className="lg:col-span-2 space-y-4">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">Giỏ hàng</h1>
          {!cart || cart?.cartItems?.length === 0 ? (
            <div className="bg-white rounded-lg shadow p-8 text-center">
              <div className="text-5xl mb-3">🛒</div>
              <p className="text-gray-600 text-lg mb-4">
                Giỏ hàng của bạn đang trống
              </p>
              <button
                className="inline-block bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-2 px-6 rounded-lg transition"
                onClick={() => navigate("/products")}
              >
                Tiếp tục mua sắm
              </button>
            </div>
          ) : (
            cart?.cartItems?.map((cartItem: CartItemType) => (
              <CartItemComponent
                key={cartItem?.id}
                item={cartItem}
                onUpdate={updateQuantity}
                onRemove={removeItem}
              />
            ))
          )}
        </div>
        {/* RIGHT - CHECKOUT */}
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
              Phương thức vận chuyển
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
            <label className="text-sm font-medium text-gray-700">
              Mã giảm giá
            </label>
            <div className="flex gap-2">
              <input
                value={couponCode}
                onChange={(e) => setCouponCode(e.target.value)}
                placeholder="Nhập mã (nếu có)"
                className="flex-1 border border-gray-300 p-2 rounded focus:ring-2 focus:ring-indigo-500 outline-none"
              />
              <button
                onClick={applyCoupon}
                className="bg-gray-200 hover:bg-gray-300 px-3 rounded font-medium"
              >
                Áp dụng
              </button>
            </div>
            {coupon && (
              <div className="bg-green-50 border border-green-200 p-2 rounded text-sm text-green-700 flex justify-between items-center">
                <span>
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
          </div>
          {/* Summary */}
          <div className="space-y-2 border-t pt-3">
            <div className="flex justify-between">
              <span>Tiền hàng</span>
              <span>{formatPrice(cart?.totalPrice || 0)}</span>
            </div>
            <div className="flex justify-between">
              <span>Phí ship</span>
              <span>{formatPrice(shippingFee)}</span>
            </div>
            {coupon && (
              <div className="flex justify-between text-green-600">
                <span>Giảm giá</span>
                <span>{formatPrice(discount)}</span>
              </div>
            )}
            <div className="flex justify-between font-bold text-lg border-t pt-2">
              <span>Tổng tiền</span>
              <span>{formatPrice(finalTotal)}</span>
            </div>
          </div>
          {/* Button */}
          <button
            onClick={handleCheckout}
            className="w-full bg-indigo-600 text-white font-bold py-3 rounded"
          >
            Đặt hàng
          </button>
        </div>
      </div>
    </div>
  );
};

export default CartPage;
