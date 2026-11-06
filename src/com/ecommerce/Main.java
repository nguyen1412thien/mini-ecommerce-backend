package com.ecommerce;

import com.ecommerce.model.product.Product;
import com.ecommerce.repository.ProductRepository;

/**
 * Điểm khởi chạy chính để kiểm thử thủ công hệ thống backend.
 */
public class Main {
    public static void main(String[] args) {
        ProductRepository products = new ProductRepository();
        Product p1 = new Product("P01", "Sản phẩm 1", 1000.0, 3);
        Product p2 = new Product("P02", "Sản phẩm 2", 2000.0, 3);
        Product p3 = new Product("P03", "Sản phẩm 3", 4000.0, 0);
        Product p4 = new Product("P04", "Sản phẩm 4", 3000.0, 3);
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

    }
}
