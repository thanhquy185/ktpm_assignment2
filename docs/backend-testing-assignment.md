# 📌 Backend Testing Assignment

## Tổng quan

### Unit test

#### CartService

- Trần Thanh Quy: addToCart(), updateQuantity()
- Danh Thị Ngọc Châu: updateQuantity()
- Đỗ Nhật Huy: addToCart()
- Nguyễn Đình Quốc Huy: removeFromCart()

#### OrderService

- Trần Thanh Quy: getOrderById(), checkStockBeforeOrder(), calculateOrderTotal()
- Danh Thị Ngọc Châu: createOrder()
- Đỗ Nhật Huy: cancelOrder()
- Nguyễn Đình Quốc Huy: createOrder()

### Integration test

- Danh Thị Ngọc Châu: POST /api/carts/user/{userId}
- Trần Thanh Quy: POST /api/orders
- Đỗ Nhật Huy: DELETE /api/orders, POST /api/inventories
- Nguyễn Đình Quốc Huy: POST /api/orders

### Mock test

#### CartController

- Trần Thanh Quy: GET /api/carts, GET /api/carts/{id}, GET /api/carts/user/{userId}, PUT /api/carts/user/{userId}
- Danh Thị Ngọc Châu: PUT /api/carts/user/{userId}
- Đỗ Nhật Huy: POST /api/carts/user/{userId}
- Nguyễn Đình Quốc Huy: DELETE /api/carts/user/{userId}

#### OrderService

- Trần Thanh Quy: checkQuantityAndPriceBeforeOrder(), checkStockBeforeOrder(), calculateOrderTotal()
- Danh Thị Ngọc Châu: createOrder()
- Đỗ Nhật Huy: cancelOrder()
- Nguyễn Đình Quốc Huy: getOrders(), getOrderById(), getOrdersByUserId()

---

## Hướng dẫn chạy test

- Nếu viết unit tests thì phải đảm bảo coverage phải tăng (chuyển màu GREEN)
- Nếu viết xong chạy ổn mà coverage hay màu của service hay controller đó không thay đổi thì cần kiểm tra lại
- Các file có thể xem của jacoco có định dạng .html

```bash
mvn test (chạy test không cập nhật report)
mvn test jacoco:report (chạy test có cập nhật report, xem ở backend/target/site/jacoco)
```

## Quy tắc đặt tên file

- Unit test: `TênClass + UnitTest.java` 👉 `UserServiceUnitTest.java`

- Integration test: `TênClass + IntegrationTest.java` 👉 `UserControllerIntegrationTest.java`

- Mock test: `TênClass + MockTest.java` 👉 `UserControllerMockTest.java`

---

# 1. Unit Test

## Mô tả

Test CartService và OrderService (không dùng DB thật):

## Phân tích

### CartService

#### Yêu cầu

```bash
Test method addToCart() với các scenarios:
 • Thêm sản phẩm thành công
 • Thêm sản phẩm đã có trong giỏ (cộng dồn số lượng)
 • Thêm khi tồn kho không đủ
 • Thêm sản phẩm không tồn tại
Test removeFromCart() và updateQuantity()
Coverage ≥ 85% cho CartService
```

#### Các phương thức

##### addToCart()

- TC1_ATC: Thêm sản phẩm thành công
- TC2_ATC: Thêm sản phẩm thành công vào giỏ hàng, cập nhật tổng trong giỏ hàng
- TC3_ATC: Thêm sản phẩm nhưng sản phẩm không tồn tại
- TC4_ATC: Thêm sản phẩm nhưng số lượng sản phẩm bé hơn hoặc bằng 0
- TC5_ATC: Thêm sản phẩm nhưng tồn kho của sản phẩm không tồn tại
- TC6_ATC: Thêm sản phẩm nhưng tồn kho của sản phẩm không đủ
- TC7_ATC: Thêm sản phẩm nhưng người dùng không tồn tại
- TC8_ATC: Thêm sản phẩm đã có trong giỏ (cộng dồn số lượng)
- TC9_ATC: Thêm sản phẩm đã có trong giỏ nhưng tồn kho của sản phẩm không đủ

##### updateQuantity()

