package com.ecommerce.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(new BigDecimal("19.99"));
        product.setStockQuantity(100);
        product.setImageUrl("http://example.com/image.jpg");
    }

    @Test
    void testProductCreation() {
        assertNotNull(product);
        assertEquals(1L, product.getId());
        assertEquals("Test Product", product.getName());
        assertEquals("Test Description", product.getDescription());
        assertEquals(new BigDecimal("19.99"), product.getPrice());
        assertEquals(100, product.getStockQuantity());
        assertEquals("http://example.com/image.jpg", product.getImageUrl());
    }

    @Test
    void testIsInStock() {
        assertTrue(product.isInStock());

        product.setStockQuantity(0);
        assertFalse(product.isInStock());

        product.setStockQuantity(-1);
        assertFalse(product.isInStock());
    }

    @Test
    void testHasStock() {
        assertTrue(product.hasStock(50));
        assertTrue(product.hasStock(100));
        assertFalse(product.hasStock(101));
        assertFalse(product.hasStock(200));
    }

    @Test
    void testReduceStock() {
        product.reduceStock(30);
        assertEquals(70, product.getStockQuantity());

        product.reduceStock(70);
        assertEquals(0, product.getStockQuantity());
    }

    @Test
    void testReduceStockThrowsExceptionWhenInsufficientStock() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> product.reduceStock(101));
        assertEquals("Insufficient stock", exception.getMessage());
        assertEquals(100, product.getStockQuantity()); // Stock should remain unchanged
    }

    @Test
    void testAddStock() {
        product.addStock(50);
        assertEquals(150, product.getStockQuantity());

        product.addStock(0);
        assertEquals(150, product.getStockQuantity());
    }

    @Test
    void testPrePersistSetsCreatedAt() {
        LocalDateTime beforeCreation = LocalDateTime.now();
        product.onCreate();
        LocalDateTime afterCreation = LocalDateTime.now();

        assertNotNull(product.getCreatedAt());
        assertTrue(product.getCreatedAt().isAfter(beforeCreation) || product.getCreatedAt().isEqual(beforeCreation));
        assertTrue(product.getCreatedAt().isBefore(afterCreation) || product.getCreatedAt().isEqual(afterCreation));
    }

    @Test
    void testStockQuantityBoundaryConditions() {
        product.setStockQuantity(1);
        assertTrue(product.isInStock());
        assertTrue(product.hasStock(1));
        assertFalse(product.hasStock(2));

        product.reduceStock(1);
        assertEquals(0, product.getStockQuantity());
        assertFalse(product.isInStock());
    }
}