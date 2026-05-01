import { useState, useEffect } from 'react';
import { Trash2, Plus, Minus } from 'lucide-react';
import api from '../services/api';
import { cartService } from '../services/cartService';

interface CartItem {
  cartItemId: string;
  productId: string;
  productName: string;
  price: number;
  quantity: number;
}

interface Cart {
  userId: string;
  totalPrice: number;
  items: CartItem[];
}

interface Coupon {
  code: string;
  discount: number;
}

export function CartPage() {
  const [cart, setCart] = useState<Cart | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [couponCode, setCouponCode] = useState('');
  const [appliedCoupon, setAppliedCoupon] = useState<Coupon | null>(null);
  const [processingOrder, setProcessingOrder] = useState(false);
  const [orderSuccess, setOrderSuccess] = useState(false);

  useEffect(() => {
    const fetchCart = async () => {
      try {
        const cartData = await cartService.getCart();
        setCart(cartData);
      } catch (err) {
        setError('Không thể tải giỏ hàng');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchCart();
  }, []);

  const handleUpdateQuantity = async (productId: string, newQuantity: number) => {
    if (newQuantity < 1) return;

    try {
      const updatedCart = await cartService.updateQuantity(productId, newQuantity);
      setCart(updatedCart);
    } catch (err) {
      setError('Không thể cập nhật số lượng');
      console.error(err);
    }
  };

  const handleRemoveItem = async (productId: string) => {
    try {
      const updatedCart = await cartService.removeFromCart(productId);
      setCart(updatedCart);
    } catch (err) {
      setError('Không thể xóa sản phẩm');
      console.error(err);
    }
  };

  const handleApplyCoupon = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!couponCode.trim()) {
      setError('Vui lòng nhập mã coupon');
      return;
    }

    try {
      const response = await api.get(`/coupons/${couponCode}`);
      setAppliedCoupon(response.data);
      setCouponCode('');
      setError('');
    } catch (err) {
      setError('Mã coupon không hợp lệ');
      console.error(err);
    }
  };

  const handleCheckout = async () => {
    if (!cart || cart.items.length === 0) {
      setError('Giỏ hàng trống');
      return;
    }

    try {
      setProcessingOrder(true);
      setError('');

      const orderData = {
        items: cart.items.map((item) => ({
          productId: item.productId,
          quantity: item.quantity,
        })),
        couponCode: appliedCoupon?.code || null,
      };

      await api.post('/orders', orderData);
      setOrderSuccess(true);
      setCart(null);
    } catch (err) {
      setError('Không thể đặt hàng');
      console.error(err);
    } finally {
      setProcessingOrder(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen" data-testid="loading">
        <p className="text-lg text-gray-600">Đang tải giỏ hàng...</p>
      </div>
    );
  }

  if (orderSuccess) {
    return (
      <div className="min-h-screen bg-green-50 flex items-center justify-center px-4">
        <div className="bg-white rounded-lg shadow-lg p-8 text-center max-w-md" data-testid="order-success">
          <h1 className="text-2xl font-bold text-green-600 mb-4">Đặt hàng thành công!</h1>
          <p className="text-gray-600 mb-6">Cảm ơn bạn đã mua hàng. Đơn hàng của bạn đang được xử lý.</p>
          <a
            href="/products"
            className="inline-block bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-2 px-6 rounded-lg transition"
          >
            Tiếp tục mua sắm
          </a>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 py-8 px-4">
      <div className="max-w-4xl mx-auto">
        <h1 className="text-3xl font-bold text-gray-900 mb-8" data-testid="cart-title">
          Giỏ hàng & Thanh toán
        </h1>

        {error && (
          <div data-testid="error-message" className="mb-4 text-red-600 bg-red-50 p-4 rounded">
            {error}
          </div>
        )}

        {!cart || cart.items.length === 0 ? (
          <div className="bg-white rounded-lg shadow p-8 text-center" data-testid="empty-cart">
            <p className="text-gray-600 text-lg mb-4">Giỏ hàng trống</p>
            <a
              href="/products"
              className="inline-block bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-2 px-6 rounded-lg transition"
            >
              Tiếp tục mua sắm
            </a>
          </div>
        ) : (
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            {/* Cart Items */}
            <div className="lg:col-span-2">
              <div className="bg-white rounded-lg shadow p-6" data-testid="cart-items">
                <h2 className="text-xl font-semibold text-gray-900 mb-4">
                  Sản phẩm trong giỏ ({cart.items.length})
                </h2>

                <div className="space-y-4">
                  {cart.items.map((item) => (
                    <div
                      key={item.productId}
                      data-testid={`cart-item-${item.productId}`}
                      className="flex items-center gap-4 pb-4 border-b border-gray-200"
                    >
                      <div className="flex-1">
                        <h3 className="font-semibold text-gray-900" data-testid={`item-name-${item.productId}`}>
                          {item.productName}
                        </h3>
                        <p className="text-indigo-600 font-semibold" data-testid={`item-price-${item.productId}`}>
                          ₫{item.price.toLocaleString('vi-VN')}
                        </p>
                      </div>

                      <div className="flex items-center gap-2">
                        <button
                          onClick={() => handleUpdateQuantity(item.productId, item.quantity - 1)}
                          data-testid={`decrease-qty-${item.productId}`}
                          className="p-1 hover:bg-gray-100 rounded"
                        >
                          <Minus size={18} />
                        </button>
                        <span
                          className="px-3 py-1 bg-gray-100 rounded"
                          data-testid={`quantity-${item.productId}`}
                        >
                          {item.quantity}
                        </span>
                        <button
                          onClick={() => handleUpdateQuantity(item.productId, item.quantity + 1)}
                          data-testid={`increase-qty-${item.productId}`}
                          className="p-1 hover:bg-gray-100 rounded"
                        >
                          <Plus size={18} />
                        </button>
                      </div>

                      <button
                        onClick={() => handleRemoveItem(item.productId)}
                        data-testid={`remove-item-${item.productId}`}
                        className="text-red-600 hover:text-red-700 p-2"
                      >
                        <Trash2 size={18} />
                      </button>
                    </div>
                  ))}
                </div>
              </div>
            </div>

            {/* Checkout Summary */}
            <div className="lg:col-span-1">
              <div className="bg-white rounded-lg shadow p-6 sticky top-4" data-testid="checkout-summary">
                <h2 className="text-xl font-semibold text-gray-900 mb-4">Tóm tắt</h2>

                {/* Coupon Input */}
                <form onSubmit={handleApplyCoupon} className="mb-6" data-testid="coupon-form">
                  <div className="flex gap-2">
                    <input
                      type="text"
                      value={couponCode}
                      onChange={(e) => setCouponCode(e.target.value)}
                      placeholder="Mã giảm giá"
                      data-testid="coupon-input"
                      className="flex-1 px-3 py-2 border border-gray-300 rounded focus:ring-2 focus:ring-indigo-500 outline-none"
                    />
                    <button
                      type="submit"
                      data-testid="apply-coupon-btn"
                      className="bg-gray-200 hover:bg-gray-300 px-3 py-2 rounded font-semibold"
                    >
                      Áp dụng
                    </button>
                  </div>
                </form>

                {appliedCoupon && (
                  <div data-testid="applied-coupon" className="mb-4 p-2 bg-green-50 rounded border border-green-200">
                    <p className="text-sm text-green-700">
                      Coupon: {appliedCoupon.code} (-₫{appliedCoupon.discount.toLocaleString('vi-VN')})
                    </p>
                  </div>
                )}

                {/* Price Summary */}
                <div className="space-y-3 border-t border-gray-200 pt-4 mb-6">
                  <div className="flex justify-between text-gray-700">
                    <span>Tổng tiền hàng:</span>
                    <span data-testid="subtotal" className="font-semibold">
                      ₫{cart.totalPrice.toLocaleString('vi-VN')}
                    </span>
                  </div>
                  {appliedCoupon && (
                    <div className="flex justify-between text-green-700">
                      <span>Giảm giá:</span>
                      <span data-testid="discount">
                        -₫{appliedCoupon.discount.toLocaleString('vi-VN')}
                      </span>
                    </div>
                  )}
                  <div className="flex justify-between text-xl font-bold text-gray-900 border-t border-gray-200 pt-3">
                    <span>Tổng cộng:</span>
                    <span data-testid="total">
                      ₫{Math.max(0, cart.totalPrice - (appliedCoupon?.discount || 0)).toLocaleString('vi-VN')}
                    </span>
                  </div>
                </div>

                {/* Checkout Button */}
                <button
                  onClick={handleCheckout}
                  disabled={processingOrder}
                  data-testid="checkout-btn"
                  className="w-full bg-indigo-600 hover:bg-indigo-700 disabled:bg-gray-400 text-white font-semibold py-3 rounded-lg transition"
                >
                  {processingOrder ? 'Đang xử lý...' : 'Thanh toán'}
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
