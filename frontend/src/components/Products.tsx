import { useState } from "react";
import { toast } from "react-toastify";
import { HttpStatusCode } from "axios";
import ProductCardComponent from "./ProductCard";
import { CartApi } from "../services/api/cartApi";
import type { ProductType } from "../types/product";
import type { CartItemAddToCartRequest } from "../types/cartItem";

type ProductsComponentProps = {
  userId: string;
  products: ProductType[];
  fetchProducts: () => Promise<void>;
};

const ProductsComponent: React.FC<ProductsComponentProps> = ({
  userId,
  products,
  fetchProducts,
}) => {
  const [addToCart, setAddToCart] = useState<string | null>(null);

  const handleAddToCart = async (product: ProductType) => {
    setAddToCart(product.id!);

    const response = await CartApi.addToCart(userId, {
      productId: product.id,
      quantity: 1,
    } as CartItemAddToCartRequest);
    if (response.status === HttpStatusCode.Created && response.data) {
      await fetchProducts();
      toast.success("Thêm sản phẩm vào giỏ hàng thành công!");
      // toast.success(
      //   <div data-testid="toast-add-to-cart-success">
      //     Thêm sản phẩm vào giỏ hàng thành công!
      //   </div>,
      // );
    } else if ((response as any).error) {
      // toast.error(
      //   <div data-testid="toast-add-to-cart-error">
      //     {(response as any).message}
      //   </div>,
      // );
      toast.error((response as any).message);
    }

    setAddToCart(null);
  };

  return (
    <>
      <h1
        data-testid="products-title"
        className="text-3xl font-bold text-gray-900 mb-4"
      >
        Sản phẩm
      </h1>
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
    </>
  );
};

export default ProductsComponent;