- TC1_UQ: Cập nhật sản phẩm thành công
- TC2_UQ: Cập nhật sản phẩm nhưng sản phẩm không tồn tại
- TC3_UQ: Cập nhật sản phẩm nhưng số lượng sản phẩm bé hơn 0
- TC4_UQ: Cập nhật sản phẩm nhưng số lượng sản phẩm bằng 0
- TC5_UQ: Cập nhật sản phẩm nhưng tồn kho của sản phẩm không tồn tại
- TC6_UQ: Cập nhật sản phẩm nhưng tồn kho của sản phẩm không đủ
- TC7_UQ: Cập nhật sản phẩm nhưng giỏ hàng của người dùng không tồn tại
- TC8_UQ: Cập nhật sản phẩm nhưng sản phẩm không tồn tại trong giỏ

##### removeFromCart() 

- TC1_RFC: Xoá sản phẩm thành công 
- TC2_RFC: Xoá sản phẩm nhưng sản phẩm không tồn tại 
- TC3_RFC: Xoá sản phẩm nhưng giỏ hàng của người dùng không tồn tại 
- TC4_RFC: Xoá sản phẩm nhưng sản phẩm không tồn tại trong giỏ 

#### Phân công

| Thành viên           | Method           | Test case |
| -------------------- | ---------------- | :-------: |
| Trần Thanh Quy       | addToCart()      | TC7 → TC9 | ✅
| Trần Thanh Quy       | updateQuantity() | TC7 → TC8 | ✅
| Danh Thị Ngọc Châu   | updateQuantity() | TC1 → TC6 | ✅
| Đỗ Nhật Huy          | addToCart()      | TC1 → TC6 | 
| Nguyễn Đình Quốc Huy | removeFromCart() | TC1 → TC4 | ✅

### OrderService

#### Yêu cầu

```bash
Test CRUD operations cho Order:
 • Test createOrder() – tạo đơn hàng, trừ tồn kho
 • Test getOrderById() – lấy thông tin đơn hàng
 • Test cancelOrder() – hủy đơn, hoàn tồn kho
 • Test calculateOrderTotal() – tính tổng giá chính xác
 • Test checkStockBeforeOrder() – kiểm tra tồn kho
Coverage ≥ 85% cho OrderService
```

#### Các phương thức

##### getOrderById()

- TC1_GOBI: Tìm kiếm đơn hàng thành công
- TC2_GOBI: Tìm kiếm đơn hàng nhưng đơn hàng không tồn tại

##### checkStockBeforeOrder()

- TC1_CSBO: Kiểm tra tồn kho sản phẩm thành công
- TC2_CSBO: Kiểm tra tồn kho sản phẩm nhưng sản phẩm không tồn tại
- TC3_CSBO: Kiểm tra tồn kho sản phẩm nhưng tồn kho không tồn tại
- TC4_CSBO: Kiểm tra tồn kho sản phẩm nhưng tồn kho không đủ

##### calculateOrderTotal()

- TC1_COT: Tính tổng tiền đơn hàng thành cHuy

##### createOrder()

- TC1_CO: Tạo đơn hàng thành công
- TC2_CO: Tạo đơn hàng nhưng sản phẩm không tồn tại
- TC3_CO: Tạo đơn hàng nhưng số lượng sản phẩm bé hơn 0
- TC4_CO: Tạo đơn hàng nhưng số lượng sản phẩm bằng 0
- TC5_CO: Tạo đơn hàng nhưng giá bán sản phẩm bé hơn 0
- TC6_CO: Tạo đơn hàng nhưng giá bán sản phẩm bằng 0
- TC7_CO: Tạo đơn hàng nhưng tồn kho của sản phẩm không tồn tại
- TC8_CO: Tạo đơn hàng nhưng tồn kho của sản phẩm không đủ (giảm tồn kho sản phẩm)
- TC9_CO: Tạo đơn hàng nhưng người dùng không tồn tại
- TC10_CO: Tạo đơn hàng nhưng mã giảm giá không tồn tại
- TC11_CO: Tạo đơn hàng nhưng mã giảm giá hết hạn

##### cancelOrder()

