import type { OrderType } from "../types/order";
import { formatDate, formatPrice } from "../utils/priceCalculation";

const getStatusInfo = (status?: string) => {
  switch (status) {
    case "Chờ xác nhận":
      return {
        label: status,
        className: "bg-gray-100 text-gray-700",
      };
    case "Đã huỷ đơn":
      return {
        label: status,
        className: "bg-red-100 text-red-700",
      };
    case "Đã xác nhận":
      return {
        label: status,
        className: "bg-green-100 text-green-700",
      };
    case "Đã giao hàng":
      return {
        label: status,
        className: "bg-purple-100 text-purple-700",
      };
    default:
      return {
        label: status || "Không rõ",
        className: "bg-gray-100 text-gray-700",
      };
  }
};

type OrderItemComponentProps = {
  order: OrderType;
  onCancel: (orderId: string) => void;
};

const OrderItemComponent: React.FC<OrderItemComponentProps> = ({
  order,
  onCancel,
}) => {
  const statusInfo = getStatusInfo(order.status);

  return (
    <div key={order.id} className="rounded-3xl bg-white shadow p-6">
      {/* HEADER */}
      <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4 mb-4">
        <div>
          <p className="text-sm text-gray-500">Mã đơn hàng</p>
          <h2 className="text-xl font-semibold text-gray-900">{order.id}</h2>
        </div>
        <div className="space-y-1 text-right">
          <p className="text-sm text-gray-500">Trạng thái</p>
          <span
            className={`inline-flex rounded-full px-3 py-1 text-sm font-semibold ${statusInfo.className}`}
          >
            {statusInfo.label}
          </span>
        </div>
      </div>
      {/* INFO */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-sm text-gray-600 mb-4">
        <div>
          <p className="font-medium text-gray-900">Ngày đặt</p>
          <p>{order.createdAt ? formatDate(order.createdAt) : "—"}</p>
        </div>
        <div>
          <p className="font-medium text-gray-900">Tổng sản phẩm</p>
          <p>{order.orderItems?.length ?? 0}</p>
        </div>
        <div>
          <p className="font-medium text-gray-900">Tổng tiền</p>
          <p>{order.totalPrice ? formatPrice(order.totalPrice) : "—"}</p>
        </div>
      </div>
      {/* ADDRESS */}
      <div className="border-t pt-4 text-sm text-gray-700">
        <p className="font-semibold text-gray-900 mb-2">Địa chỉ giao hàng</p>
        <p>{order.shippingAddress || "Không xác định"}</p>
      </div>
      {/* ACTION */}
      {order.status === "Chờ xác nhận" && (
        <div className="mt-4 flex justify-end">
          <button
            onClick={() => onCancel(order.id!)}
            className="px-4 py-2 rounded-lg bg-red-500 text-white text-sm font-medium hover:bg-red-600 transition"
          >
            Huỷ đơn
          </button>
        </div>
      )}
    </div>
  );
};

export default OrderItemComponent;
