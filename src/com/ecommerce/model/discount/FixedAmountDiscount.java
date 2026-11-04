package com.ecommerce.model.discount;

/**
 * Chiến lược giảm giá theo một số tiền cố định.
 */
public class FixedAmountDiscount implements DiscountStrategy {
    private double amount; // Số tiền giảm giá cố định

    public FixedAmountDiscount(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("So tien giam phai lon hon 0");
        }
        this.amount = amount;
    }

    // Áp dụng trừ số tiền cố định (không vượt quá tổng giá trị đơn hàng)
    @Override
    public double applyDiscount(double originalTotal) {
        if (this.amount > originalTotal) {
            return originalTotal;
        } else {
            return amount;
        }
    }
}
