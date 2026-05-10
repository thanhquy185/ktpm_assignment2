import { HttpStatusCode } from "axios";
import { CartApi } from "../services/api/cartApi";
import { useEffect, useState } from "react";
import { useAuth } from "../contexts/AuthContext";
import { toast } from "react-toastify";
import type { CartType } from "../types/cart";
import CheckoutComponent from "../components/Checkout";
import CheckoutSummaryComponent from "../components/CheckoutSummary";

const CheckOutPage: React.FC = () => {
  const { user } = useAuth();

  const [cart, setCart] = useState<CartType>();
  const [loading, setLoading] = useState<boolean>(true);

  const fetchCartByUserId = async (userId: string) => {
    const response = await CartApi.getCartByUserId(userId);
    if (response.status === HttpStatusCode.Ok && response.data) {
      setCart(response.data);
    } else if ((response as any).error) {
      setCart(undefined);
      toast.error((response as any).message);
    }
  };

  useEffect(() => {
    setLoading(true);

    if (user && user.id) {
      fetchCartByUserId(user.id);
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
        {/* Checkout Component */}
        <CheckoutComponent cartItems={cart?.cartItems || []} />
        {/* Checkout Summary Component */}
        <CheckoutSummaryComponent
          userId={user?.id || ""}
          subtotal={cart?.totalPrice || 0}
          cartItems={cart?.cartItems || []}
          fetchCartByUserId={fetchCartByUserId}
        />
      </div>
    </div>
  );
};

export default CheckOutPage;
