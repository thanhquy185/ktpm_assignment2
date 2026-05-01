# ShopCart Frontend Setup Guide

## Cấu trúc thư mục

```
src/
├── components/        # Reusable components
│   └── ProtectedRoute.tsx
├── contexts/          # React Context (Auth)
│   └── AuthContext.tsx
├── layouts/           # Layout components
│   └── Layout.tsx
├── pages/             # Page components
│   ├── LoginPage.tsx
│   ├── ProductsPage.tsx
│   └── CartPage.tsx
├── services/          # API services
│   ├── api.ts
│   ├── cartService.ts
│   ├── inventoryService.ts
│   └── orderService.ts
├── types/             # TypeScript types
└── App.tsx            # Main app with routing
```

## Cách sử dụng & Testing

### Chạy Development Server
```bash
npm run dev
```
Server sẽ chạy trên http://localhost:5173

### Chạy Unit Tests
```bash
npm run test
```
Chạy tất cả tests trong dự án

### Chạy Tests với UI
```bash
npm run test:ui
```
Mở giao diện web để xem kết quả tests

### Chạy E2E Tests (Playwright)
```bash
npm run e2e              # Headless mode
npm run e2e:headed       # Headed mode (xem browser)
npm run e2e:debug        # Debug mode
```

## Thiết kế để dễ Test

Tất cả các components đều có `data-testid` attributes để dễ dàng test:

### Login Page
```jsx
data-testid="login-title"
data-testid="login-form"
data-testid="username-input"
data-testid="login-button"
data-testid="error-message"
```

### Products Page
```jsx
data-testid="products-title"
data-testid="products-grid"
data-testid="product-card-{id}"
data-testid="product-name-{id}"
data-testid="product-price-{id}"
data-testid="add-to-cart-btn-{id}"
```

### Cart Page
```jsx
data-testid="cart-title"
data-testid="cart-items"
data-testid="cart-item-{id}"
data-testid="item-name-{id}"
data-testid="item-price-{id}"
data-testid="quantity-{id}"
data-testid="increase-qty-{id}"
data-testid="decrease-qty-{id}"
data-testid="remove-item-{id}"
data-testid="coupon-form"
data-testid="coupon-input"
data-testid="apply-coupon-btn"
data-testid="applied-coupon"
data-testid="checkout-btn"
data-testid="order-success"
```

### Layout/Navigation
```jsx
data-testid="header"
data-testid="logo"
data-testid="navigation"
data-testid="nav-products"
data-testid="nav-cart"
data-testid="user-name"
data-testid="logout-btn"
data-testid="footer"
```

## Flow Ứng dụng

1. **Đăng nhập** - Người dùng nhập tên, được lưu vào localStorage
2. **Xem sản phẩm** - Fetch danh sách sản phẩm từ API
3. **Thêm vào giỏ** - Gọi API thêm sản phẩm vào giỏ
4. **Xem giỏ hàng** - Fetch giỏ hàng hiện tại, có thể:
   - Tăng/giảm số lượng
   - Xóa sản phẩm
   - Nhập mã giảm giá
5. **Thanh toán** - Gửi đơn hàng

## Environment Variables

- `VITE_API_URL`: Base URL cho API (mặc định: http://localhost:8080/api)

## Dependencies Chính

- **react-router-dom**: Routing giữa các trang
- **axios**: HTTP client để gọi API
- **tailwindcss**: CSS utility framework
- **lucide-react**: Icon components
- **vitest**: Unit testing
- **@testing-library/react**: Testing utilities
- **@playwright/test**: E2E testing

## AuthContext

Quản lý trạng thái đăng nhập:
```typescript
{
  user: { id, username } | null
  isLoggedIn: boolean
  login(username): void
  logout(): void
}
```

## ProtectedRoute

Bảo vệ các trang yêu cầu đăng nhập. Nếu chưa đăng nhập, tự động redirect tới `/login`.
