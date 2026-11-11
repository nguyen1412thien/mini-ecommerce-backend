package com.ecommerce.repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ecommerce.exception.ProductNotFoundException;
import com.ecommerce.model.product.Product;

/**
 * Kho lưu trữ và quản lý dữ liệu sản phẩm trong bộ nhớ (In-Memory Repository).
 */
public class ProductRepository {
    /** Bản đồ lưu trữ sản phẩm theo mã định danh O(1) */
    private Map<String, Product> products = new HashMap<>();

    public ProductRepository() {

    }

    /**
     * Lưu hoặc cập nhật thông tin sản phẩm vào kho.
     */
    public void save(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Sản phẩm không được để trống");
        }
        products.put(product.getId(), product);
    }

    /**
     * Tìm kiếm sản phẩm theo mã định danh.
     */
    public Product findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã sản phẩm không được để trống");
        }
        return products.get(id);
    }

    /**
     * Kiểm tra sản phẩm có tồn tại trong kho theo mã định danh hay không.
     */
    public boolean existsById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã sản phẩm không được để trống");
        }
        return products.containsKey(id);
    }

    /**
     * Lấy toàn bộ danh sách sản phẩm hiện có trong kho.
     */
    public List<Product> findAll() {
        List<Product> list = new ArrayList<>(products.values());
        return list;
    }

    /**
     * Xóa sản phẩm khỏi kho theo mã định danh.
     */
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

    /**
     * Đếm tổng số lượng sản phẩm hiện có trong kho.
     */
    public int count() {
        return this.products.size();
    }

    /**
     * Lấy sản phẩm theo mã định danh, ném ngoại lệ nếu không tìm thấy.
     */
    public Product getOrThrow(String id) {
        Product product = this.findById(id);
        if (product == null) {
            throw new ProductNotFoundException("Không tìm thấy sản phẩm có ID " + id);
        } else {

            return product;

        }
    }

    /**
     * Trả về danh sách sản phẩm được sắp xếp theo giá tăng dần (thấp đến cao).
     */
    public List<Product> findAllSortedByPriceAsc() {
        List<Product> list = this.findAll();
        list.sort((p1, p2) -> p1.getPrice().compareTo(p2.getPrice()));
        return list;
    }

    /**
     * Trả về danh sách sản phẩm được sắp xếp theo giá giảm dần (cao đến thấp).
     */
    public List<Product> findAllSortedByPriceDesc() {
        List<Product> list = this.findAll();
        list.sort((p1, p2) -> p2.getPrice().compareTo(p1.getPrice()));
        return list;
    }

    /**
     * Trả về danh sách sản phẩm được sắp xếp theo tên từ A đến Z (không phân biệt
     * hoa thường).
     */
    public List<Product> findAllSortedByName() {
        List<Product> list = this.findAll();
        list.sort((p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));
        return list;
    }

    /**
     * Tìm và trả về danh sách các sản phẩm có đơn giá nằm trong khoảng từ minPrice
     * đến maxPrice.
     */
    public List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice == null || maxPrice == null) {
            throw new IllegalArgumentException("Giá tìm kiếm không được để trống");
        }
        if (minPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Giá tìm kiếm " + minPrice + " không hợp lệ, không được âm");
        }
        if (maxPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Giá tìm kiếm " + maxPrice + " không hợp lệ, không được âm");
        }
        if (minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException(
                    "Giá tối thiểu " + minPrice + " không được lớn hơn giá tối đa " + maxPrice);
        }
        return this.findAll().stream()
                .filter(p -> p.getPrice().compareTo(minPrice) >= 0 && p.getPrice().compareTo(maxPrice) <= 0)
                .sorted((p1, p2) -> p1.getPrice().compareTo(p2.getPrice()))
                .toList();
    }

    /**
     * Lấy danh sách tên của tất cả các sản phẩm hiện có trong kho hàng.
     */
    public List<String> findAllProductName() {
        return this.findAll().stream()
                .map(p -> p.getName())
                .sorted()
                .toList();
    }

    /**
     * Lọc và trả về danh sách các sản phẩm đang còn hàng trong kho (tồn kho > 0).
     */
    public List<Product> findInStockProducts() {
        return this.findAll().stream()
                .filter(p -> p.getStockQuantity() > 0)
                .toList();
    }

    /**
     * Lấy danh sách top N sản phẩm có đơn giá cao nhất trong kho.
     */
    public List<Product> findTopExpensiveProducts(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Số lượng giới hạn " + limit + " không hợp lệ, phải lớn hơn 0");
        }
        return this.findAll().stream()
                .sorted((p1, p2) -> p2.getPrice().compareTo(p1.getPrice()))
                .limit(limit)
                .toList();
    }

    /**
     * Tính tổng giá trị tiền tệ của toàn bộ hàng hóa đang tồn kho.
     */
    public BigDecimal calculateTotalInventoryValue() {
        return this.findAll().stream()
                .map(p -> p.getPrice().multiply(BigDecimal.valueOf(p.getStockQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Tính giá bán trung bình của các sản phẩm hiện có trong kho hàng.
     */
    public BigDecimal calculateAveragePrice() {
        if (this.count() == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal totalUnitPrice = BigDecimal.ZERO;
        totalUnitPrice = totalUnitPrice
                .add(this.findAll().stream().map(p -> p.getPrice()).reduce(BigDecimal.ZERO, BigDecimal::add));
        return totalUnitPrice.divide(BigDecimal.valueOf(this.count()), 0, RoundingMode.HALF_UP);

    }

    /**
     * Phân loại toàn bộ sản phẩm trong kho thành 2 nhóm dựa trên trạng thái tồn kho
     * (IN_STOCK / OUT_OF_STOCK).
     */
    public Map<String, List<Product>> groupProductsByStockStatus() {
        return this.findAll().stream()
                .collect(Collectors.groupingBy(p -> p.getStockQuantity() > 0 ? "IN_STOCK" : "OUT_OF_STOCK"));
    }

    /**
     * Nối toàn bộ tên của các sản phẩm hiện có trong kho thành một chuỗi văn bản
     * duy nhất.
     */
    public String getAllProductNamesJoined(String delimiter) {
        if (delimiter == null) {
            throw new IllegalArgumentException("Ký tự phân cách không được để trống");
        }
        return this.findAll().stream()
                .map(p -> p.getName())
                .collect(Collectors.joining(delimiter));
    }
}
