package com.ecommerce.model.discount;

import java.math.BigDecimal;

/**
 * Chiến lược giảm giá theo một số tiền cố định.
 */
public class FixedAmountDiscount implements DiscountStrategy {
    private BigDecimal amount; // Số tiền giảm giá cố định

    public FixedAmountDiscount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Số tiền giảm giá không được để trống");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số tiền giảm giá " + amount + " không hợp lệ, phải lớn hơn 0");
        }
        this.amount = amount;
    }

    // Áp dụng trừ số tiền cố định (không vượt quá tổng giá trị đơn hàng)
    @Override
    public BigDecimal applyDiscount(BigDecimal originalTotal) {
        if (originalTotal == null) {
            throw new IllegalArgumentException("Tổng đơn hàng không được để trống");
        }
        if (originalTotal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Tổng đơn hàng " + originalTotal + " không hợp lệ, phải lớn hơn 0");
        }
        if (this.amount.compareTo(originalTotal) > 0) {
            return originalTotal;
        } else {
            return amount;
        }
    }
}
