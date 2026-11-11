package com.ecommerce.model.product;

import java.time.Instant;

/**
 * Đại diện cho phiếu giữ chỗ tồn kho có thời hạn của một đơn hàng đối với sản
 * phẩm.
 * Phiếu chỉ còn hiệu lực trước mốc thời gian hết hạn (expiresAt).
 */

public class StockReservation {
    /** Mã đơn hàng đang giữ chỗ */
    private String orderId;

    /** Số lượng sản phẩm đang tạm giữ */
    private int quantity;

    /** Thời điểm hết hạn giữ chỗ */
    private Instant expiresAt;

    public StockReservation(String orderId, int quantity, Instant expiresAt) {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("ID đơn đặt hàng không được để trống");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng đặt hàng " + quantity + " không hợp lệ, phải lớn hơn 0");
        }
        if (expiresAt == null) {
            throw new IllegalArgumentException("Thời điểm hết hạn giữ không được để trống");
        }
        this.orderId = orderId;
        this.quantity = quantity;
        this.expiresAt = expiresAt;
    }

    public String getOrderId() {
        return orderId;
    }

    public int getQuantity() {
        return quantity;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    /**
     * Kiểm tra phiếu giữ chỗ đã hết hạn tại thời điểm now hay chưa.
     */
    public boolean isExpired(Instant now) {
        if (now == null) {
            throw new IllegalArgumentException("Thời điểm hiện tại không được để trống");
        }
        return !now.isBefore(expiresAt);
    }
}
