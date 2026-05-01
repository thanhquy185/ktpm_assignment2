import { useState, useEffect } from "react";
import { ShoppingCart } from "lucide-react";
import api from "../services/api";
import { ProductCard } from "../components/ProductCard";
// import { cartService } from '../services/cartService';

interface Product {
  id: string;
  name: string;
  description: string;
  price: number;
  quantity: number;
  category: {
    id: string;
    name: string;
  };
  status: string;
}

export function ProductsPage() {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [addingToCart, setAddingToCart] = useState<string | null>(null);

  const demoProducts: Product[] = [
    {
      id: "1",
      name: "Son dưỡng môi",
      description: "Giữ ẩm môi mềm mịn suốt cả ngày",
      price: 120000,
      quantity: 15,
      category: { id: "c1", name: "Son" },
      status: "ACTIVE",
    },
    {
      id: "2",
      name: "Kem chống nắng SPF50",
      description: "Bảo vệ da khỏi tia UV, không gây nhờn rít",
      price: 250000,
      quantity: 8,
      category: { id: "c2", name: "Skincare" },
      status: "ACTIVE",
    },
    {
      id: "3",
      name: "Sữa rửa mặt dịu nhẹ",
      description: "Làm sạch sâu, phù hợp da nhạy cảm",
      price: 180000,
      quantity: 20,
      category: { id: "c2", name: "Skincare" },
      status: "ACTIVE",
    },
    {
      id: "4",
      name: "Serum Vitamin C",
      description: "Giúp da sáng và đều màu hơn",
      price: 320000,
      quantity: 5,
      category: { id: "c2", name: "Skincare" },
      status: "ACTIVE",
    },
    {
      id: "5",
      name: "Phấn phủ kiềm dầu",
      description: "Kiềm dầu tốt, giữ lớp makeup lâu trôi",
      price: 210000,
      quantity: 0,
      category: { id: "c3", name: "Makeup" },
      status: "OUT_OF_STOCK",
    },
    {
      id: "6",
      name: "Nước tẩy trang",
      description: "Làm sạch lớp makeup và bụi bẩn",
      price: 150000,
      quantity: 12,
      category: { id: "c2", name: "Skincare" },
      status: "ACTIVE",
    },
  ];

  useEffect(() => {
    // const fetchProducts = async () => {
    //   try {
    //     const response = await api.get("/products");
    //     setProducts(response.data);
    //   } catch (err) {
    //     setError("Không thể tải danh sách sản phẩm");
    //     console.error(err);
    //   } finally {
    //     setLoading(false);
    //   }
    // };

    // fetchProducts();
    setProducts(demoProducts);
    setLoading(false);
  }, []);

  const handleAddToCart = async (product: Product) => {
    try {
      // setAddingToCart(product.id);
      // await cartService.addToCart({
      //   productId: product.id,
      //   quantity: 1,
      // });
      // Show success message (could be improved with toast)
      alert(`Đã thêm ${product.name} vào giỏ hàng`);
    } catch (err) {
      alert("Không thể thêm vào giỏ hàng");
      console.error(err);
    } finally {
      setAddingToCart(null);
    }
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
              <ProductCard
                key={product.id}
                product={product}
                onAddToCart={async (product) => {
                  console.log(product);
                }}
              />
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
