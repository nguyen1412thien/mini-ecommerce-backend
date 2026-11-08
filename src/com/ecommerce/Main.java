package com.ecommerce;

import java.math.BigDecimal;

import com.ecommerce.model.discount.FixedAmountDiscount;
import com.ecommerce.model.discount.PercentageDiscount;
import com.ecommerce.model.order.Order;
import com.ecommerce.model.order.OrderItem;
import com.ecommerce.model.product.Product;
import com.ecommerce.repository.ProductRepository;

/**
 * Điểm khởi chạy chính để kiểm thử thủ công hệ thống backend.
 */
public class Main {
    public static void main(String[] args) {
        ProductRepository products = new ProductRepository();
        Product p1 = new Product("P01", "Sản phẩm 1", BigDecimal.valueOf(1000), 3);
        Product p2 = new Product("P02", "Sản phẩm 2", BigDecimal.valueOf(2000), 3);
        Product p3 = new Product("P03", "Sản phẩm 3", BigDecimal.valueOf(4000), 0);
        Product p4 = new Product("P04", "Sản phẩm 4", BigDecimal.valueOf(3000), 3);
        products.save(p1);
        products.save(p2);
        products.save(p3);
        products.save(p4);

        System.out.println(products.calculateTotalInventoryValue());
        System.out.println();
        System.out.println(products.calculateAveragePrice());
        System.out.println();
        System.out.println(products.groupProductsByStockStatus());
        System.out.println();
        System.out.println(products.getAllProductNamesJoined(", "));

        Order order = new Order("O01", "Khách hàng mẫu");
        OrderItem itemA = new OrderItem(p1, 2);
        OrderItem itemB = new OrderItem(p2, 1);
        order.addItem(itemA);
        order.addItem(itemB);

        System.out.println();
        System.out.println("Thành tiền A: " + itemA.calculateSubtotal());
        System.out.println("Tổng đơn: " + order.calculateTotal());
        System.out.println("Giảm 10%: " + new PercentageDiscount(10).applyDiscount(order.calculateTotal()));
        System.out.println("Sau giảm 10%: " + order.calculateFinalTotal(new PercentageDiscount(10)));
        System.out.println("Sau voucher 500đ: " + order.calculateFinalTotal(new FixedAmountDiscount(BigDecimal.valueOf(500))));

        Product roundingProduct = new Product("P05", "Sản phẩm làm tròn", BigDecimal.valueOf(10001), 1);
        Order roundingOrder = new Order("O02", "Khách hàng mẫu");
        roundingOrder.addItem(new OrderItem(roundingProduct, 1));
        System.out.println("Giảm 10% trên 10.001đ: "
                + new PercentageDiscount(10).applyDiscount(roundingOrder.calculateTotal()));
        System.out.println("Sau giảm 10% trên 10.001đ: "
                + roundingOrder.calculateFinalTotal(new PercentageDiscount(10)));

        ProductRepository emptyProducts = new ProductRepository();
        System.out.println("Giá trung bình kho rỗng: " + emptyProducts.calculateAveragePrice());

        ProductRepository roundingProducts = new ProductRepository();
        roundingProducts.save(new Product("P06", "Sản phẩm 6", BigDecimal.valueOf(1), 1));
        roundingProducts.save(new Product("P07", "Sản phẩm 7", BigDecimal.valueOf(2), 1));
        roundingProducts.save(new Product("P08", "Sản phẩm 8", BigDecimal.valueOf(2), 1));
        System.out.println("Giá trung bình 5đ / 3 sản phẩm, làm tròn HALF_UP: "
                + roundingProducts.calculateAveragePrice());

    }
}
