package com.ecommerce;

import java.math.BigDecimal;

import com.ecommerce.model.discount.FixedAmountDiscount;
import com.ecommerce.model.order.Order;
import com.ecommerce.model.order.OrderItem;
import com.ecommerce.model.product.Product;

/**
 * Điểm khởi chạy chính để kiểm thử thủ công hệ thống backend.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println(1.0 - 0.9);
        System.out.println(0.1 + 0.2 == 0.3);
        System.out.println(BigDecimal.valueOf(1.0).subtract(BigDecimal.valueOf(0.9)));
        BigDecimal valueOf = new BigDecimal("2.0");
        BigDecimal valueOf2 = new BigDecimal("2.00");
        System.out.println(valueOf.equals(valueOf2));
        System.out.println(valueOf.compareTo(valueOf2));

        Product p = new Product("P01", "San pham 1", 1000.0, 5);
        try {
            new OrderItem(p, 10);

        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }

        Order o1 = new Order("O01", "Nguyễn Thanh Thiên");
        Product p1 = new Product("P01", "Sản phẩm 1", 100000.0, 1);
        Product p2 = new Product("P02", "Sản phẩm 2", 100000.0, 1);
        OrderItem item1 = new OrderItem(p1, 1);
        OrderItem item2 = new OrderItem(p2, 1);
        o1.addItem(item1);
        o1.addItem(item2);
        FixedAmountDiscount discount = new FixedAmountDiscount(500000.0);
        Double total = o1.calculateFinalTotal(discount);
        System.out.println(total);


    }
}
