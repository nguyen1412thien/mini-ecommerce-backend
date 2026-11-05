package com.ecommerce.model.product;

import java.util.Objects;

/**
 * Quản lý thông tin và các thao tác nghiệp vụ đối với sản phẩm.
 */
public class Product {
    private String id; // Mã định danh sản phẩm
    private String name; // Tên sản phẩm
    private double price; // Đơn giá sản phẩm
    private int stockQuantity; // Số lượng tồn kho

    public Product(String id, String name, double price, int stockQuantity) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã sản phẩm không được để trống");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Giá sản phẩm phải lớn hơn 0");
        }
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("Số lượng tồn kho không thể âm");
        }
        this.id = id;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    // Trừ số lượng tồn kho khi bán hàng
    public void deductStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng trừ tồn kho phải lớn hơn 0");
        }
        if (quantity > this.stockQuantity) {
            throw new IllegalStateException("Hàng trong kho không đủ! Hiện chỉ còn: " + this.stockQuantity);
        }
        this.stockQuantity -= quantity;
    }

    // Nhập thêm hàng vào kho
    public void addStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng thêm vào kho phải lớn hơn 0");
        }
        // this.stockQuantity += quantity;
        this.stockQuantity = Math.addExact(stockQuantity, quantity);
    }

    // Cập nhật giá bán mới
    public void updatePrice(double newPrice) {
        if (newPrice <= 0) {
            throw new IllegalArgumentException("Giá mới phải lớn hơn 0");
        }
        this.price = newPrice;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public double getPrice() {
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
