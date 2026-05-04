import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import { HttpStatusCode } from "axios";
import { useAuth } from "../contexts/AuthContext";
import OrderItemComponent from "../components/OrderItem";
import { OrderService } from "../services/orderService";
import type { OrderType } from "../types/order";

const OrderPage: React.FC = () => {
  const { user, fetchUser } = useAuth();

  const [orders, setOrders] = useState<OrderType[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>("");

  const handleCancelOrder = async (orderId: string) => {
    const confirmCancel = window.confirm(
      "Bạn có chắc muốn huỷ đơn hàng này không?",
    );

    if (!confirmCancel) return;

    const response = await OrderService.cancelOrder(orderId);
    if (response.status === HttpStatusCode.Ok && response.data) {
      await fetchUser();

      toast.success("Huỷ đơn hàng thành công!");
    } else if ((response as any).error) {
      setError("Huỷ đơn hàng thất bại!");
      toast.error((response as any).message);
    }
  };

  useEffect(() => {
    setLoading(true);

    if (user?.orders) {
      setOrders(user.orders);
    }

    console.log(user);

    setLoading(false);
  }, [user]);

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 py-8">
        <h1
          data-testid="orders-title"
          className="text-3xl font-bold text-gray-900 mb-2"
        >
          Đơn hàng
        </h1>
        <p className="text-gray-600 mb-6">
          Xem lại các đơn hàng đã đặt và theo dõi trạng thái giao hàng.
        </p>
        {/* LOADING */}
        {loading ? (
          <div className="flex items-center justify-center min-h-[256px] rounded-lg bg-white shadow p-8">
            <span className="text-gray-600">Đang tải đơn hàng...</span>
          </div>
        ) : error ? (
          /* ERROR */
          <div className="rounded-lg bg-red-50 text-red-700 border border-red-200 p-6">
            {error}
          </div>
        ) : orders.length === 0 ? (
          /* EMPTY */
          <div className="rounded-lg bg-white shadow p-10 text-center">
            <div className="text-5xl mb-4">📦</div>
            <p className="text-xl font-semibold text-gray-900 mb-2">
              Chưa có đơn hàng nào
            </p>
            <p className="text-gray-600">
              Các đơn hàng của bạn sẽ hiển thị ở đây khi bạn đặt hàng.
            </p>
          </div>
        ) : (
          /* LIST */
          <div className="space-y-6">
            {orders.map((order) => (
              <OrderItemComponent order={order} onCancel={handleCancelOrder} />
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default OrderPage;
