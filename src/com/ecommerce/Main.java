package com.ecommerce;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

import com.ecommerce.model.order.Order;
import com.ecommerce.model.order.OrderItem;
import com.ecommerce.model.product.Product;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== BẮT ĐẦU KIỂM THỬ KỊCH BẢN ĐẶT HÀNG ===");

        // 0. Chuẩn bị Sản phẩm và Mốc thời gian T0 (UTC)
        Product p1 = new Product("P01", "Áo thun", BigDecimal.valueOf(100_000), 10);
        Product p2 = new Product("P02", "Quần jean", BigDecimal.valueOf(200_000), 5);
        Instant t0 = Instant.parse("2026-11-11T09:00:00Z");

        // 🧪 Ca 1: Tạo đơn nháp rỗng (DRAFT)
        System.out.println("\n--- Ca 1: Tạo đơn nháp rỗng ---");
        Order order = new Order("ORD-001", "Nguyễn Văn A");
        System.out.println("Trạng thái: " + order.getStatus() + " (Kỳ vọng: DRAFT)");
        System.out.println("Tổng tiền: " + order.calculateTotal() + " (Kỳ vọng: 0)");
        System.out.println("Mốc T0: " + order.getT0() + " (Kỳ vọng: null)");

        // 🧪 Ca 2: Thêm dòng đầu tiên (Kích hoạt giữ chỗ & Chuyển sang CREATED)
        System.out.println("\n--- Ca 2: Thêm sản phẩm đầu tiên ---");
        order.addItem(new OrderItem(p1, 3), t0);
        System.out.println("Trạng thái: " + order.getStatus() + " (Kỳ vọng: CREATED)");
        System.out.println("Mốc T0: " + order.getT0() + " (Kỳ vọng: 2026-11-11T09:00:00Z)");
        System.out.println("Tồn vật lý p1: " + p1.getStockQuantity() + " (Kỳ vọng: 10)");
        System.out.println("Tồn khả dụng p1: " + p1.getAvailableStock(t0) + " (Kỳ vọng: 7)");

        // 🧪 Ca 3: Gộp dòng trước hạn (T0 + 14 phút) — Không tạo dòng trùng
        System.out.println("\n--- Ca 3: Thêm trùng sản phẩm trước hạn (T0 + 14m) ---");
        Instant t14 = t0.plus(Duration.ofMinutes(14));
        order.addItem(new OrderItem(p1, 2), t14);
        System.out.println("Số dòng hàng: " + order.getItems().size() + " (Kỳ vọng: 1)");
        System.out.println("Số lượng p1 trong đơn: " + order.getItems().get(0).getQuantity() + " (Kỳ vọng: 5)");
        System.out.println("Tồn khả dụng p1: " + p1.getAvailableStock(t14) + " (Kỳ vọng: 5)");

        // 🧪 Ca 4: Thêm sản phẩm thứ hai trước hạn (T0 + 8 phút)
        System.out.println("\n--- Ca 4: Thêm sản phẩm thứ hai trước hạn (T0 + 8m) ---");
        Instant t8 = t0.plus(Duration.ofMinutes(8));
        order.addItem(new OrderItem(p2, 2), t8);
        System.out.println("Tồn khả dụng p2: " + p2.getAvailableStock(t8) + " (Kỳ vọng: 3)");
        System.out.println("Tổng tiền: " + order.calculateTotal() + " (Kỳ vọng: 900000)");

        // 🧪 Ca 5: Bắt ca biên từ chối thao tác sau hạn (T0 + 15 phút)
        System.out.println("\n--- Ca 5: Thử thêm hàng sau hạn (T0 + 15m) ---");
        Instant t15 = t0.plus(Duration.ofMinutes(15));
        try {
            order.addItem(new OrderItem(p1, 1), t15);
            System.out.println("❌ LỖI: Lẽ ra phải chặn khi quá hạn!");
        } catch (IllegalStateException e) {
            System.out.println("✅ Chặn thành công sau hạn: " + e.getMessage());
        }

        // 🧪 Ca 6: Xác nhận đơn (confirm) trước hạn — Thao tác trừ kho nguyên tử
        System.out.println("\n--- Ca 6: Xác nhận đơn ---");
        Instant t10 = t0.plus(Duration.ofMinutes(10));
        order.confirm(t10);
        System.out.println("Trạng thái: " + order.getStatus() + " (Kỳ vọng: CONFIRMED)");
        System.out.println("Tồn vật lý p1: " + p1.getStockQuantity() + " (Kỳ vọng: 5)");
        System.out.println("Tồn vật lý p2: " + p2.getStockQuantity() + " (Kỳ vọng: 3)");
        System.out.println("Tồn khả dụng p1: " + p1.getAvailableStock(t10) + " (Kỳ vọng: 5)");

        // 🧪 Ca 7: Thanh toán thành công (markAsPaid) & Chặn hủy đơn sau thanh toán
        System.out.println("\n--- Ca 7: Thanh toán và chặn hủy ---");
        Instant t20 = t10.plus(Duration.ofMinutes(10));
        order.markAsPaid(t20);
        System.out.println("Trạng thái: " + order.getStatus() + " (Kỳ vọng: PAID)");
        try {
            order.cancel();
            System.out.println("❌ LỖI: Không được phép hủy đơn đã thanh toán!");
        } catch (IllegalStateException e) {
            System.out.println("✅ Chặn hủy đơn PAID thành công: " + e.getMessage());
        }

        System.out.println("\n=== HOÀN TẤT KIỂM THỬ ===");
    }
}
