package com.ecommerce.model.discount;

/**
 * Chiến lược giảm giá theo tỷ lệ phần trăm.
 */
public class PercentageDiscount implements DiscountStrategy {
    private double percentage; // Tỷ lệ phần trăm giảm giá (0 - 100)

    public PercentageDiscount(double percentage) {
        if (percentage > 100 || percentage <= 0) {
            throw new IllegalArgumentException("Phan tram phai nam trong khoang 0 den 100");
        }
        this.percentage = percentage;
    }

    // Áp dụng tính số tiền giảm theo phần trăm
    @Override
    public double applyDiscount(double originalTotal) {
        return originalTotal * this.percentage / 100;
    }
}
