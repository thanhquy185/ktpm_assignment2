# ShopCart - E-commerce Testing Project

A full-stack e-commerce application built with React 19 and Spring Boot 4.0, designed for comprehensive software testing including unit tests, integration tests, E2E tests, and CI/CD pipelines.

## 📋 Project Overview

ShopCart is a learning project for software testing best practices covering:
- **Shopping Cart Management** - Add/remove items, update quantities
- **Product Inventory** - Stock management and availability checking
- **Purchase/Checkout** - Order creation with pricing and discount calculations
- **Test Coverage** - Unit, integration, E2E, mock, performance, and security tests

## 🏗️ Technology Stack

### Backend
- **Framework**: Spring Boot 4.0.5
- **Language**: Java 21
- **Testing**: JUnit 5, Mockito, Spring Test
- **Build Tool**: Maven
- **Database**: H2 (testing), PostgreSQL (production)
- **ORM**: Spring Data JPA
- **Security**: Spring Security
- **Code Coverage**: JaCoCo

### Frontend
- **Framework**: React 19
- **Language**: TypeScript
- **Build Tool**: Vite
- **Testing**: Vitest, React Testing Library
- **E2E**: Playwright
- **HTTP Client**: Axios
- **Styling**: CSS3

### CI/CD
- **Platform**: GitHub Actions
- **Coverage Reports**: Codecov
- **Artifacts**: Playwright HTML Report

## 📁 Project Structure

```
ShopCart/
├── backend/
│   ├── src/main/java/com/shopcart/
│   │   ├── entity/              # Database entities
│   │   ├── repository/          # Data access layer
│   │   ├── service/             # Business logic
│   │   ├── controller/          # REST API endpoints
│   │   └── dto/                 # Data Transfer Objects
│   ├── src/test/java/           # Unit tests
│   ├── pom.xml                  # Maven configuration
│   └── README.md
│
├── frontend/
│   ├── src/
│   │   ├── services/            # API service layer
│   │   ├── utils/               # Utilities & validation
│   │   ├── types/               # TypeScript types
│   │   ├── components/          # React components
│   │   ├── hooks/               # Custom hooks
│   │   └── App.tsx
│   ├── e2e/                     # Playwright E2E tests
│   ├── vite.config.ts
│   ├── vitest.config.ts
│   ├── playwright.config.ts
│   ├── package.json
│   └── README.md
│
├── .github/workflows/
│   └── ci-cd.yml                # GitHub Actions CI/CD
│
└── README.md (this file)
```

## 🚀 Getting Started

### Prerequisites
- **Java**: JDK 21+
- **Node.js**: v22+
- **npm**: v10+
- **Maven**: 3.8.1+

### Installation

#### 1. Clone Repository
```bash
git clone <repository-url>
cd ShopCart
```

#### 2. Setup Backend
```bash
cd backend

# Install dependencies (Maven auto-downloads)
./mvnw clean install

# Run the backend server
./mvnw spring-boot:run
```

Backend runs on: `http://localhost:8080`

#### 3. Setup Frontend
```bash
cd frontend

# Install dependencies
npm install

# Run development server
npm run dev
```

Frontend runs on: `http://localhost:5173`

## 🧪 Testing

### Backend Unit Tests
```bash
cd backend

# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=CartServiceTest

# Generate JaCoCo coverage report
./mvnw clean test jacoco:report
# Report: backend/target/site/jacoco/index.html
```

### Frontend Unit Tests
```bash
cd frontend

# Run tests in watch mode
npm run test

# Run with coverage
npm run test:coverage

# Open UI dashboard
npm run test:ui
```

### End-to-End Tests
```bash
cd frontend

# Run Playwright E2E tests
npm run e2e

# Run with headed browser (visible)
npm run e2e:headed

# Debug mode
npm run e2e:debug

# View HTML report
npx playwright show-report
```

## 🔧 API Endpoints

### Cart API
```
POST   /api/cart/add              - Add product to cart
GET    /api/cart                  - Get cart
PUT    /api/cart/update/{id}      - Update quantity
DELETE /api/cart/remove/{id}      - Remove product
```

### Order API
```
POST   /api/orders                - Create order
GET    /api/orders/{orderId}      - Get order details
GET    /api/orders/user/{userId}  - Get user orders
PUT    /api/orders/{id}/cancel    - Cancel order
```

