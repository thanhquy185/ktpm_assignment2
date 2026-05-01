import { useEffect, useState } from "react";
import api from "../services/api";
import { CartItem } from "../components/CartItem";

export function CartPage() {
  const [cart, setCart] = useState<any>(null);
  const [address, setAddress] = useState("");
  const [shipping, setShipping] = useState("standard");
  const [couponCode, setCouponCode] = useState("");
  const [coupon, setCoupon] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  const shippingFeeMap: any = {
    standard: 20000,
    fast: 40000,
    express: 60000,
  };

  useEffect(() => {
    const demoCart = {
      items: [
        {
          productId: "1",
          productName: "Son dưỡng môi",
          productCategory: "Mỹ phẩm",
          price: 120000,
          quantity: 2,
        },
        {
          productId: "2",
          productName: "Kem chống nắng SPF50",
          productCategory: "Mỹ phẩm",
          price: 250000,
          quantity: 1,
        },
      ],
      totalPrice: 490000,
    };
    setCart(demoCart);
    setLoading(false);

    const demoCoupon = {
      code: "SALE10",
      discount: 10000,
      description: "Giảm 10k cho đơn hàng",
    };
    setCoupon(demoCoupon);
  }, []);

  const updateQuantity = (id: string, qty: number) => {
    if (qty < 1) return;

    const updatedItems = cart.items.map((i: any) =>
      i.productId === id ? { ...i, quantity: qty } : i,
    );

    const total = updatedItems.reduce(
      (sum: number, i: any) => sum + i.price * i.quantity,
      0,
    );

    setCart({ items: updatedItems, totalPrice: total });
  };

  const removeItem = (id: string) => {
    const updatedItems = cart.items.filter((i: any) => i.productId !== id);

    const total = updatedItems.reduce(
      (sum: number, i: any) => sum + i.price * i.quantity,
      0,
    );

    setCart({ items: updatedItems, totalPrice: total });
  };

  const applyCoupon = async () => {
    if (coupon) {
      alert("Chỉ được áp dụng 1 mã giảm giá. Hãy xóa mã hiện tại trước.");
      return;
    }

    try {
      const res = await api.get(`/coupons/${couponCode}`);
      setCoupon(res.data);
    } catch {
      alert("Coupon không hợp lệ");
    }
  };

  const shippingFee = shippingFeeMap[shipping];
  const discount = coupon?.discount || 0;
  const finalTotal = (cart?.totalPrice || 0) + shippingFee - discount;

  const handleCheckout = async () => {
    if (!address) {
      alert("Nhập địa chỉ giao hàng");
      return;
    }

    await api.post("/orders", {
      items: cart.items,
      address,
      shippingMethod: shipping,
      couponCode: coupon?.code,
    });

    alert("Đặt hàng thành công");
    setCart(null);
  };

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
          {!cart || cart.items.length === 0 ? (
            <div className="bg-white rounded-lg shadow p-8 text-center">
              <div className="text-5xl mb-3">🛒</div>
              <p className="text-gray-600 text-lg mb-4">
                Giỏ hàng của bạn đang trống
              </p>

              <a
                href="/products"
                className="inline-block bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-2 px-6 rounded-lg transition"
              >
                Tiếp tục mua sắm
              </a>
            </div>
          ) : (
            cart.items.map((item: any) => (
              <CartItem
                key={item.productId}
                item={item}
                onUpdate={updateQuantity}
                onRemove={removeItem}
              />
            ))
          )}
        </div>
        {/* RIGHT - CHECKOUT */}
        <div className="bg-white rounded-lg shadow p-6 space-y-4 h-fit sticky top-4">
          <h2 className="text-xl font-bold">Thanh toán</h2>
          {/* Address */}
          <div className="space-y-1">
            <label className="text-sm font-medium text-gray-700">
              Địa chỉ giao hàng
            </label>
            <input
              type="text"
              placeholder="Nhập địa chỉ nhận hàng"
              value={address}
              onChange={(e) => setAddress(e.target.value)}
              className="w-full border border-gray-300 p-2 rounded focus:ring-2 focus:ring-indigo-500 outline-none"
            />
          </div>
          {/* Shipping */}
          <div className="space-y-1">
            <label className="text-sm font-medium text-gray-700">
              Phương thức vận chuyển
            </label>
            <select
              value={shipping}
              onChange={(e) => setShipping(e.target.value)}
              className="w-full border border-gray-300 p-2 rounded focus:ring-2 focus:ring-indigo-500 outline-none"
            >
              <option value="standard">Tiêu chuẩn (20k)</option>
              <option value="fast">Nhanh (40k)</option>
              <option value="express">Hỏa tốc (60k)</option>
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
                  {coupon.code} - {coupon.description}
                </span>
                <button
                  onClick={() => setCoupon(null)}
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
              <span>₫{cart.totalPrice.toLocaleString("vi-VN")}</span>
            </div>
            <div className="flex justify-between">
              <span>Phí ship</span>
              <span>₫{shippingFee.toLocaleString("vi-VN")}</span>
            </div>
            {coupon && (
              <div className="flex justify-between text-green-600">
                <span>Giảm giá</span>
                <span>-₫{discount.toLocaleString("vi-VN")}</span>
              </div>
            )}
            <div className="flex justify-between font-bold text-lg border-t pt-2">
              <span>Tổng tiền</span>
              <span>₫{finalTotal.toLocaleString("vi-VN")}</span>
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
}
