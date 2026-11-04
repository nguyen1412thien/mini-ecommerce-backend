package com.ecommerce;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ecommerce.exception.ProductNotFoundException;
import com.ecommerce.model.product.Product;
import com.ecommerce.repository.ProductRepository;

/**
 * Điểm khởi chạy chính để kiểm thử thủ công hệ thống backend.
 */
public class Main {
    public static void main(String[] args) {

        // Kịch bản 1: Quản lý kho hàng siêu tốc với HashMap (Tốc độ $O(1)$)
        ProductRepository productRepository = new ProductRepository();
        Product banPhimCo = new Product("P01", "Bàn phím cơ", 1500000.0, 100);
        Product chuotKhongDay = new Product("P02", "Chuột không dây", 500000.0, 300);
        Product taiNgheBluetooth = new Product("P03", "Tai nghe Bluetooth", 800000.0, 1000);

        productRepository.save(banPhimCo);
        productRepository.save(taiNgheBluetooth);
        productRepository.save(chuotKhongDay);

        System.out.println("Tổng số sản phẩm hiện có trong kho" + productRepository.count());
        Product productHopLe = productRepository.findById("P01");
        System.out.println("Tên: " + productHopLe.getName() + " | Đơn giá: " + productHopLe.getPrice());

        String idKhongHopLe = "P05";
        if (productRepository.existsById(idKhongHopLe) == true) {
            System.out.println("Sản phẩm với ID " + idKhongHopLe + " có tồn tại trong kho");
        } else {
            System.out.println("Sản phẩm với ID " + idKhongHopLe + " không tồn tại trong kho");
        }

        String idHopLe = "P02";
        if (productRepository.existsById(idHopLe) == true) {
            System.out.println("Sản phẩm với ID " + idHopLe + " có tồn tại trong kho");
        } else {
            System.out.println("Sản phẩm với ID " + idHopLe + " không tồn tại trong kho");
        }

        System.out.println("Số sản phẩm có trong kho hàng: " + productRepository.count());
        String idBiXoa = "P03";
        productRepository.deleteById(idBiXoa);
        System.out.println("Số sản phẩm có trong kho hàng sau khi xoá sản phẩm có ID " + idBiXoa + " : "
                + productRepository.count());

        // Kịch bản 2: Cơ chế phòng vệ ngoại lệ (Exception Handling)
        String idUnknow = "P999";
        try {
            productRepository.getOrThrow(idUnknow);
        } catch (ProductNotFoundException e) {
            System.out.println("Không có sản phẩm nào với ID " + idUnknow);
        }

        // Kịch bản 3: Kiểm chứng Hợp đồng `equals()` & `hashCode()` và Khử trùng trong
        // `HashSet`
        Product p1 = new Product("P10", "Máy bay", 150000000.0, 1);
        Product p2 = new Product("P10", "Máy bay chiến đấu", 250000000.0, 3);
        System.out.println(p1.equals(p2));

        Set<Product> set = new HashSet<>();
        set.add(p1);
        set.add(p2);
        System.out.println(set.size());

        // Kịch bản 4: Kiểm chứng tính năng Sắp xếp linh hoạt bằng Biểu thức Lambda
        Product taiNgheCoDay = new Product("P04", "Tai nghe có dây", 1000000000.0, 1000);
        productRepository.save(taiNgheBluetooth);
        productRepository.save(taiNgheCoDay);
        List<Product> giaTangDan = productRepository.findAllSortedByPriceAsc();
        for (Product product : giaTangDan) {
            System.out.println(product.getName() + " " + product.getPrice());
        }
        System.out.println();
        List<Product> giaGiamDan = productRepository.findAllSortedByPriceDesc();
        for (Product product : giaGiamDan) {
            System.out.println(product.getName() + " " + product.getPrice());
        }
        System.out.println();
        List<Product> sapXepTen = productRepository.findAllSortedByName();
        for (Product product : sapXepTen) {
            System.out.println(product.getName());
        }
        System.out.println();
        List<Product> list = productRepository.findAll();
        for (Product product : list) {
            System.out.println(product.getName() + " " + product.getPrice());
        }

    }
}
