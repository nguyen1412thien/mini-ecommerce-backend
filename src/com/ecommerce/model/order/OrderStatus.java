package com.ecommerce.model.order;

/**
 * Các trạng thái trong vòng đời của đơn hàng.
 */
public enum OrderStatus {
    /** Đơn hàng nháp, chưa có sản phẩm nào */
    DRAFT,
    /** Đơn hàng đã được thêm sản phẩm và giữ chỗ kho thành công */
    CREATED,
    /** Đơn hàng đã được người mua xác nhận, chốt trừ tồn vật lý */
    CONFIRMED,
    /** Đơn hàng đã thanh toán thành công */
    PAID,
    /** Đơn hàng đã bị hủy (do người dùng hủy hoặc hết hạn) */
    CANCELLED
}
