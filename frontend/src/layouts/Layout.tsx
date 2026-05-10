import { Outlet, Link, useNavigate, useLocation } from "react-router-dom";
import { ShoppingCart, LogOut, ClipboardList, Computer } from "lucide-react";
import { useAuth } from "../contexts/AuthContext";

const Layout: React.FC = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const isActive = (path: string) => location.pathname === path;

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <div className="min-h-screen flex flex-col bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow" data-testid="header">
        <div className="max-w-7xl mx-auto px-4 py-4">
          <div className="flex items-center justify-between">
            {/* Logo */}
            <Link to="/" className="flex items-center gap-2" data-testid="logo">
              <img
                src="/src/assets/common/logosgu.png"
                alt=""
                className="w-8 h-8 rounded"
              />
              <span className="text-2xl font-bold text-gray-900">ShopCart</span>
            </Link>
            {/* Navigation */}
            {user && (
              <nav className="flex items-center gap-6" data-testid="navigation">
                <Link
                  to="/products"
                  data-testid="nav-products"
                  className={`flex items-center gap-1 px-3 py-2 rounded-lg transition ${
                    isActive("/products")
                      ? "bg-indigo-100 text-indigo-700 font-semibold"
                      : "text-gray-700 hover:bg-gray-100"
                  }`}
                >
                  <Computer size={18} />
                  Sản phẩm
                </Link>
                <Link
                  to="/cart"
                  data-testid="nav-cart"
                  className={`flex items-center gap-1 px-3 py-2 rounded-lg transition ${
                    isActive("/cart")
                      ? "bg-indigo-100 text-indigo-700 font-semibold"
                      : "text-gray-700 hover:bg-gray-100"
                  }`}
                >
                  <ShoppingCart size={18} />
                  Giỏ hàng
                </Link>
                {/* <Link
                  to="/checkout"
                  data-testid="nav-checkout"
                  className={`flex items-center gap-1 px-3 py-2 rounded-lg transition ${
                    isActive("/checkout")
                      ? "bg-indigo-100 text-indigo-700 font-semibold"
                      : "text-gray-700 hover:bg-gray-100"
                  }`}
                >
                  <ShoppingCart size={18} />
                  Thanh toán
                </Link> */}
                <Link
                  to="/orders"
                  data-testid="nav-orders"
                  className={`flex items-center gap-1 px-3 py-2 rounded-lg transition ${
                    isActive("/orders")
                      ? "bg-indigo-100 text-indigo-700 font-semibold"
                      : "text-gray-700 hover:bg-gray-100"
                  }`}
                >
                  <ClipboardList size={18} />
                  Đơn hàng
                </Link>
                {/* User Info and Logout */}
                <div className="flex items-center gap-3 pl-6 border-l border-gray-200">
                  <span
                    className="text-sm text-gray-700"
                    data-testid="user-name"
                  >
                    {user.username}
                  </span>
                  <button
                    onClick={handleLogout}
                    data-testid="logout-btn"
                    className="flex items-center gap-1 text-red-600 hover:text-red-700 px-3 py-2 rounded-lg hover:bg-red-50 transition"
                  >
                    <LogOut size={18} />
                    Đăng xuất
                  </button>
                </div>
              </nav>
            )}
          </div>
        </div>
      </header>
      {/* Main Content */}
      <main className="flex-1">
        <Outlet />
      </main>
      {/* Footer */}
      <footer
        className="bg-gray-900 text-white py-8 mt-12"
        data-testid="footer"
      >
        <div className="max-w-7xl mx-auto px-4 text-center">
          <p>&copy; 2026 ShopCart. All rights reserved.</p>
          <p>Quy - Châu - Nhật Huy - Quốc Huy</p>
        </div>
      </footer>
    </div>
  );
};

export default Layout;
