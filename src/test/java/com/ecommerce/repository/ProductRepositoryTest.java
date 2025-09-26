package com.ecommerce.repository;

import com.ecommerce.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    private Product product1;
    private Product product2;
    private Product product3;

    @BeforeEach
    void setUp() {
        // Clear all existing data
        productRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();

        // Create test products
        product1 = new Product();
        product1.setName("Laptop");
        product1.setDescription("Gaming laptop with high performance");
        product1.setPrice(new BigDecimal("999.99"));
        product1.setStockQuantity(10);
        product1.setImageUrl("laptop.jpg");

        product2 = new Product();
        product2.setName("Mouse");
        product2.setDescription("Wireless gaming mouse");
        product2.setPrice(new BigDecimal("49.99"));
        product2.setStockQuantity(0); // Out of stock
        product2.setImageUrl("mouse.jpg");

        product3 = new Product();
        product3.setName("Keyboard");
        product3.setDescription("Mechanical keyboard");
        product3.setPrice(new BigDecimal("79.99"));
        product3.setStockQuantity(5);
        product3.setImageUrl("keyboard.jpg");

        // Persist the entities
        product1 = entityManager.persistAndFlush(product1);
        product2 = entityManager.persistAndFlush(product2);
        product3 = entityManager.persistAndFlush(product3);
    }

    @Test
    void testFindByNameContainingIgnoreCase() {
        List<Product> result = productRepository.findByNameContainingIgnoreCase("LAP");

        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).getName());
    }

    @Test
    void testFindByNameContainingIgnoreCaseNoMatch() {
        List<Product> result = productRepository.findByNameContainingIgnoreCase("Phone");

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindAllInStock() {
        List<Product> result = productRepository.findAllInStock();

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(p -> p.getStockQuantity() > 0));
        assertTrue(result.stream().noneMatch(p -> p.getName().equals("Mouse")));
    }

    @Test
    void testFindByPriceBetween() {
        BigDecimal minPrice = new BigDecimal("40.00");
        BigDecimal maxPrice = new BigDecimal("80.00");

        List<Product> result = productRepository.findByPriceBetween(minPrice, maxPrice);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(p -> p.getName().equals("Mouse")));
        assertTrue(result.stream().anyMatch(p -> p.getName().equals("Keyboard")));
    }

    @Test
    void testFindLowStockProducts() {
        List<Product> result = productRepository.findLowStockProducts(5);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(p -> p.getName().equals("Mouse")));
        assertTrue(result.stream().anyMatch(p -> p.getName().equals("Keyboard")));
    }

    @Test
    void testFindAllOrderByCreatedAtDesc() {
        List<Product> result = productRepository.findAllOrderByCreatedAtDesc();

        assertEquals(3, result.size());
        // Since we created them in sequence, the last one should be first
        assertEquals("Keyboard", result.get(0).getName());
    }

    @Test
    void testSearchProducts() {
        List<Product> result = productRepository.searchProducts("gaming");

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(p -> p.getName().equals("Laptop")));
        assertTrue(result.stream().anyMatch(p -> p.getName().equals("Mouse")));
    }

    @Test
    void testSearchProductsByName() {
        List<Product> result = productRepository.searchProducts("mouse");

        assertEquals(1, result.size());
        assertEquals("Mouse", result.get(0).getName());
    }

    @Test
    void testSearchProductsNoMatch() {
        List<Product> result = productRepository.searchProducts("tablet");

        assertTrue(result.isEmpty());
    }

    @Test
    void testBasicCrudOperations() {
        // Test save
        Product newProduct = new Product();
        newProduct.setName("Monitor");
        newProduct.setDescription("4K Monitor");
        newProduct.setPrice(new BigDecimal("299.99"));
        newProduct.setStockQuantity(15);

        Product saved = productRepository.save(newProduct);
        assertNotNull(saved.getId());
        assertEquals("Monitor", saved.getName());

        // Test findById
        Optional<Product> found = productRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Monitor", found.get().getName());

        // Test update
        found.get().setStockQuantity(20);
        Product updated = productRepository.save(found.get());
        assertEquals(20, updated.getStockQuantity());

        // Test delete
        productRepository.deleteById(saved.getId());
        Optional<Product> deleted = productRepository.findById(saved.getId());
        assertFalse(deleted.isPresent());
    }

    @Test
    void testExistsById() {
        assertTrue(productRepository.existsById(product1.getId()));
        assertFalse(productRepository.existsById(999L));
    }

    @Test
    void testCount() {
        long count = productRepository.count();
        assertEquals(3, count);
    }

    @Test
    void testFindAll() {
        List<Product> all = productRepository.findAll();
        assertEquals(3, all.size());
    }
}