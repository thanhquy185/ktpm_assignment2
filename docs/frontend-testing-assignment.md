# 📌 Frontend Testing Assignment

## Tổng quan

### Unit test

- Trần Thanh Quy: calculateOrderTotal()
- Danh Thị Ngọc Châu: validateCartItem()
- Đỗ Nhật Huy: calculateCartTotal()
- Nguyễn Đình Quốc Huy: checkInventoryAvailable()

### Integration test

#### CartComponent

- Trần Thanh Quy:
- Đỗ Nhật Huy:

#### CheckoutSummaryComponent + PriceCalculatorComponent

- Nguyễn Đình Quốc Huy:

### Mock test

#### cartService.addToCart()

- Trần Thanh Quy:
- Đỗ Nhật Huy:

#### orderService.createOrder() + inventoryService.checkStock()

- Danh Thị Ngọc Châu:
- Nguyễn Đình Quốc Huy:

---

## Hướng dẫn chạy test

- Nếu viết unit tests thì phải đảm bảo coverage phải tăng (chuyển màu GREEN)
- Nếu viết xong chạy ổn mà coverage hay màu của file đó không thay đổi thì cần kiểm tra lại
- Khi chạy thì kết quả được hiển thị trên terminal

```bash
npm run test (chạy test không có bảng coverage)
npm run test:coverage (chạy test có bảng coverage)
```

## Quy tắc đặt tên file

- Unit test: `TênClass + UnitTest.test.ts` 👉 `UserServiceUnitTest.test.ts`

- Integration test: `TênClass + IntegrationTest.test.ts` 👉 `UserControllerIntegrationTest.test.ts`

- Mock test: `TênClass + MockTest.test.ts` 👉 `UserControllerMockTest.test.ts`

---

# 1. Unit Test

## Mô tả

Áp dụng TDD để phát triển unit tests cho validation, calculation module của Cart, Order và Inventory sử dụng Vitest:

## Phân tích

### Yêu cầu

Coverage ≥ 90% (không tính điểm riêng, dùng để đối chiếu chất lượng bài làm)

### Các phương thức

#### validateCartItem()

- TC1: Test quantity null
- TC2: Test quantity undefined
- TC3: Test quantity âm
- TC4: Test quantity bằng 0
- TC5: Test quantity vượt quá tồn kho
- TC6: Test quantity hợp lệ

#### calculateCartTotal()

- TC1: Test giỏ hàng rỗng
- TC2: Test tính tổng giá đúng với nhiều sản phẩm
- TC3: Test áp dụng mã giảm giá
- TC4: Test tổng giá sau khi xóa sản phẩm

#### calculateOrderTotal()

- TC1: Test tính tổng giá trước giảm giá
- TC2: Test áp dụng coupon giảm % (ví dụ: 10%, 20%)
- TC3: Test áp dụng coupon giảm số tiền cố định
- TC4: Test tính phí vận chuyển
- TC5: Test tổng cuối cùng (subtotal + shipping - discount)

#### checkInventoryAvailability()

- TC1: Test danh sách sản phẩm đều có thể sử dụng
- TC2: Test danh sách sẩn phẩm rỗng
- TC3: Test quantity của 1 sản phẩm là null
- TC4: Test quantity của 1 sản phẩm bé hơn 0
- TC5: Test quantity của 1 sản phẩm bằng 0
- TC6: Test stock của 1 sản phẩm là null
- TC7: Test quantity của 1 sản phẩm bé hơn 0
- TC8: Test quantity lớn hơn stock trên cùng 1 sản phẩm

## Phân công

| Thành viên           | Method                       |
| -------------------- | ---------------------------- | --- |
| Trần Thanh Quy       | calculateOrderTotal()        | ✅  |
| Danh Thị Ngọc Châu   | validateCartItem()           |
| Đỗ Nhật Huy          | calculateCartTotal()         |
| Nguyễn Đình Quốc Huy | checkInventoryAvailability() | ✅  |

---

# 2. Integration Test

