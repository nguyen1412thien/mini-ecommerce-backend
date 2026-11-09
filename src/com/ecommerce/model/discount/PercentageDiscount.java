package com.ecommerce.model.discount;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Chiến lược giảm giá theo tỷ lệ phần trăm.
 */
public class PercentageDiscount implements DiscountStrategy {
    private double percentage; // Tỷ lệ phần trăm giảm giá (0 - 100)

    public PercentageDiscount(double percentage) {
        if (percentage >= 100 || percentage <= 0) {
            throw new IllegalArgumentException(
                    "Phần trăm giảm giá " + percentage + " không hợp lệ, phải lớn hơn 0% và nhỏ hơn 100%");
        }
        this.percentage = percentage;
    }

    // Áp dụng tính số tiền giảm theo phần trăm
    @Override
    public BigDecimal applyDiscount(BigDecimal originalTotal) {
        if (originalTotal == null) {
            throw new IllegalArgumentException("Tổng đơn hàng không được để trống");
        }
        if (originalTotal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Tổng đơn hàng hiện tại là " + originalTotal + " không hợp lệ, phải lớn hơn 0");
        }
        return originalTotal.multiply(BigDecimal.valueOf(percentage)).divide(BigDecimal.valueOf(100)).setScale(0,
                RoundingMode.HALF_UP);
    }
}
