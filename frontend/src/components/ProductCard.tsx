import React, { useState } from "react";
import { ShoppingCart } from "lucide-react";

interface Product {
  id: string;
  name: string;
  description: string;
  price: number;
  quantity: number;
}

interface ProductCardProps {
  product: Product;
  onAddToCart: (product: Product) => Promise<void>;
}

export const ProductCard: React.FC<ProductCardProps> = ({
  product,
  onAddToCart,
}) => {
  const [adding, setAdding] = useState(false);

  const handleClick = async () => {
    try {
      setAdding(true);
      await onAddToCart(product);
    } catch (err) {
      alert("Không thể thêm vào giỏ hàng");
    } finally {
      setAdding(false);
    }
  };

  return (
    <div
      data-testid={`product-card-${product.id}`}
      className="bg-white rounded-lg shadow hover:shadow-lg transition overflow-hidden"
    >
      {/* Image */}
      <div className="bg-gray-200 h-48 flex items-center justify-center">
        <span className="text-gray-400">No Image</span>
      </div>
      <div className="p-4">
        {/* Name */}
        <h3
          className="text-lg font-semibold text-gray-900 mb-2"
          data-testid={`product-name-${product.id}`}
        >
          {product.name}
        </h3>
        {/* Description */}
        <p className="text-gray-600 text-sm mb-3 line-clamp-2">
          {product.description || "Không có mô tả"}
        </p>
        {/* Price + Quantity */}
        <div className="flex items-center justify-between mb-4">
          <span
            className="text-2xl font-bold text-indigo-600"
            data-testid={`product-price-${product.id}`}
          >
            {product.price.toLocaleString("vi-VN")}đ
          </span>
          <span className="text-sm text-gray-500">Còn: {product.quantity}</span>
        </div>
        {/* Button */}
        <button
          onClick={handleClick}
          disabled={adding || product.quantity === 0}
          data-testid={`add-to-cart-btn-${product.id}`}
          className="w-full bg-indigo-600 hover:bg-indigo-700 disabled:bg-gray-400 text-white font-semibold py-2 px-4 rounded-lg transition flex items-center justify-center gap-2"
        >
          <ShoppingCart size={18} />
          {adding ? "Đang thêm..." : "Thêm vào giỏ"}
        </button>
      </div>
    </div>
  );
};
