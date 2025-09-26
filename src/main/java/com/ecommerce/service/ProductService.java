package com.ecommerce.service;

import com.ecommerce.entity.Product;
import com.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public List<Product> findAllOrderByCreatedAtDesc() {
        return productRepository.findAllOrderByCreatedAtDesc();
    }

    public List<Product> findAllInStock() {
        return productRepository.findAllInStock();
    }

    public List<Product> searchProducts(String searchTerm) {
        return productRepository.searchProducts(searchTerm);
    }

    public List<Product> findByNameContaining(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }

    public List<Product> findLowStockProducts(Integer threshold) {
        return productRepository.findLowStockProducts(threshold);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return productRepository.existsById(id);
    }

    public void updateStock(Long productId, Integer newStock) {
        Product product = findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setStockQuantity(newStock);
        productRepository.save(product);
    }

    public void reduceStock(Long productId, Integer quantity) {
        Product product = findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.hasStock(quantity)) {
            throw new RuntimeException("Insufficient stock for product: " + product.getName());
        }

        product.reduceStock(quantity);
        productRepository.save(product);
    }

    public void addStock(Long productId, Integer quantity) {
        Product product = findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.addStock(quantity);
        productRepository.save(product);
    }

    public boolean hasStock(Long productId, Integer quantity) {
        Product product = findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return product.hasStock(quantity);
    }
}