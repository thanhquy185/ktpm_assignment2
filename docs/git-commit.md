# 📘 Git Branch + Conventional Commits (bản dễ hiểu)

## 1. Git Branch (làm nhóm)

Mỗi người tạo 1 nhánh riêng theo tên mình

```
main
 ├── chau
 ├── nhuy
 ├── qhuy
```

### Cách dùng

```bash
git checkout -b chau
```

Ai làm nhánh người đó, không code trực tiếp trên `main`

---

## 2. Quy trình làm việc

1. Tạo nhánh

```bash
git checkout -b ten-ban
```

2. Code + commit

```bash
git add .
git commit -m "feat: add login"
```

3. Push lên Git

```bash
git push origin ten-ban
```

4. Xong → tạo Pull Request → merge vào `main`

---

## 3. Conventional Commits (ghi commit cho rõ ràng)

Format:

```
<type>: <description>
```

### Các type quan trọng

- **feat**: thêm tính năng mới

  ```
  feat: add login page
  ```

- **fix**: sửa lỗi

  ```
  fix: fix login bug
  ```

- **refactor**: chỉnh code nhưng không thêm tính năng

  ```
  refactor: clean auth service
  ```

- **docs**: tài liệu

  ```
  docs: update README
  ```

- **style**: format code (không ảnh hưởng logic)

  ```
  style: format code
  ```

- **test**: thêm/sửa test

  ```
  test: add unit test
  ```

- **chore**: việc phụ (config, build...)

  ```
  chore: update dependencies
  ```

### Khi trong 1 commit có nhiều thay đổi

Nếu cùng 1 chức năng → có thể gộp

```
fix(auth): fix login error and token refresh
```

Nếu nhiều chức năng khác nhau → KHÔNG gộp, phải tách

```
fix(auth): fix login bug
fix(cart): fix checkout error
feat(profile): add avatar upload
```

Tránh commit kiểu:

```
fix: fix login, cart, payment, UI
```

---

## 4. Quy tắc commit

- 1 commit = 1 việc nhỏ
- Commit phải dễ hiểu
- Nếu có nhiều thứ không liên quan → tách commit

---

## 5. Quy tắc Git nhóm

- ✔ Mỗi người 1 nhánh
- ✔ Không code trực tiếp `main`
- ✔ Commit rõ ràng theo Conventional Commits
- ✔ Xong việc → merge qua Pull Request

---
