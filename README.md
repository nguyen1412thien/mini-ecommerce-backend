# MINI E-COMMERCE - BACKEND SERVICE

> **Công nghệ:** Java 17/21, Spring Boot 3, Spring Data JPA, PostgreSQL / MySQL, Spring Security 6 + JWT  
> **Kiến trúc:** 3-tier Architecture (Controller - Service - Repository)

## 📁 Cấu trúc thư mục Backend:
```text
backend/
└── src/
    └── com/
        └── ecommerce/
            ├── model/          # Các đối tượng Entity (Product, Order, OrderItem, Discount...)
            ├── repository/     # Tầng truy xuất dữ liệu (ProductRepository...)
            ├── exception/      # Ngoại lệ nghiệp vụ riêng (ProductNotFoundException...)
            ├── comparator/     # Các thước đo sắp xếp sản phẩm
            └── Main.java       # File kiểm thử hệ thống
```
