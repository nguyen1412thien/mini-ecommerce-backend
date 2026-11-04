package com.ecommerce.model.discount;

/**
 * Interface định nghĩa chiến lược tính toán giảm giá (Strategy Pattern).
 */
public interface DiscountStrategy {
    // Tính toán số tiền được giảm dựa trên tổng tiền ban đầu
    double applyDiscount(double originalTotal);
}
