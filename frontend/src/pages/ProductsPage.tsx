import { useState, useEffect } from "react";
import { HttpStatusCode } from "axios";
import { ProductApi } from "../services/api/productApi";
import type { ProductType } from "../types/product";
import ProductsComponent from "../components/Products";
import { useAuth } from "../contexts/AuthContext";

const ProductsPage: React.FC = () => {
  const { user } = useAuth();

  const [products, setProducts] = useState<ProductType[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>("");

  const fetchProducts = async () => {
    const response = await ProductApi.getAllProduct();
    if (response.status === HttpStatusCode.Ok && response.data) {
      setProducts(response.data);
    } else if ((response as any).error) {
      console.error((response as any).message);
      setError("Không thể tải danh sách sản phẩm");
    }
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
        <ProductsComponent
          userId={user?.id!}
          products={products}
          fetchProducts={fetchProducts}
        />
      </div>
    </div>
  );
};

export default ProductsPage;