## Mô tả

Test tích hợp Cart component với API service và Test tích hợp Checkout components:

## Phân tích

### Cart Component

#### Yêu cầu

```bash
Test rendering và user interactions
Test form submission và API calls
Test error handling và success messages (không tính điểm riêng, dùng để hoàn thiện bộ test)
```

#### Các data-testid (sử dụng getByTestId()) | text (sử dụng getByText())

Biến {property} có thể thay đổi giá trị

- Thông báo giỏ hàng rỗng: **_"empty-cart-inform"_** | **_"Giỏ hàng của bạn đang trống"_**
- Nút nhấn mua sắm: **_"go-to-products-page-button"_** | **_"Tiếp tục mua sắm"_**
- Hình ảnh sản phẩm: **_"cart-item-product-image-{productId}"_** | {}
- Tên sản phẩm: **_"cart-item-product-name-{productId}"_** | {}
- Loại sản phẩm: **_"cart-item-product-category-{productId}"_** | {}
- Giá bán sản phẩm: **_"cart-item-product-price-{productId}"_** | {}
- Số lượng sản phẩm: **_"cart-item-quantity-${productId}"_** | {}
- Nút tăng số lượng sản phẩm: **_"cart-item-increase-quantity-button-${productId}"_** | {}
- Nút giảm số lượng sản phẩm: **_"cart-item-decrease-quantity-button-${productId}"_** | {}
- Nút xoá sản phẩm: **_"cart-item-remove-button-${productId}"_** | {}

#### Các test cases

- TC1: Giỏ hàng rỗng
- TC2: Nhấn nút "Tiếp tục mua sắm" khi giỏ hàng rỗng
- TC3: Giỏ hàng có sản phẩm
- TC4: Giỏ hàng có sản phẩm và tăng số lượng 1 sản phẩm thành công
- TC5: Giỏ hàng có sản phẩm và giảm số lượng 1 sản phẩm thành công
- TC6: Giỏ hàng có sản phẩm và giảm số lượng 1 sản phẩm nhưng số lượng trong giỏ đang là 1
- TC7: Giỏ hàng có sản phẩm và xoá 1 sản phẩm thành công
- TC8: Nhấn nút "Thanh toán ngay" khi giỏ hàng rỗng
- TC9: Nhấn nút "Thanh toán ngay" khi có sản phẩm

#### Phân công

| Thành viên     | Test case |
| -------------- | :-------: | --- |
| Trần Thanh Quy | TC1 → TC4 | ✅  |
| Đỗ Nhật Huy    | TC5 → TC9 |

### Checkout Component

#### Yêu cầu

```bash
Test CheckoutSummary component với dữ liệu giỏ hàng
Test PriceCalculator component (tính giá real-time)
Test InventoryWarning component (cảnh báo hết hàng) (không tính điểm riêng, dùng để mở rộng bài làm)
```

#### Các data-testid (sử dụng getByTestId()) | text (sử dụng getByText())

Biến {property} có thể thay đổi giá trị

- Tiền hàng: **_"checkout-summary-subtotal"_** | {}
- Thông báo tiền hàng là số âm: **_"checkout-summary-subtotal-negative-inform"_** | **_"Tiền hàng đang là số âm"_**
- Phí ship: **_"checkout-summary-shipping-fee"_** | {}
- Thông báo phí ship là số âm: **_"checkout-summary-shipping-fee-negative-inform"_** | **_"Phí ship đang là số âm"_**
- Giảm giá: **_"checkout-summary-subtotal"_** | {}
- Thông báo giảm giá là số âm: **_"checkout-summary-discount-negative-inform"_** | **_"Giảm giá đang là số âm"_**
- Tổng tiền: **_"checkout-summary-total-price"_** | {}
- Thông báo tổng tiền là số âm: **_"checkout-summary-total-price-negative-inform"_** | **_"Tổng tiền đang là số âm"_**

#### Các test cases

Dành cho Checkout Summary Component

