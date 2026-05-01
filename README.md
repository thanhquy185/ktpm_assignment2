# 📘 ShopCart - Dự án kiểm thử E-commerce

ShopCart là dự án web bán hàng fullstack (React + Spring Boot) dùng để thực hành kiểm thử phần mềm: unit test, integration test, E2E và CI/CD.

---

## 👥 Thành viên nhóm

| Thành viên           | MSSV       |
| -------------------- | ---------- |
| Trần Thanh Quy       | 3123560072 |
| Danh Thị Ngọc Châu   | 3123560005 |
| Đỗ Nhật Huy          | 3123560032 |
| Nguyễn Đình Quốc Huy | 3123560034 |

---

## 🏗️ Công nghệ chính

### Backend

- Spring Boot 4, Java 21
- JUnit 5, Mockito
- JPA + PostgreSQL / H2
- Spring Security

### Frontend

- React 19 + TypeScript
- Vite
- Vitest + React Testing Library
- Playwright (E2E)

### CI/CD

- GitHub Actions
- Codecov (coverage)

---

## 📁 Cấu trúc dự án

```
ktpm_assignment2/
├── docs/
│   ├── git-commit.md
│   ├── setup-database.md
|
├── frontend/
│   ├── src/
│   │   ├── services/               # API service layer
│   │   ├── utils/                  # Utilities & validation
│   │   ├── types/                  # TypeScript types
│   │   ├── components/             # React components
│   │   ├── hooks/                  # Custom hooks
│   │   └── App.tsx
│   ├── e2e/                        # Playwright E2E tests
│   ├── vite.config.ts
│   ├── vitest.config.ts
│   ├── playwright.config.ts
│   ├── package.json
│   └── README.md
|
├── backend/
│   ├── src/main/java/com/shopcart/
│   │   ├── configs/                # Configs something in project
│   │   ├── controllers/            # REST API endpoints
│   │   ├── dtos/                   # Data Transfer Objects
│   │   ├── entities/               # Database entities
│   │   ├── enums/                  # Entity enums
│   │   ├── exceptions/             # Exceptions for business logic
│   │   ├── repositories/           # Data access layer
│   │   ├── services/               # Business logic
│   │   └── utils/
│   ├── src/main/resources/
│   │   ├── db/migration/*.sql      # Setup flyway
│   │   ├── application.yml/        # Configs project
│   ├── src/test/java/              # Unit tests
│   │   └── integration/            # Integration Tests
│   │   └── mock/                   # Mock Tests
│   │   └── unit/                   # Unit Tests
│   ├── docker-compose.yml          # Docker configuration
│   ├── pom.xml                     # Maven configuration
│   └── README.md
│
├── .github/workflows/
│   └── ci-cd.yml               # GitHub Actions CI/CD
│
└── README.md (this file)
```

---

## 🚀 Chạy dự án

### Clone repository

```bash
git clone https://github.com/thanhquy185/ktpm_assignment2
cd ktpm_assignment2
```

### PostgreSQL

```bash
docker-compose up -d
```

[http://localhost:5433](http://localhost:5433)

### Frontend

```bash
cd frontend
npm install
npm run dev
```

[http://localhost:5173](http://localhost:5173)

### Backend

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

[http://localhost:8080](http://localhost:8080)

---

## 🧪 Testing

### Frontend test

```bash
npm run test
```

### Backend test

```bash
mvn test
```

### E2E test

```bash
npm run e2e
```

---

## 🎯 Chức năng chính

- Giỏ hàng (thêm / xóa / cập nhật)
- Quản lý sản phẩm & tồn kho
- Thanh toán & tạo đơn hàng

---

## 🧪 Các loại test

- Unit test: test từng hàm riêng lẻ
- Integration test: test giữa các module
- E2E test: test toàn bộ luồng người dùng
- Mock test: giả lập API / service

---

## 🔄 Workflow làm việc

1. Tạo nhánh: feature/ten-chuc-nang
2. Code + viết test
3. Chạy test
4. Push code
5. Tạo Pull Request
6. CI chạy tự động
7. Merge vào main

---

## 📊 Mục tiêu chất lượng

- Backend coverage ≥ 85%
- Frontend coverage ≥ 90%
- Không merge khi test fail

---

## 🔐 Kiểm thử quan trọng

- Kiểm tra giỏ hàng
- Kiểm tra tồn kho
- Kiểm tra thanh toán
- Kiểm tra bảo mật API

---

## ✨ Tóm tắt

ShopCart là dự án mô phỏng hệ thống e-commerce thật, tập trung vào:

- Code clean
- Test đầy đủ
- CI/CD tự động
- Quy trình làm việc nhóm chuẩn Git

---

**Version**: 1.1.0 (Vietnamese simplified)