### Inventory API
```
GET    /api/inventory/check/{id}  - Check stock availability
```

## 📊 Test Coverage Goals

- **Backend**: ≥ 85% (JaCoCo)
- **Frontend**: ≥ 90% (Vitest)
- **E2E**: All critical user flows

## 🔄 CI/CD Pipeline

GitHub Actions automatically:
1. Runs backend unit tests with JaCoCo
2. Runs frontend unit tests with coverage
3. Installs Playwright browsers
4. Runs E2E tests on Chromium, Firefox, WebKit
5. Uploads coverage to Codecov
6. Stores Playwright HTML report

View workflow: `.github/workflows/ci-cd.yml`

## 📝 Testing Best Practices

### TDD (Test-Driven Development)
- Write tests first (Red)
- Implement code to pass (Green)
- Refactor (Refactor)

### Unit Tests
- Single responsibility per test
- Use mocking for external dependencies
- Meaningful assertion messages
- Arrange-Act-Assert (AAA) pattern

### Integration Tests
- Test component interactions
- Mock only external services
- Use realistic test data

### E2E Tests
- Test complete user workflows
- Use Page Object Model (POM)
- Test across multiple browsers
- Validate UI and backend integration

### Mock Testing
- Mock external APIs and services
- Verify mock interactions
- Use when testing in isolation

## 🐛 Debugging

### Backend
```bash
cd backend
./mvnw spring-boot:run -Dspring-boot.run.arguments="--debug"
```

### Frontend
```bash
cd frontend
npm run dev

# Open Chrome DevTools: F12
```

### E2E
```bash
cd frontend
npm run e2e:debug

# Step through tests in Playwright Inspector
```

## 📚 Documentation

- [Backend Testing Guide](backend/README.md)
- [Frontend Testing Guide](frontend/README.md)
- Test case templates and scenarios in assignment PDF

## 🎯 Key Test Scenarios

### Cart Management
- ✅ Add product to cart (valid quantity)
- ✅ Add product with insufficient stock
- ✅ Add product with invalid quantity
- ✅ Update quantity in cart
- ✅ Remove product from cart
- ✅ Cart total price calculation

### Checkout/Order
- ✅ Create order with valid items
- ✅ Apply discount coupon
- ✅ Calculate shipping fee
- ✅ Validate inventory before checkout
- ✅ Cancel order and restore stock
- ✅ Insufficient stock during checkout

### Inventory
- ✅ Check product availability
- ✅ Deduct stock after order
- ✅ Reserve stock for pending orders
- ✅ Prevent negative stock

## 🔐 Security Testing

- SQL Injection prevention
- XSS protection
- CSRF token validation
- API authorization checks
- Input validation

## ⚡ Performance Testing

- Response time metrics
- Throughput measurement
- Load testing with k6
- Error rate monitoring

## 📋 Development Workflow

1. Create feature branch: `git checkout -b feature/cart-improvements`
2. Write tests first (TDD)
3. Implement feature
4. Run all tests: `npm run test` + `./mvnw test`
5. Push to branch
6. CI/CD pipeline runs automatically
7. Create Pull Request
8. Code review + approve
9. Merge to main

## 🤝 Contributing

1. Follow project structure
2. Write tests for new features
3. Maintain ≥ 85% backend coverage, ≥ 90% frontend coverage
4. Update README if adding new features
5. Commit with conventional commits

## 📄 License

This is an educational project for Saigon University.

## 📞 Support

For issues or questions:
- Check existing test cases
- Review error messages in terminal
- Check CI/CD logs on GitHub
- Review API responses in Network tab

## ✨ Features in Scope

- [x] Shopping Cart functionality
- [x] Product Inventory management
- [x] Order creation & management
- [x] Unit testing (backend & frontend)
- [x] Integration testing
- [x] E2E testing with Playwright
- [x] Mock testing
- [x] CI/CD pipeline
- [x] Code coverage reporting
- [ ] Performance testing (k6)
- [ ] Security testing (OWASP)

---

**Version**: 1.1.0  
**Last Updated**: April 2026  
**Course**: Software Testing (Kiểm Thử Phần Mềm)
