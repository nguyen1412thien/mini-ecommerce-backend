package com.ecommerce.model.order;

import com.ecommerce.model.product.Product;

/**
 * Đại diện cho một mặt hàng cụ thể trong đơn hàng.
 */
public class OrderItem {
    private final Product product;  // Sản phẩm được đặt mua
    private int quantity;           // Số lượng đặt mua
    private final double unitPrice; // Đơn giá tại thời điểm đặt hàng

    public OrderItem(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("San pham khong duoc de trong");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("So luong phai lon hon 0");
        }
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = product.getPrice();
    }

    // Tính thành tiền của mặt hàng (đơn giá * số lượng)
    public double calculateSubtotal() {
        return this.quantity * this.unitPrice;
    }

    // Cập nhật số lượng của mặt hàng
    public void updateQuantity(int newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("So luong hang phai lon hon 0");
        }
        this.quantity = newQuantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }
}
