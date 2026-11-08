package com.ecommerce.model.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ecommerce.model.discount.DiscountStrategy;

/**
 * Đại diện cho đơn hàng chứa danh sách mặt hàng và tính toán thanh toán.
 */
public class Order {
    private String id; // Mã đơn hàng
    private String customerName; // Tên khách hàng
    private List<OrderItem> items = new ArrayList<>(); // Danh sách các mặt hàng trong đơn

    public Order(String id, String customerName) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID khong duoc de trong");
        }
        if (customerName == null || customerName.isBlank()) {
            throw new IllegalArgumentException("Ten khac hang khong duoc de rong");
        }
        this.id = id;
        this.customerName = customerName;
    }

    // Thêm một mặt hàng vào đơn hàng
    public void addItem(OrderItem item) {
        if (item == null) {
            throw new IllegalArgumentException("item khong duoc de trong");
        }
        this.items.add(item);
    }

    // Tính tổng tiền các mặt hàng trước khi áp dụng giảm giá
    public BigDecimal calculateTotal() {
        BigDecimal total = new BigDecimal(0);
        for (OrderItem item : items) {
            total = total.add(item.calculateSubtotal());
        }
        return total;
    }

    // Tính tổng tiền thanh toán cuối cùng sau khi áp dụng chiến lược giảm giá
    public BigDecimal calculateFinalTotal(DiscountStrategy discountStrategy) {
        BigDecimal total = this.calculateTotal();
        if (discountStrategy == null) {
            return total;
        }
        BigDecimal discount = discountStrategy.applyDiscount(total);
        if (discount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Số tiền giảm giá không hợp lệ");
        }
        if (discount.compareTo(total) > 0) {
            return BigDecimal.ZERO;
        } else {
            return total.subtract(discount);
        }
    }

    public String getCustomerName() {
        return this.customerName;
    }

    public String getId() {
        return this.id;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(this.items);
    }
}
