package com.ecommerce;

import com.ecommerce.model.product.Product;
import com.ecommerce.repository.ProductRepository;

/**
 * Điểm khởi chạy chính để kiểm thử thủ công hệ thống backend.
 */
public class Main {
    public static void main(String[] args) {
        Product p1 = new Product("P01", "Sản phẩm 1", 200000.0, 1000);
        Product p2 = new Product("P02", "Sản phẩm 2", 500000.0, 99214);
        Product p3 = new Product("P03", "Sản phẩm 3", 1500000.0, 43546);
        Product p4 = new Product("P04", "Sản phẩm 4", 15000000.0, 0);
        Product p5 = new Product("P05", "Sản phẩm 5", 30000000.0, 12432);
        Product p6 = new Product("P06", "Sản phẩm 6", 40000000.0, 55464);

        ProductRepository products = new ProductRepository();
        products.save(p1);
        products.save(p2);
        products.save(p3);
        products.save(p4);
        products.save(p5);
        products.save(p6);

        System.out.println(products.findByPriceRange(500000.0, 2000000.0));
        System.out.println();
        System.out.println(products.findAllProductName());
        System.out.println();
        System.out.println(products.findInStockProducts());
        System.out.println();
        System.out.println(products.findTopExpensiveProducts(2));
    }
}
