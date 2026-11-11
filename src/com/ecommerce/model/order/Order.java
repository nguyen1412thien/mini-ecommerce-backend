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
    /** Mã đơn hàng */
    private final String id;

    /** Tên khách hàng */
    private final String customerName;

    /** Danh sách các mặt hàng trong đơn */
    private List<OrderItem> items = new ArrayList<>();

    /** Trạng thái hiện tại của đơn hàng (mặc định DRAFT) */
    private OrderStatus status = OrderStatus.DRAFT;

    /** Mốc thêm mặt hàng đầu tiên (bắt đầu tính hạn giữ chỗ 15 phút) */
    private Instant T0;

    /** Mốc xác nhận đơn (bắt đầu tính hạn thanh toán 15 phút) */
    private Instant confirmAt;

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

    /**
     * Tìm mặt hàng trong đơn hàng theo ID sản phẩm.
     */
    private OrderItem findItemByProductId(String productId) {
        return this.items.stream()
                .filter(p -> p.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Thêm một mặt hàng vào đơn hàng.
     */
    public void addItem(OrderItem item, Instant now) {
        if (item == null) {
            throw new IllegalArgumentException("Mặt hàng không được để trống");
        }
        if (now == null) {
            throw new IllegalArgumentException("Thời gian không được null");
        }
        Instant expiresAt;
        if (status == OrderStatus.DRAFT) {
            expiresAt = now.plus(Duration.ofMinutes(15)); // Cộng 15 phút vào Instant
        } else if (status == OrderStatus.CREATED) {
            if (!now.isBefore(T0.plus(Duration.ofMinutes(15)))) { // Kiểm tra trước mốc hết hạn
                throw new IllegalStateException("Đơn hàng đã hết hạn giữ chỗ");
            } else {
                expiresAt = this.T0.plus(Duration.ofMinutes(15)); // Cộng 15 phút vào Instant
            }
        } else {
            throw new IllegalStateException("Không thể thêm mặt hàng khi đơn hàng đã được " + status.toString());
        }
        OrderItem existingItem = findItemByProductId(item.getProduct().getId());
        if (existingItem == null) {
            item.getProduct().reserveStock(this.id, item.getQuantity(), expiresAt, now); // Giữ chỗ trên sản phẩm
            this.items.add(item);
            if (status == OrderStatus.DRAFT) {
                this.status = OrderStatus.CREATED;
                this.T0 = now;
            }
        } else {
            int newTotal = Math.addExact(existingItem.getQuantity(), item.getQuantity()); // Cộng dồn chống tràn số
            existingItem.getProduct().reserveStock(this.id, newTotal, expiresAt, now); // Giữ chỗ trên sản phẩm
            existingItem.updateQuantity(newTotal);
        }
    }

    /**
     * Tính tổng tiền các mặt hàng trước khi áp dụng giảm giá.
     */
    public BigDecimal calculateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : items) {
            total = total.add(item.calculateSubtotal());
        }
        return total;
    }

    /**
     * Tính tổng tiền thanh toán cuối cùng sau khi áp dụng chiến lược giảm giá.
     */
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

    /**
     * Cập nhật số lượng của một sản phẩm đã có trong đơn hàng (chỉ áp dụng khi
     * đơn đang ở trạng thái CREATED và còn trong hạn giữ chỗ 15 phút), đồng bộ
     * cả dòng hàng lẫn phiếu giữ chỗ trên sản phẩm.
     *
     * @param productId   Mã sản phẩm cần thay đổi số lượng
     * @param newQuantity Số lượng mới mong muốn (phải lớn hơn 0)
     * @param now         Thời điểm hiện tại (UTC) để kiểm tra hạn và tồn khả dụng
     */
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
        /**
         * Chặn thao tác khi hết hạn giữ chỗ - từ chối cập nhật khi đơn hàng CREATED đã
         * vượt quá mốc T0 + 15 phút
         */
        if (!now.isBefore(this.T0.plus(Duration.ofMinutes(15)))) {
            throw new IllegalStateException("Đơn hàng đã hết hạn giữ chỗ");
        }
        OrderItem existingItem = findItemByProductId(productId);
        if (existingItem == null) {
            throw new IllegalArgumentException("Không tìm thấy sản phẩm có mã " + productId + " trong đơn hàng");
        }
        /**
         * Cập nhật số lượng mặt hàng (Quantity Update) - điều chỉnh số lượng dòng hàng
         * và số lượng giữ chỗ tương ứng
         */
        existingItem.getProduct().reserveStock(this.id, newQuantity, this.T0.plus(Duration.ofMinutes(15)), now);
        existingItem.updateQuantity(newQuantity);
    }

    /**
     * Xác nhận đơn hàng đang ở trạng thái CREATED (không rỗng và còn trong hạn
     * giữ chỗ 15 phút), chốt trừ tồn kho vật lý, giải phóng phiếu giữ chỗ của
     * từng mặt hàng và chuyển trạng thái sang CONFIRMED.
     *
     * @param now Thời điểm hiện tại (UTC) để kiểm tra hạn giữ chỗ và ghi nhận
     *            confirmedAt
     */
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

    /**
     * Ghi nhận thanh toán thành công cho đơn hàng đang ở trạng thái CONFIRMED
     * trong thời hạn 15 phút tính từ lúc xác nhận (confirmedAt) và chuyển
     * trạng thái sang PAID.
     *
     * @param paidAt Thời điểm thanh toán (UTC) để kiểm tra thời hạn thanh toán
     */
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

    /**
     * Hủy đơn hàng và xử lý tồn kho tương ứng với trạng thái hiện tại
     * (DRAFT: không đổi kho; CREATED: giải phóng giữ chỗ; CONFIRMED: hoàn tồn
     * vật lý về kho), sau đó chuyển sang trạng thái CANCELLED.
     * Từ chối thao tác nếu đơn đã ở trạng thái PAID hoặc CANCELLED.
     */
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
