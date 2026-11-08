# MINI E-COMMERCE - BACKEND SERVICE

> **Công nghệ:** Java 17/21, Spring Boot 3, Spring Data JPA, PostgreSQL / MySQL, Spring Security 6 + JWT  
> **Kiến trúc:** 3-tier Architecture (Controller - Service - Repository) & Clean Domain Modeling

---

## CẤU TRÚC THƯ MỤC BACKEND (PACKAGE-BY-FEATURE)

```text
backend/
└── src/
    └── com/
        └── ecommerce/
            ├── model/                          # Domain Entities & Business Strategies
            │   ├── product/
            │   │   └── Product.java            # Rich Entity: Invariants, equals/hashCode, Math.addExact
            │   ├── order/
            │   │   ├── Order.java              # Aggregate Root: Defensive copying, Voucher post-conditions
            │   │   └── OrderItem.java          # Value Object: Price snapshot, Stock invariant check
            │   └── discount/
            │       ├── DiscountStrategy.java   # Strategy Pattern Interface
            │       ├── PercentageDiscount.java # Giảm giá theo phần trăm (0 - 100%)
            │       └── FixedAmountDiscount.java# Giảm giá cố định (chặn vượt quá tổng tiền)
            │
            ├── repository/                     # Data Access Layer
            │   └── ProductRepository.java      # In-Memory O(1) HashMap, Stream API Pipeline & Collectors, Lambda Sorting
            │
            ├── exception/                      # Custom Business Exceptions
            │   └── ProductNotFoundException.java # Runtime Exception khi không tìm thấy thực thể
            │
            └── Main.java                       # Kịch bản kiểm thử tích hợp (Integration Verification Suite)
```

---

## ĐẶC TẢ KIẾN TRÚC & ĐIỂM SÁNG KỸ THUẬT (ENGINEERING HIGHLIGHTS)

1. **Rich Domain Modeling & Tính Đóng Gói (Encapsulation):**
   - Các Entity tự chịu trách nhiệm bảo vệ tính toàn vẹn trạng thái nội tại (Invariants) qua constructor và các phương thức nghiệp vụ (`deductStock`, `updatePrice`). Không mở setter bừa bãi.
2. **Nghiệp vụ Chốt Giá (Price Snapshot) & Chống Rò Rỉ Tham Chiếu (Defensive Copying):**
   - `OrderItem` đóng băng `final double unitPrice` tại thời điểm mua, độc lập với biến động giá tương lai của sản phẩm.
   - `Order` bảo vệ giỏ hàng khỏi bị sửa đổi trái phép bằng `Collections.unmodifiableList()`.
3. **Hợp Đồng Sinh Tử `equals()` & `hashCode()`:**
   - Định danh đối tượng dựa trên khóa nghiệp vụ (`id`), đảm bảo độ chính xác tuyệt đối khi quản lý trong các tập hợp băm (`HashSet`, `HashMap`).
4. **Đa Hình & Strategy Pattern với Hậu Điều Kiện (Post-condition Safeguards):**
   - Tách biệt thuật toán giảm giá qua `DiscountStrategy`.
   - `Order` tự phòng vệ: từ chối số tiền giảm âm và giới hạn mức giảm tối đa bằng tổng tiền đơn hàng (tiền thanh toán không bao giờ âm).
5. **Kho Lưu Trữ Tối Ưu Tốc Độ $O(1)$ & Sắp Xếp Lambda:**
   - Loại bỏ Anti-pattern tra cứu 2 lần (`containsKey` + `get`).
   - Chốt chặn phòng vệ tham số (Defensive Parameter Validation) ngăn chặn dữ liệu `null`/rỗng.
   - Sắp xếp động linh hoạt với `Comparator` và cú pháp Biểu thức Lambda.
6. **Bảo Vệ Dòng Tiền & Chống Tràn Số (Financial Computing Safeguards):**
   - Sử dụng `Math.addExact()` trong `addStock` để chặn đứng rủi ro tràn số nguyên (Integer Overflow).
   - Kiểm chứng thực nghiệm sai số dấu phẩy động IEEE 754 của `double` và quy chuẩn so sánh tiền tệ với `BigDecimal.compareTo()`.
7. **Khai Thác Java Stream API Pipeline & Gom Nhóm Thống Kê (Stream Collectors):**
   - Xây dựng các luồng truy vấn dữ liệu động qua chuỗi `stream().filter().sorted().limit().toList()` (lọc khoảng giá, lấy danh sách tên, phân loại còn hàng, cắt lát Top N sản phẩm).
   - Thống kê tài chính và gom nhóm dữ liệu nâng cao với `Collectors.summingDouble`, `Collectors.averagingDouble`, `Collectors.groupingBy` (phân nhóm `IN_STOCK`/`OUT_OF_STOCK`) và `Collectors.joining`.

---

## HƯỚNG DẪN BIÊN DỊCH VÀ CHẠY THỬ NGHIỆM

Mở Terminal tại thư mục `backend`:

```bash
# 1. Biên dịch toàn bộ mã nguồn
javac -d bin src/com/ecommerce/*/*.java src/com/ecommerce/model/*/*.java src/com/ecommerce/Main.java

# 2. Khởi chạy bộ kiểm thử tích hợp
java -cp bin com.ecommerce.Main
```
