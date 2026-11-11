package com.ecommerce.model.order;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ecommerce.model.discount.DiscountStrategy;

/**
 * Đại diện cho đơn hàng chứa danh sách mặt hàng và tính toán thanh toán.
 */
public class Order {
    private final String id; // Mã đơn hàng
    private final String customerName; // Tên khách hàng
    private List<OrderItem> items = new ArrayList<>(); // Danh sách các mặt hàng trong đơn
    private OrderStatus status = OrderStatus.DRAFT; // Trạng thái hiện tại của đơn hàng
    private Instant T0; // Mốc thêm mặt hàng đầu tiên
    private Instant confirmAt; // Mốc xác nhận đơn

    public Order(String id, String customerName) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID không được để trống");
        }
        if (customerName == null || customerName.isBlank()) {
            throw new IllegalArgumentException("Tên khách hàng không được để trống");
        }
        this.id = id;
        this.customerName = customerName;
    }

    // Tìm mặt hàng trong đơn hàng theo ID sản phẩm
    private OrderItem findItemByProductId(String productId) {
        return this.items.stream()
                .filter(p -> p.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);
    }

    // Thêm một mặt hàng vào đơn hàng
    public void addItem(OrderItem item, Instant now) {
        if (item == null) {
            throw new IllegalArgumentException("Mặt hàng không được để trống");
        }
        if (now == null) {
            throw new IllegalArgumentException("Thời gian không được null");
        }
        Instant expiresAt;
        if (status == OrderStatus.DRAFT) {
            expiresAt = now.plus(Duration.ofMinutes(15));
        } else if (status == OrderStatus.CREATED) {
            if (!now.isBefore(T0.plus(Duration.ofMinutes(15)))) {
                throw new IllegalStateException("Đơn hàng đã hết hạn giữ chỗ");
            } else {
                expiresAt = this.T0.plus(Duration.ofMinutes(15));
            }
        } else {
            throw new IllegalStateException("Không thể thêm mặt hàng khi đơn hàng đã được " + status.toString());
        }
        OrderItem existingItem = findItemByProductId(item.getProduct().getId());
        if (existingItem == null) {
            item.getProduct().reserveStock(this.id, item.getQuantity(), expiresAt, now);
            this.items.add(item);
            if (status == OrderStatus.DRAFT) {
                this.status = OrderStatus.CREATED;
                this.T0 = now;
            }
        } else {
            int newTotal = Math.addExact(existingItem.getQuantity(), item.getQuantity());
            existingItem.getProduct().reserveStock(this.id, newTotal, expiresAt, now);
            existingItem.updateQuantity(newTotal);
        }
    }

    // Tính tổng tiền các mặt hàng trước khi áp dụng giảm giá
    public BigDecimal calculateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : items) {
            total = total.add(item.calculateSubtotal());
        }
        return total;
    }

    // Tính tổng tiền thanh toán cuối cùng sau khi áp dụng chiến lược giảm giá
    public BigDecimal calculateFinalTotal(DiscountStrategy discountStrategy) {
        if (items.isEmpty()) {
            throw new IllegalStateException("Không thể tính tiền đơn hàng rỗng");
        }
        BigDecimal total = this.calculateTotal();
        if (discountStrategy == null) {
            return total;
        }
        BigDecimal discount = discountStrategy.applyDiscount(total);
        if (discount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Số tiền giảm giá " + discount + " không hợp lệ, không được nhỏ hơn 0");
        }
        if (discount.compareTo(total) > 0) {
            return BigDecimal.ZERO;
        } else {
            return total.subtract(discount);
        }
    }

    // Cập nhật số lượng của một sản phẩm đã có trong đơn hàng
    public void updateItemQuantity(String productId, int newQuantity, Instant now) {
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("Mã sản phẩm không được để trống");
        }
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Số lượng " + newQuantity + " không hợp lệ, phải lớn hơn 0");
        }
        if (now == null) {
            throw new IllegalArgumentException("Thời gian hiện tại không được để trống");
        }
        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException("Không thể cập nhật số lượng đơn hàng ở trạng thái " + status.toString());
        }
        if (!now.isBefore(this.T0.plus(Duration.ofMinutes(15)))) {
            throw new IllegalStateException("Đơn hàng đã hết hạn giữ chỗ");
        }
        OrderItem existingItem = findItemByProductId(productId);
        if (existingItem == null) {
            throw new IllegalArgumentException("Không tìm thấy sản phẩm có mã " + productId + " trong đơn hàng");
        }
        existingItem.getProduct().reserveStock(this.id, newQuantity, this.T0.plus(Duration.ofMinutes(15)), now);
        existingItem.updateQuantity(newQuantity);
    }

    // Xác nhận đơn hàng đang ở trạng thái CREATED
    public void confirm(Instant now) {
        if (now == null) {
            throw new IllegalArgumentException("Thời gian hiện tại không được để trống");
        }
        if (this.status != OrderStatus.CREATED) {
            throw new IllegalStateException("Không thể xác nhận đơn hàng khi ở trạng thái " + this.status.toString());
        }
        if (this.items.isEmpty()) {
            throw new IllegalStateException("Không thể xác nhận đơn hàng khi chưa có mặt hàng nào");
        }
        if (!now.isBefore(this.T0.plus(Duration.ofMinutes(15)))) {
            throw new IllegalStateException("Đơn hàng đã hết hạn giữ chỗ");
        }
        for (OrderItem item : items) {
            item.getProduct().commitReservation(this.id);
        }
        this.status = OrderStatus.CONFIRMED;
        this.confirmAt = now;
    }

    // Ghi nhận thanh toán thành công cho đơn hàng
    public void markAsPaid(Instant paidAt) {
        if (paidAt == null) {
            throw new IllegalArgumentException("Thời điểm thanh toán không được để trống");
        }
        if (this.status != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Không thể thanh toán đơn hàng khi ở trạng thái " + this.status.toString());
        }
        if (!paidAt.isBefore(this.confirmAt.plus(Duration.ofMinutes(15)))) {
            throw new IllegalStateException("Đã quá thời hạn thanh toán đơn hàng");
        }
        this.status = OrderStatus.PAID;
    }

    // Hủy đơn hàng và xử lý tồn kho tương ứng
    public void cancel() {
        switch (this.status) {
            case PAID -> {
                throw new IllegalStateException("Không thể huỷ đơn hàng khi đã thanh toán");
            }
            case CANCELLED -> {
                throw new IllegalStateException("Đơn hàng đã bị huỷ trước đó");
            }
            case DRAFT -> {
                this.status = OrderStatus.CANCELLED;
            }
            case CREATED -> {
                for (OrderItem item : items) {
                    item.getProduct().releaseReservation(this.id);
                }
                this.status = OrderStatus.CANCELLED;
            }
            case CONFIRMED -> {
                for (OrderItem item : items) {
                    item.getProduct().addStock(item.getQuantity());
                }
                this.status = OrderStatus.CANCELLED;
            }
            default -> {

            }
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

    public OrderStatus getStatus() {
        return status;
    }

    public Instant getT0() {
        return T0;
    }

    public Instant getConfirmAt() {
        return confirmAt;
    }
}
