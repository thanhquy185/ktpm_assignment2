import { useState, useEffect } from "react";
import { toast } from "react-toastify";
import { HttpStatusCode } from "axios";
import { useAuth } from "../contexts/AuthContext";
import ProductCardComponent from "../components/ProductCard";
import { ProductApi } from "../services/api/productApi";
import { CartApi } from "../services/api/cartApi";
import type { ProductType } from "../types/product";
import type { CartItemAddToCartRequest } from "../types/cartItem";

const ProductsPage: React.FC = () => {
  const { user } = useAuth();

  const [products, setProducts] = useState<ProductType[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>("");
  const [addToCart, setAddToCart] = useState<string | null>(null);

  const fetchProducts = async () => {
    const response = await ProductApi.getAllProduct();
    if (response.status === HttpStatusCode.Ok && response.data) {
      setProducts(response.data);
    } else if ((response as any).error) {
      console.error((response as any).message);
      setError("Không thể tải danh sách sản phẩm");
    }
  };
  const handleAddToCart = async (product: ProductType) => {
    setAddToCart(product.id!);

    const response = await CartApi.addToCart(user?.id!, {
      productId: product.id,
      quantity: 1,
    } as CartItemAddToCartRequest);
    if (response.status === HttpStatusCode.Created && response.data) {
      await fetchProducts();
      toast.success(
        <div data-testid="toast-add-to-cart-success">
          Thêm sản phẩm vào giỏ hàng thành công!
        </div>,
      );
      // window.alert("Thêm sản phẩm vào giỏ hàng thành công!");
    } else if ((response as any).error) {
      toast.error((response as any).message);
    }

    setAddToCart(null);
  };

  useEffect(() => {
    setLoading(true);
    fetchProducts();
    setLoading(false);
  }, []);

  if (loading) {
    return (
      <div
        className="flex items-center justify-center min-h-screen"
        data-testid="loading"
      >
        <p className="text-lg text-gray-600">Đang tải sản phẩm...</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 py-8">
        <h1
          data-testid="products-title"
          className="text-3xl font-bold text-gray-900 mb-4"
        >
          Sản phẩm
        </h1>
        {error && (
          <div
            data-testid="error-message"
            className="mb-4 text-red-600 bg-red-50 p-4 rounded"
          >
            {error}
          </div>
        )}
        {products.length === 0 ? (
          <div className="text-center py-12" data-testid="no-products">
            <p className="text-gray-600 text-lg">Chưa có sản phẩm nào</p>
          </div>
        ) : (
          <div
            className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6"
            data-testid="products-grid"
          >
            {products.map((product) => (
              <ProductCardComponent
                key={product.id}
                product={product}
                onAddToCart={handleAddToCart}
              />
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default ProductsPage;
