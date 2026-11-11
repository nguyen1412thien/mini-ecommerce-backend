package com.ecommerce.model.product;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Quản lý thông tin và các thao tác nghiệp vụ đối với sản phẩm.
 */
public class Product {
    /** Mã định danh sản phẩm */
    private final String id;

    /** Tên sản phẩm */
    private String name;

    /** Đơn giá sản phẩm */
    private BigDecimal price;

    /** Số lượng tồn kho */
    private int stockQuantity;

    /** Danh sách phiếu giữ chỗ tồn kho theo từng mã đơn hàng */
    private Map<String, StockReservation> reservations = new HashMap<>();

    public Product(String id, String name, BigDecimal price, int stockQuantity) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã sản phẩm không được để trống");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống");
        }
        if (price == null) {
            throw new IllegalArgumentException("Giá sản phẩm không được để trống");
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Giá sản phẩm " + price + " không hợp lệ, phải lớn hơn 0");
        }
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("Số lượng tồn kho " + stockQuantity + " không hợp lệ, không được âm");
        }
        this.id = id;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    /**
     * Trừ hàng trực tiếp từ phần tồn khả dụng, không được lẹm vào hàng đơn khác đang giữ.
     */
    public void deductStock(int quantity, Instant now) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng trừ hàng phải lớn hơn 0");
        }
        if (now == null) {
            throw new IllegalArgumentException("Thời gian trừ hàng không được để trống");
        }
        int availableStock = getAvailableStock(now);
        if (quantity > availableStock) {
            throw new IllegalArgumentException("Số lượng trừ hàng " + quantity
                    + " vượt quá số lượng tồn kho khả dụng là " + availableStock);
        }
        this.stockQuantity -= quantity;
    }

    /**
     * Nhập thêm hàng vào kho.
     */
    public void addStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng thêm vào kho " + quantity + " không hợp lệ, phải lớn hơn 0");
        }
        // this.stockQuantity += quantity;
        this.stockQuantity = Math.addExact(stockQuantity, quantity);
    }

    /**
     * Cập nhật giá bán mới.
     */
    public void updatePrice(BigDecimal newPrice) {
        if (newPrice == null) {
            throw new IllegalArgumentException("Giá mới không được để trống");
        }
        if (newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Giá mới " + newPrice + " không hợp lệ, phải lớn hơn 0");
        }
        this.price = newPrice;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public int getStockQuantity() {
        return this.stockQuantity;
    }

    /**
     * Tính toán số lượng tồn kho khả dụng tại thời điểm now (tồn vật lý trừ đi các phiếu giữ chỗ còn hạn).
     */
    public int getAvailableStock(Instant now) {
        if (now == null) {
            throw new IllegalArgumentException("Thời điểm hiện tại không được để trống");
        }
        int reservedQuantity; // Tổng số lượng đang bị giữ chỗ (còn hạn)
        reservedQuantity = this.reservations.values().stream()
                .filter(r -> !r.isExpired(now))
                .mapToInt(StockReservation::getQuantity)
                .sum();
        return stockQuantity - reservedQuantity;
    }

    /**
     * Đặt giữ chỗ một lượng sản phẩm cho đơn hàng orderId đến thời điểm expiresAt
     */
    public void reserveStock(String orderId, int quantity, Instant expiresAt, Instant now) {
        if (now == null) {
            throw new IllegalArgumentException("Thời điểm hiện tại không được để trống");
        }
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("Mã đơn hàng đang giữ chỗ không được để trống");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng giữ chỗ " + quantity + " không hợp lệ, phải lớn hơn 0");
        }
        if (expiresAt == null) {
            throw new IllegalArgumentException("Thời điểm hết hạn không được để trống");
        }
        if (!expiresAt.isAfter(now)) {
            throw new IllegalArgumentException("Thời điểm hết hạn phải sau thời điểm hiện tại");
        }
        StockReservation existingReservation = this.reservations.get(orderId);
        int delta;
        if (existingReservation == null || existingReservation.isExpired(now)) {
            delta = quantity;
        } else {
            delta = quantity - existingReservation.getQuantity();
        }
        int availableStock = getAvailableStock(now);
        if (delta > availableStock) {
            throw new IllegalArgumentException("Số lượng giữ chỗ " + quantity
                    + " vượt quá số lượng tồn kho khả dụng là " + availableStock);
        } else {
            this.reservations.put(orderId, new StockReservation(orderId, quantity, expiresAt));
        }
    }

    /**
     * Hủy phiếu giữ chỗ khi đơn hàng bị hủy hoặc hết hạn.
     */
    public void releaseReservation(String orderId) {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("Mã đơn hàng đang giữ chỗ không được để trống");
        }
        this.reservations.remove(orderId);
    }

    /**
     * Chốt trừ tồn vật lý và xóa phiếu giữ chỗ cùng lúc khi đơn hàng được xác nhận.
     */
    public int commitReservation(String orderId) {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("Mã đơn hàng đang giữ chỗ không được để trống");
        }
        StockReservation existingReservation = reservations.get(orderId);
        if (existingReservation == null) {
            throw new IllegalStateException("Không tìm thấy giữ chỗ cho đơn hàng " + orderId);
        } else {
            int quantity = existingReservation.getQuantity();
            this.stockQuantity -= quantity;
            this.reservations.remove(orderId);
            return quantity;
        }
    }

    /**
     * Cập nhật số lượng tồn kho mới, không được nhỏ hơn số lượng đang giữ chỗ còn hiệu lực.
     */
    public void updateStock(int newStock, Instant now) {
        if (newStock < 0) {
            throw new IllegalArgumentException("Số lượng tồn kho không được âm");
        }
        if (now == null) {
            throw new IllegalArgumentException("Thời điểm hiện tại không được để trống");
        }
        int reserved = stockQuantity - getAvailableStock(now);
        if (newStock < reserved) {
            throw new IllegalArgumentException("Số lượng tồn kho mới " + newStock
                    + " không thể nhỏ hơn số lượng đang giữ chỗ là " + reserved);
        }
        this.stockQuantity = newStock;
    }

    @Override
    public String toString() {
        return String.format("Product[id='%s', name='%s', price=%,.0fđ, stock=%d]",
                id, name, price, stockQuantity);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Product other = (Product) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
