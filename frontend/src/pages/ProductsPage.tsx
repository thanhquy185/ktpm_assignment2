import { useState, useEffect } from 'react';
import { ShoppingCart } from 'lucide-react';
import api from '../services/api';
import { cartService } from '../services/cartService';

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
  const [error, setError] = useState('');
  const [addingToCart, setAddingToCart] = useState<string | null>(null);

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const response = await api.get('/products');
        setProducts(response.data);
      } catch (err) {
        setError('Không thể tải danh sách sản phẩm');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchProducts();
  }, []);

  const handleAddToCart = async (product: Product) => {
    try {
      setAddingToCart(product.id);
      await cartService.addToCart({
        productId: product.id,
        quantity: 1,
      });
      // Show success message (could be improved with toast)
      alert(`Đã thêm ${product.name} vào giỏ hàng`);
    } catch (err) {
      alert('Không thể thêm vào giỏ hàng');
      console.error(err);
    } finally {
      setAddingToCart(null);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen" data-testid="loading">
        <p className="text-lg text-gray-600">Đang tải sản phẩm...</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 py-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-2" data-testid="products-title">
          Sản phẩm
        </h1>
        
        {error && (
          <div data-testid="error-message" className="mb-4 text-red-600 bg-red-50 p-4 rounded">
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
              <div
                key={product.id}
                data-testid={`product-card-${product.id}`}
                className="bg-white rounded-lg shadow hover:shadow-lg transition overflow-hidden"
              >
                <div className="bg-gray-200 h-48 flex items-center justify-center">
                  <span className="text-gray-400">No Image</span>
                </div>

                <div className="p-4">
                  <h3
                    className="text-lg font-semibold text-gray-900 mb-2"
                    data-testid={`product-name-${product.id}`}
                  >
                    {product.name}
                  </h3>

                  <p className="text-gray-600 text-sm mb-3 line-clamp-2">
                    {product.description || 'Không có mô tả'}
                  </p>

                  <div className="flex items-center justify-between mb-4">
                    <span
                      className="text-2xl font-bold text-indigo-600"
                      data-testid={`product-price-${product.id}`}
                    >
                      ₫{product.price.toLocaleString('vi-VN')}
                    </span>
                    <span className="text-sm text-gray-500">
                      Còn: {product.quantity}
                    </span>
                  </div>

                  <button
                    onClick={() => handleAddToCart(product)}
                    disabled={addingToCart === product.id || product.quantity === 0}
                    data-testid={`add-to-cart-btn-${product.id}`}
                    className="w-full bg-indigo-600 hover:bg-indigo-700 disabled:bg-gray-400 text-white font-semibold py-2 px-4 rounded-lg transition flex items-center justify-center gap-2"
                  >
                    <ShoppingCart size={18} />
                    {addingToCart === product.id ? 'Đang thêm...' : 'Thêm vào giỏ'}
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
