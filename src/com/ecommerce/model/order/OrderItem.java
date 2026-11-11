package com.ecommerce.model.order;

import java.math.BigDecimal;

import com.ecommerce.model.product.Product;

/**
 * Đại diện cho một mặt hàng cụ thể trong đơn hàng.
 */
public class OrderItem {
    /** Sản phẩm được đặt mua */
    private final Product product;

    /** Số lượng đặt mua */
    private int quantity;

    /** Đơn giá tại thời điểm đặt hàng */
    private final BigDecimal unitPrice;

    public OrderItem(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Sản phẩm không được để trống");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng mặt hàng phải lớn hơn 0");
        }
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = product.getPrice();
    }

    /**
     * Tính thành tiền của mặt hàng (đơn giá * số lượng).
     */
    public BigDecimal calculateSubtotal() {
        return BigDecimal.valueOf(this.quantity).multiply(unitPrice);
    }

    /**
     * Cập nhật số lượng của mặt hàng.
     */
    void updateQuantity(int newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Số lượng mặt hàng " + newQuantity + " không hợp lệ, phải lớn hơn 0");
        }
        this.quantity = newQuantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return this.unitPrice;
    }
}
