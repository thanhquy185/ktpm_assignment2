import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
} from "react-router-dom";
import { ToastContainer } from "react-toastify";
import { AuthProvider } from "./contexts/AuthContext";
import ProtectedRoute from "./components/ProtectedRoute";
import Layout from "./layouts/Layout";
import LoginPage from "./pages/LoginPage";
import ProductsPage from "./pages/ProductsPage";
import CartPage from "./pages/CartPage";
import CheckOutPage from "./pages/CheckoutPage";
import OrderPage from "./pages/OrderPage";
import "react-toastify/dist/ReactToastify.css";

function App() {
  return (
    <>
      <Router>
        <AuthProvider>
          <Routes>
            {/* Login Route */}
            <Route path="/login" element={<LoginPage />} />
            {/* Protected Routes */}
            <Route
              element={
                <ProtectedRoute>
                  <Layout />
                </ProtectedRoute>
              }
            >
              <Route path="/" element={<Navigate to="/products" replace />} />
              <Route path="/products" element={<ProductsPage />} />
              <Route path="/cart" element={<CartPage />} />
              <Route path="/checkout" element={<CheckOutPage />} />
              <Route path="/orders" element={<OrderPage />} />
            </Route>
            {/* Catch all - redirect to products */}
            <Route path="*" element={<Navigate to="/products" replace />} />
          </Routes>
        </AuthProvider>
      </Router>
      <ToastContainer autoClose={3000} />
    </>
  );
}

export default App;