- TC1: Thông tin thanh toán với dữ liệu giỏ hàng
- TC2: Thanh toán thành công
- TC3: Thanh toán thành công và có sử dụng mã giảm giá
- TC4: Thanh toán nhưng chưa nhập địa chỉ giao hàng

Dành cho Price Calculator Component

- TC1: Tiền hàng là số âm
- TC2: Phí ship là số âm
- TC3: Giảm giá là số âm
- TC4: Tiền hàng, phí ship và giảm giá đều hợp lệ
- TC5: Tính tổng tiền đơn hàng thành công
- TC6: Tính tổng tiền đơn hàng là số âm

#### Phân công

| Thành viên           | Component        |
| -------------------- | ---------------- | --- |
| Trần Thanh Quy       | Checkout Summary | ✅  |
| Nguyễn Đình Quốc Huy | Price Calculator | ✅  |

---

# 3. Mock Test

## Mô tả

Mock CartService và OrderService trong component test

## Phân tích

### CartService

#### Yêu cầu

```bash
Mock cartService.addToCart()
Test với mocked successful/failed responses và verify mock calls
```

#### Các test cases

- TC1: Thêm sản phẩm thành công
- TC2: Thêm sản phẩm nhưng sản phẩm không tồn tại
- TC3: Thêm sản phẩm nhưng số lượng sản phẩm bé hơn 0
- TC4: Thêm sản phẩm nhưng số lượng sản phẩm bằng 0
- TC5: Thêm sản phẩm nhưng tồn kho của sản phẩm không tồn tại
- TC6: Thêm sản phẩm nhưng tồn kho của sản phẩm không đủ
- TC7: Thêm sản phẩm nhưng người dùng không tồn tại
- TC8: Thêm sản phẩm đã có trong giỏ nhưng tồn kho của sản phẩm không đủ

#### Phân công

| Thành viên     | Test case |
| -------------- | :-------: |
| Trần Thanh Quy | TC7 → TC8 |
| Đỗ Nhật Huy    | TC1 → TC6 |

### OrderService + InventoryService

#### Yêu cầu

```bash
Mock orderService.createOrder(), inventoryService.checkStock()
Test success và failure scenarios, đồng thời verify mock calls
```

#### Các test cases

##### OrderService

- TC1: Tạo đơn hàng thành công
- TC2: Tạo đơn hàng nhưng sản phẩm không tồn tại
- TC3: Tạo đơn hàng nhưng số lượng sản phẩm bé hơn 0
- TC4: Tạo đơn hàng nhưng số lượng sản phẩm bằng 0
- TC5: Tạo đơn hàng nhưng giá bán sản phẩm bé hơn 0
- TC6: Tạo đơn hàng nhưng giá bán sản phẩm bằng 0
- TC7: Tạo đơn hàng nhưng tồn kho của sản phẩm không tồn tại
- TC8: Tạo đơn hàng nhưng tồn kho của sản phẩm không đủ (giảm tồn kho sản phẩm)
- TC9: Tạo đơn hàng nhưng người dùng không tồn tại
- TC10: Tạo đơn hàng nhưng mã giảm giá không tồn tại
- TC11: Tạo đơn hàng nhưng mã giảm giá hết hạn

##### InventoryService

- TC1: Kiểm tra tồn kho thành công
- TC2: Kiểm tra tồn kho nhưng có 1 sản phẩm có số lượng cần so sánh bé hơn hoặc bằng 0
- TC3: Kiểm tra tồn kho nhưng có 1 sản phẩm có số lượng cần so sánh bằng 0
- TC4: Kiểm tra tồn kho nhưng có 1 sản phẩm không tồn tại trong tồn kho

#### Phân công

| Thành viên           |  Service  | Test case  |
| -------------------- | :-------: | :--------: |
| Danh Thị Ngọc Châu   |   Order   | TC1 → TC6  |
| Đỗ Nhật Huy          | Inventory |    All     |
| Nguyễn Đình Quốc Huy |   Order   | TC7 → TC11 |
