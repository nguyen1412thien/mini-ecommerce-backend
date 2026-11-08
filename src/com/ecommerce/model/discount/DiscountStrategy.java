package com.ecommerce.model.discount;

import java.math.BigDecimal;

/**
 * Interface định nghĩa chiến lược tính toán giảm giá (Strategy Pattern).
 */
public interface DiscountStrategy {
    // Tính toán số tiền được giảm dựa trên tổng tiền ban đầu
    BigDecimal applyDiscount(BigDecimal originalTotal);
}