- TC1_CO: Huỷ đơn hàng thành công
- TC2_CO: Huỷ đơn hàng nhưng đơn hàng của người dùng không tồn tại
- TC3_CO: Huỷ đơn hàng nhưng đơn hàng đã được huỷ từ trước
- TC4_CO: Huỷ đơn hàng nhưng số lượng sản phẩm bé hơn hoặc bằng (tăng tồn kho sản phẩm)

#### Phân công

| Thành viên           | Method                  | Test case  |
| -------------------- | ----------------------- | :--------: |
| Trần Thanh Quy       | getOrderById()          | TC1 → TC2  | ✅
| Trần Thanh Quy       | checkStockBeforeOrder() | TC1 → TC4  | ✅
| Trần Thanh Quy       | calculateOrderTotal()   |    TC1     | ✅
| Danh Thị Ngọc Châu   | createOrder()           | TC1 → TC6  | ✅
| Đỗ Nhật Huy          | cancelOrder()           | TC1 → TC4  |
| Nguyễn Đình Quốc Huy | createOrder()           | TC7 → TC11 | ✅

---

# 2. Integration Test

## Mô tả

Test full flow: Controller → Service → Repository → Database

## Phân tích

### CartController

#### Yêu cầu

```bash
Test POST /api/cart/add endpoint
Test response structure và status codes
Test CORS và headers (không tính điểm riêng, dùng để hoàn thiện bài làm)
```

#### Các endpoint

##### POST /api/carts/user/{userId}

- Thêm sản phẩm thành công
- Thêm sản phẩm nhưng sản phẩm không tồn tại
- Thêm sản phẩm nhưng số lượng sản phẩm bé hơn 0
- Thêm sản phẩm nhưng số lượng sản phẩm bằng 0
- Thêm sản phẩm nhưng tồn kho của sản phẩm không tồn tại
- Thêm sản phẩm nhưng tồn kho của sản phẩm không đủ
- Thêm sản phẩm nhưng người dùng không tồn tại
- Thêm sản phẩm đã có trong giỏ (cộng dồn số lượng)
- Thêm sản phẩm đã có trong giỏ nhưng tồn kho của sản phẩm không đủ

### OrderController + InventoryController

#### Yêu cầu

```bash
Test POST /api/orders (Tạo đơn hàng)
Test thêm 1 endpoint bất kỳ trong nhóm Order hoặc Inventory
Các endpoint còn lại được khuyến khích triển khai thêm để hoàn thiện bộ test
```

#### Các endpoint

##### POST /api/orders

- Tạo đơn hàng thành công
- Tạo đơn hàng nhưng sản phẩm không tồn tại
- Tạo đơn hàng nhưng số lượng sản phẩm bé hơn 0
- Tạo đơn hàng nhưng số lượng sản phẩm bằng 0
- Tạo đơn hàng nhưng giá bán sản phẩm bé hơn 0
- Tạo đơn hàng nhưng giá bán sản phẩm bằng 0
- Tạo đơn hàng nhưng tồn kho của sản phẩm không tồn tại
- Tạo đơn hàng nhưng tồn kho của sản phẩm không đủ (giảm tồn kho sản phẩm)
- Tạo đơn hàng nhưng người dùng không tồn tại
- Tạo đơn hàng nhưng mã giảm giá không tồn tại
- Tạo đơn hàng nhưng mã giảm giá hết hạn

##### DELETE /api/orders

- Huỷ đơn hàng thành công
- Huỷ đơn hàng nhưng đơn hàng của người dùng không tồn tại
- Huỷ đơn hàng nhưng đơn hàng đã được huỷ từ trước
- Huỷ đơn hàng nhưng số lượng sản phẩm bé hơn hoặc bằng (tăng tồn kho sản phẩm)

##### POST /api/inventories

- Kiểm tra tồn kho thành công
- Kiểm tra tồn kho nhưng có 1 sản phẩm có số lượng cần so sánh bé hơn hoặc bằng 0
- Kiểm tra tồn kho nhưng có 1 sản phẩm có số lượng cần so sánh bằng 0
- Kiểm tra tồn kho nhưng có 1 sản phẩm không tồn tại trong tồn kho

