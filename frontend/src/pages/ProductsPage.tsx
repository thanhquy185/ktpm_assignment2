import { useState, useEffect } from "react";
import { toast } from "react-toastify";
import { HttpStatusCode } from "axios";
import { useAuth } from "../contexts/AuthContext";
import ProductCardComponent from "../components/ProductCard";
import { ProductService } from "../services/productService";
import { CartService } from "../services/cartService";
import type { ProductType } from "../types/product";
import type { CartItemRequest } from "../types/cartItem";

const ProductsPage: React.FC = () => {
  const { user, fetchUser } = useAuth();

  const [products, setProducts] = useState<ProductType[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>("");
  useEffect(() => {
    const fetchProducts = async () => {
      const response = await ProductService.getAllProduct();
      if (response.status === HttpStatusCode.Ok && response.data) {
        setProducts(response.data);
      } else if ((response as any).error) {
        console.error((response as any).message);
        setError("Không thể tải danh sách sản phẩm");
      }
    };

    fetchProducts();
    setLoading(false);
  }, []);

  const [addToCart, setAddToCart] = useState<string | null>(null);
  const handleAddToCart = async (product: ProductType) => {
    setAddToCart(product.id!);

    const response = await CartService.addToCart(user?.id!, {
      productId: product.id,
      quantity: 1,
    } as CartItemRequest);
    if (response.status === HttpStatusCode.Created && response.data) {
      await fetchUser();
      toast.success("Thêm sản phẩm vào giỏ hàng thành công!");
    } else if ((response as any).error) {
      toast.error((response as any).message);
    }

    setAddToCart(null);
  };

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
          className="text-3xl font-bold text-gray-900 mb-4"
          data-testid="products-title"
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
