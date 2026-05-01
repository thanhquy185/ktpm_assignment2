import React, { useState } from 'react';
import { Plus, AlertCircle } from 'lucide-react';
import { Product } from '../types';
import { validateCartItem } from '../utils/validation';

interface ProductCardProps {
  product: Product;
  onAddToCart: (productId: string, quantity: number) => Promise<void>;
}

export const ProductCard: React.FC<ProductCardProps> = ({ product, onAddToCart }) => {
  const [quantity, setQuantity] = useState(1);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleAddToCart = async () => {
    // Validate quantity
    const validation = validateCartItem(quantity, product.stock);
    if (validation.error) {
      setError(validation.error);
      return;
    }

    setIsLoading(true);
    setError(null);
    
    try {
      await onAddToCart(product.id, quantity);
      setQuantity(1);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to add to cart');
    } finally {
      setIsLoading(false);
    }
  };

  const isOutOfStock = product.stock <= 0;

  return (
    <div className="card w-full">
      <div className="bg-gradient-to-br from-blue-100 to-blue-50 h-48 rounded-lg flex items-center justify-center mb-4">
        <div className="text-6xl">📦</div>
      </div>

      <h3 className="font-bold text-lg mb-2 text-left line-clamp-2">{product.name}</h3>
      
      <p className="text-gray-600 text-sm mb-3 text-left line-clamp-2">
        {product.description || 'No description available'}
      </p>

      <div className="flex justify-between items-center mb-4">
        <span className="text-2xl font-bold text-blue-600">
          {product.price.toLocaleString('vi-VN')} ₫
        </span>
        <span
          className={`text-sm font-medium px-3 py-1 rounded-full ${
            isOutOfStock
              ? 'bg-red-100 text-red-700'
              : product.stock < 5
              ? 'bg-yellow-100 text-yellow-700'
              : 'bg-green-100 text-green-700'
          }`}
        >
          {isOutOfStock ? 'Hết hàng' : `${product.stock} còn`}
        </span>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 rounded-lg p-3 mb-4 flex gap-2">
          <AlertCircle className="w-5 h-5 text-red-600 flex-shrink-0 mt-0.5" />
          <p className="text-red-700 text-sm">{error}</p>
        </div>
      )}

      <div className="flex gap-2 mb-4">
        <input
          type="number"
          min="1"
          max={product.stock}
          value={quantity}
          onChange={(e) => setQuantity(Math.max(1, parseInt(e.target.value) || 1))}
          disabled={isOutOfStock || isLoading}
          className="input-field w-20 text-center"
          data-testid="quantity-input"
        />
        <button
          onClick={handleAddToCart}
          disabled={isOutOfStock || isLoading}
          className={`btn-primary flex-1 flex items-center justify-center gap-2 ${
            isOutOfStock || isLoading ? 'opacity-50 cursor-not-allowed' : ''
          }`}
          data-testid="add-to-cart-btn"
        >
          <Plus className="w-5 h-5" />
          {isLoading ? 'Đang thêm...' : 'Thêm vào giỏ'}
        </button>
      </div>
    </div>
  );
};
