package com.ecommerce;

import com.ecommerce.model.discount.FixedAmountDiscount;
import com.ecommerce.model.discount.PercentageDiscount;
import com.ecommerce.model.order.Order;
import com.ecommerce.model.order.OrderItem;
import com.ecommerce.model.product.Product;

/**
 * Điểm khởi chạy chính để kiểm thử thủ công hệ thống backend.
 */
public class Main {
    public static void main(String[] args) {
        Product p1 = new Product("P01", "Laptop", 20000000.0, 10);
        Product p2 = new Product("P02", "Chuot khong day", 500000.0, 50);

        String customer = "Nguyen Thanh Thien";
        Order order1 = new Order("O01", customer);
        OrderItem item1 = new OrderItem(p1, 1);
        OrderItem item2 = new OrderItem(p2, 2);
        order1.addItem(item1);
        order1.addItem(item2);
        System.out.println("Gia goc cua don hang khach da dat: " + order1.calculateTotal());
        p2.updatePrice(600000.0);

        System.out.println("Kiem tra lai don hang khach da mua truoc khi tang gia: " + order1.calculateTotal());

        try {
            order1.getItems().clear();
        } catch (UnsupportedOperationException e) {
            System.out.println("He thong da chang dung hanh vi pha hoai gio hang thanh cong");
        }

        PercentageDiscount d1 = new PercentageDiscount(10.0);
        FixedAmountDiscount d2 = new FixedAmountDiscount(200000.0);

        System.out.println("So tien thanh toan khi ap dung voucher giam 10%: " + order1.calculateFinalTotal(d1));
        System.out.println("So tien thanh toan khi ap dung voucher giam 200.000d: " + order1.calculateFinalTotal(d2));
        System.out.println("So tien thanh toan khi khong dung voucher: " + order1.calculateFinalTotal(null));

        p1.deductStock(1);
        p2.deductStock(2);
        System.out.println("So luong con lai cua Laptop: " + p1.getStockQuantity());
        System.out.println("So luong con lai cua Chuot khong day: " + p2.getStockQuantity());
    }
}
