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
        if (products.containsKey(id)) {
            return products.get(id);
        } else {
            return null;
        }
    }

    // kiem tra san pham co id = .. da ton tai chua
    public boolean existsById(String id) {
        return products.containsKey(id);
    }

    // lay toan bo san pham trong kho
    public List<Product> findAll() {
        List<Product> list = new ArrayList<>(products.values());
        return list;
    }

    // xoa san pham trong kho
    public boolean deleteById(String id) {
        if (this.products.containsKey(id)) {
            this.products.remove(id);
            return true;
        } else {
            return false;
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
        list.sort((p1,p2)-> p1.getName().compareToIgnoreCase(p2.getName()));
        return list;
    }
}
