package com.ecommerce.model.order;

// Các trạng thái trong vòng đời của đơn hàng
public enum OrderStatus {
    DRAFT,
    CREATED,
    CONFIRMED,
    PAID,
    CANCELLED
}