## Phân công

| Thành viên           | Controller | Method | Endpoint                 | Test case |
| -------------------- | :--------: | :----: | ------------------------ | :-------: |
| Trần Thanh Quy       |    Cart    |  POST  | /api/carts/user/{userId} |    All    | ✅
| Danh Thị Ngọc Châu   |   Order    |  POST  | /api/orders              |   1 → 6   | ✅
| Đỗ Nhật Huy          |   Order    | DELETE | /api/orders              |    All    |
| Đỗ Nhật Huy          | Inventory  |  POST  | /api/inventories         |    All    |
| Nguyễn Đình Quốc Huy |   Order    |  POST  | /api/orders              |  7 → 11   | ✅

---

# 3. Mock Test

## Mô tả

Mock Repository / External API để test logic độc lập.

## Phân tích

### CartController

#### Yêu cầu

```bash
Mock CartService với @MockBean
Test controller với mocked service
Verify mock interactions và kiểm tra phản hồi trả về từ controller khi service được mock (không tính điểm riêng, dùng để hoàn thiện bài làm)
```

#### Các endpoint

##### GET /api/carts

- Truy danh sách giỏ hàng thành công
- Truy danh sách giỏ hàng thành công (danh sách rỗng)

##### GET /api/carts/{id}

- Truy một giỏ hàng theo mã giỏ hàng thành công
- Truy một giỏ hàng theo mã giỏ hàng nhưng mã giỏ hàng không tồn tại

##### GET /api/carts/user/{userId}

- Truy một giỏ hàng theo mã người dùng thành công
- Truy một giỏ hàng theo mã người dùng nhưng mã người dùng không tồn tại

##### POST /api/carts/user/{userId}

- Thêm sản phẩm thành công
- Thêm sản phẩm nhưng sản phẩm không tồn tại
- Thêm sản phẩm nhưng số lượng sản phẩm bé hơn 0
- Thêm sản phẩm nhưng số lượng sản phẩm bằng 0
- Thêm sản phẩm nhưng tồn kho của sản phẩm không tồn tại
- Thêm sản phẩm nhưng tồn kho của sản phẩm không đủ
- Thêm sản phẩm nhưng người dùng không tồn tại
- Thêm sản phẩm đã có trong giỏ (cộng dồn số lượng)
- Thêm sản phẩm đã có trong giỏ nhưng tồn kho của sản phẩm không đủ

##### PUT /api/carts/user/{userId}

- Cập nhật sản phẩm thành công
- Cập nhật sản phẩm nhưng sản phẩm không tồn tại
- Cập nhật sản phẩm nhưng số lượng sản phẩm bé hơn 0
- Cập nhật sản phẩm nhưng số lượng sản phẩm bằng 0
- Cập nhật sản phẩm nhưng tồn kho của sản phẩm không tồn tại
- Cập nhật sản phẩm nhưng tồn kho của sản phẩm không đủ
- Cập nhật sản phẩm nhưng giỏ hàng của người dùng không tồn tại
- Cập nhật sản phẩm nhưng sản phẩm không tồn tại trong giỏ

##### DELETE /api/carts/user/{userId}

- Xoá sản phẩm thành công
- Xoá sản phẩm nhưng sản phẩm không tồn tại
- Xoá sản phẩm nhưng giỏ hàng của người dùng không tồn tại
- Xoá sản phẩm nhưng sản phẩm không tồn tại trong giỏ

#### Phân công

| Thành viên           | Method | Endpoint                 | Test case |
| -------------------- | :----: | ------------------------ | :-------: |
| Trần Thanh Quy       |  GET   | /api/carts               |    All    | ✅
| Trần Thanh Quy       |  GET   | /api/carts/{id}          |    All    | ✅
| Trần Thanh Quy       |  GET   | /api/carts/user/{userId} |    All    | ✅
| Trần Thanh Quy       |  PUT   | /api/carts/user/{userId} |   7 → 8   | ✅
| Danh Thị Ngọc Châu   |  PUT   | /api/carts/user/{userId} |   1 → 6   | ✅
| Đỗ Nhật Huy          |  POST  | /api/carts/user/{userId} |    All    |
| Nguyễn Đình Quốc Huy | DELETE | /api/carts/user/{userId} |    All    | ✅

