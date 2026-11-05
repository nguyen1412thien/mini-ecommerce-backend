package com.ecommerce.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ecommerce.exception.ProductNotFoundException;
import com.ecommerce.model.product.Product;

public class ProductRepository {
    private Map<String, Product> products = new HashMap<>();

    public ProductRepository() {

    }

    // luu Product
    public void save(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product khong duoc de trong");
        }
        products.put(product.getId(), product);
    }

    // tim Product theo id
    public Product findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã sản phẩm không được để trống");
        }
        return products.get(id);
    }

    // kiem tra san pham co id = .. da ton tai chua
    public boolean existsById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã sản phẩm không được để trống");
        }
        return products.containsKey(id);
    }

    // lay toan bo san pham trong kho
    public List<Product> findAll() {
        List<Product> list = new ArrayList<>(products.values());
        return list;
    }

    // xoa san pham trong kho
    public boolean deleteById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã sản phẩm không được để trống");
        }
        Product rm = this.products.remove(id);
        if (rm == null) {
            return false;
        } else {
            return true;
        }
    }

    // dem tong so luong san pham hien co trong kho
    public int count() {
        return this.products.size();
    }

    // lay san pham phai chac chan ton tai, khong thi nem ngoai le
    public Product getOrThrow(String id) {
        Product product = this.findById(id);
        if (product == null) {
            throw new ProductNotFoundException("Khong tim thay san pham co ID " + id);
        } else {

            return product;

        }
    }

    // tra ve danh sach sap xep theo gia tang dan (thap -> cao)
    public List<Product> findAllSortedByPriceAsc() {
        List<Product> list = this.findAll();
        list.sort((p1, p2) -> Double.compare(p1.getPrice(), p2.getPrice()));
        return list;
    }

    // tra ve danh sach sap xep theo gia giam dan (cao -> thap)
    public List<Product> findAllSortedByPriceDesc() {
        List<Product> list = this.findAll();
        list.sort((p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice()));
        return list;
    }

    // Trả về danh sách sản phẩm được sắp xếp theo **tên chữ cái từ A đến Z** (không
    // phân biệt chữ hoa, chữ thường)
    public List<Product> findAllSortedByName() {
        List<Product> list = this.findAll();
        list.sort((p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));
        return list;
    }

    // Tìm và trả về danh sách các sản phẩm có đơn giá nằm trong đoạn từ `minPrice`
    // đến `maxPrice`
    public List<Product> findByPriceRange(double minPrice, double maxPrice) {
        if (minPrice < 0 || maxPrice < 0) {
            throw new IllegalArgumentException("Giá tìm kiếm không được âm");
        }
        if (minPrice > maxPrice) {
            throw new IllegalArgumentException("Giá tối thiểu không được lớn hơn giá tối đa");
        }
        return this.findAll().stream()
                .filter(p -> p.getPrice() >= minPrice && p.getPrice() <= maxPrice)
                .sorted((p1,p2) -> Double.compare(p1.getPrice(), p2.getPrice()))
                .toList();
    }

    // Lấy ra danh sách toàn bộ Tên của tất cả các sản phẩm hiện có trong kho hàng.
    public List<String> findAllProductName() {
        return this.findAll().stream()
                .map(p -> p.getName())
                .sorted()
                .toList();
    }

    // Lọc và trả về danh sách các sản phẩm **Đang còn hàng** trong kho
    public List<Product> findInStockProducts() {
        return this.findAll().stream()
                .filter(p -> p.getStockQuantity() > 0)
                .toList();
    }

    // Lấy ra danh sách Top `limit` sản phẩm có đơn giá cao nhất trong kho (sắp xếp
    // giá từ cao xuống thấp và chỉ lấy đúng số lượng phần tử theo `limit`).
    public List<Product> findTopExpensiveProducts(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Số lượng giới hạn phải lớn hơn 0");
        }
        return this.findAll().stream()
                .sorted((p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice()))
                .limit(limit)
                .toList();
    }

}
