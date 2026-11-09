package com.ecommerce.model.product;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Quản lý thông tin và các thao tác nghiệp vụ đối với sản phẩm.
 */
public class Product {
    private String id; // Mã định danh sản phẩm
    private String name; // Tên sản phẩm
    private BigDecimal price; // Đơn giá sản phẩm
    private int stockQuantity; // Số lượng tồn kho

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

    // Trừ số lượng tồn kho khi bán hàng
    public void deductStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng trừ tồn kho " + quantity + " không hợp lệ, phải lớn hơn 0");
        }
        if (quantity > this.stockQuantity) {
            throw new IllegalStateException("Số lượng đặt hàng là " + quantity + ", vượt quá tồn kho hiện có là "
                    + this.stockQuantity + ". Vui lòng giảm số lượng đặt hàng");
        }
        this.stockQuantity -= quantity;
    }

    // Nhập thêm hàng vào kho
    public void addStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng thêm vào kho " + quantity + " không hợp lệ, phải lớn hơn 0");
        }
        // this.stockQuantity += quantity;
        this.stockQuantity = Math.addExact(stockQuantity, quantity);
    }

    // Cập nhật giá bán mới
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
