# 🗄️ Database Setup Guide (Docker + PostgreSQL + DBeaver + Spring Boot + Flyway)

Docker → PostgreSQL (port 4333) → DBeaver → Spring Boot → Flyway → Tạo database

Thông tin DB (file backend/src/main/resources/application.yml):

- Host: localhost
- Port: 4333
- DB: shopcart
- User: admin
- Pass: 123456

---

## 1. Chạy PostgreSQL bằng Docker

```bash
cd backend
docker-compose up -d (download)
docker-compose down -v (delete)
```

---

## 2. Sử dụng DBeaver

- Tải DBeaver (hoặc tương đương)
- Tạo Connection với thông tin DB
- Nhấn Test Connection nếu OK là xong

---

## 3. Chạy backend

Chạy backend bằng lệnh:

```bash
cd backend
mvn spring-boot:run
```

Flyway sẽ:

- Tự động chạy khi start app
- Chạy theo thứ tự version
- Tìm file trong:

```
src/main/resources/db/migration
```

---

## 4. Flow chạy hệ thống

Docker PostgreSQL
→ DBeaver test connection
→ Spring Boot connect DB
→ Flyway chạy migration
→ Tạo bảng + dữ liệu

---

## 5. Lưu ý

- Không sửa SQL migration đã chạy
- Mỗi thay đổi DB tạo file V3, V4...
- Không chỉnh DB bằng tay khi dùng Flyway

---
