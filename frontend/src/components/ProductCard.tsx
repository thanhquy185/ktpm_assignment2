import React, { useState } from "react";
import { ShoppingCart } from "lucide-react";
import type { ProductType } from "../types/product";
import { formatPrice } from "../utils/priceCalculation";

interface ProductCardComponentProps {
  product: ProductType;
  onAddToCart: (product: ProductType) => Promise<void>;
}

const ProductCardComponent: React.FC<ProductCardComponentProps> = ({
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
      data-testid={`product-card-${product?.id}`}
      className="bg-white rounded-lg shadow hover:shadow-lg transition overflow-hidden"
    >
      {/* Image */}
      <div className="bg-gray-200 h-48 flex items-center justify-center">
        {product?.image ? (
          <img src={"/src/assets/products/" + product.image} />
        ) : (
          <span className="text-gray-400">No Image</span>
        )}
      </div>
      <div className="p-4">
        {/* Category + Status */}
        <div className="flex items-center justify-between mb-2">
          <span className="text-sm text-indigo-500">
            {product?.category?.name || "Không có danh mục"}
          </span>

          <span
            className={`text-xs px-2 py-1 rounded ${
              product?.status === "Đang bán"
                ? "bg-green-100 text-green-600"
                : "bg-red-100 text-red-600"
            }`}
          >
            {product?.status}
          </span>
        </div>
        {/* Name */}
        <h3
          className="text-lg font-semibold text-gray-900 mb-2"
          data-testid={`product-name-${product?.id}`}
        >
          {product?.name}
        </h3>
        {/* Description */}
        <p className="text-gray-600 text-sm mb-3 line-clamp-2">
          {product?.description || "Không có mô tả"}
        </p>
        {/* Price + Quantity */}
        <div className="flex items-center justify-between mb-4">
          <span className="text-sm text-gray-500">
            Còn: {product?.inventory?.stock!}
          </span>
          <span
            className="text-2xl font-bold text-indigo-600"
            data-testid={`product-price-${product?.id}`}
          >
            {formatPrice(product.price || 0)}
          </span>
        </div>
        {/* Button */}
        <button
          onClick={handleClick}
          data-testid={`add-to-cart-btn-${product?.id}`}
          className="w-full bg-indigo-600 hover:bg-indigo-700 disabled:bg-gray-400 text-white font-semibold py-2 px-4 rounded-lg transition flex items-center justify-center gap-2"
          disabled={
            adding ||
            product?.inventory?.stock! === 0 ||
            product?.status === "Dừng bán"
          }
        >
          <ShoppingCart size={18} />
          {adding ? "Đang thêm..." : "Thêm vào giỏ"}
        </button>
      </div>
    </div>
  );
};

export default ProductCardComponent;