### OrderService

#### Yêu cầu

```bash
Mock InventoryRepository, OrderRepository
Test service layer với mocked repositories
Verify repository interactions và kiểm tra dữ liệu sau khi service xử lý với repository
được mock (không tính điểm riêng, dùng để hoàn thiện bài làm)
```

#### Các service

##### getOrders()

- Truy danh sách đơn hàng thành công
- Truy danh sách đơn hàng thành công (danh sách rỗng)

##### getOrderById()

- Truy một đơn hàng theo mã đơn hàng thành công
- Truy một đơn hàng theo mã đơn hàng nhưng mã đơn hàng không tồn tại

##### getOrdersByUserId()

- Truy danh sách đơn hàng theo mã người dùng thành công
- Truy danh sách đơn hàng theo mã người dùng thành công (danh sách rỗng)
- Truy danh sách đơn hàng theo mã người dùng nhưng mã người dùng không tồn tại

##### checkQuantityAndPriceBeforeOrder()

- Kiểm tra danh sách chi tiết đơn hàng thành công
- Kiểm tra danh sách chi tiết đơn hàng nhưng số lượng yêu cầu bé hơn 0
- Kiểm tra danh sách chi tiết đơn hàng nhưng số lượng yêu cầu bằng 0
- Kiểm tra danh sách chi tiết đơn hàng nhưng giá sản phẩm bé hơn 0

##### checkStockBeforeOrder()

- Kiểm tra tồn kho sản phẩm thành công
- Kiểm tra tồn kho sản phẩm nhưng sản phẩm không tồn tại
- Kiểm tra tồn kho sản phẩm nhưng tồn kho của sản phẩm không tồn tại
- Kiểm tra tồn kho sản phẩm nhưng tồn kho của sản phẩm không đủ

##### calculateOrderTotal()

- Tính tổng tiền đơn hàng thành công

##### createOrder()

- Tạo đơn hàng thành công
- Tạo đơn hàng nhưng sản phẩm không tồn tại
- Tạo đơn hàng nhưng số lượng sản phẩm bé hơn 0
- Tạo đơn hàng nhưng số lượng sản phẩm bằng 0
- Tạo đơn hàng nhưng giá bán sản phẩm bé hơn 0
- Tạo đơn hàng nhưng giá bán sản phẩm bằng 0
- Tạo đơn hàng nhưng tồn kho của sản phẩm không tồn tại
- Tạo đơn hàng nhưng tồn kho của sản phẩm không đủ (giảm tồn kho sản phẩm)
- Tạo đơn hàng nhưng người dùng không tồn tại
- Tạo đơn hàng nhưng mã giảm giá không tồn tại
- Tạo đơn hàng nhưng mã giảm giá hết hạn

##### cancelOrder()

- Huỷ đơn hàng thành công
- Huỷ đơn hàng nhưng đơn hàng của người dùng không tồn tại
- Huỷ đơn hàng nhưng đơn hàng đã được huỷ từ trước
- Huỷ đơn hàng nhưng số lượng sản phẩm bé hơn hoặc bằng (tăng tồn kho sản phẩm)

#### Phân công

| Thành viên           | Method                             | Test case |
| -------------------- | ---------------------------------- | :-------: |
| Trần Thanh Quy       | getOrders()                        |    All    | ✅
| Trần Thanh Quy       | getOrderById()                     |    All    | ✅
| Trần Thanh Quy       | checkStockBeforeOrder()            |    All    | ✅
| Trần Thanh Quy       | calculateOrderTotal()              |    All    | ✅
| Danh Thị Ngọc Châu   | createOrder()                      |   1 → 6   | ✅
| Đỗ Nhật Huy          | getOrdersByUserId()                |    All    |
| Đỗ Nhật Huy          | cancelOrder()                      |    All    |
| Nguyễn Đình Quốc Huy | checkQuantityAndPriceBeforeOrder() |    All    | ✅
| Nguyễn Đình Quốc Huy | createOrder()                      |  7 → 11   | ✅

---
