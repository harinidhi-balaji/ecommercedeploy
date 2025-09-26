package com.ecommerce.service;

import com.ecommerce.entity.Product;
import com.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(new BigDecimal("19.99"));
        testProduct.setStockQuantity(100);
    }

    @Test
    void testCreateProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        Product result = productService.createProduct(testProduct);

        assertNotNull(result);
        assertEquals(testProduct.getId(), result.getId());
        assertEquals(testProduct.getName(), result.getName());
        verify(productRepository, times(1)).save(testProduct);
    }

    @Test
    void testUpdateProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        Product result = productService.updateProduct(testProduct);

        assertNotNull(result);
        assertEquals(testProduct.getId(), result.getId());
        verify(productRepository, times(1)).save(testProduct);
    }

    @Test
    void testFindById() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        Optional<Product> result = productService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(testProduct.getId(), result.get().getId());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testFindByIdNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Product> result = productService.findById(999L);

        assertFalse(result.isPresent());
        verify(productRepository, times(1)).findById(999L);
    }

    @Test
    void testFindAll() {
        List<Product> products = Arrays.asList(testProduct, new Product());
        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = productService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testFindAllOrderByCreatedAtDesc() {
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findAllOrderByCreatedAtDesc()).thenReturn(products);

        List<Product> result = productService.findAllOrderByCreatedAtDesc();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository, times(1)).findAllOrderByCreatedAtDesc();
    }

    @Test
    void testFindAllInStock() {
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findAllInStock()).thenReturn(products);

        List<Product> result = productService.findAllInStock();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository, times(1)).findAllInStock();
    }

    @Test
    void testSearchProducts() {
        String searchTerm = "test";
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.searchProducts(searchTerm)).thenReturn(products);

        List<Product> result = productService.searchProducts(searchTerm);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository, times(1)).searchProducts(searchTerm);
    }

    @Test
    void testFindByNameContaining() {
        String name = "Test";
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findByNameContainingIgnoreCase(name)).thenReturn(products);

        List<Product> result = productService.findByNameContaining(name);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository, times(1)).findByNameContainingIgnoreCase(name);
    }

    @Test
    void testFindByPriceBetween() {
        BigDecimal minPrice = new BigDecimal("10.00");
        BigDecimal maxPrice = new BigDecimal("30.00");
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findByPriceBetween(minPrice, maxPrice)).thenReturn(products);

        List<Product> result = productService.findByPriceBetween(minPrice, maxPrice);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository, times(1)).findByPriceBetween(minPrice, maxPrice);
    }

    @Test
    void testFindLowStockProducts() {
        Integer threshold = 10;
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findLowStockProducts(threshold)).thenReturn(products);

        List<Product> result = productService.findLowStockProducts(threshold);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository, times(1)).findLowStockProducts(threshold);
    }

    @Test
    void testDeleteProduct() {
        doNothing().when(productRepository).deleteById(1L);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void testExistsById() {
        when(productRepository.existsById(1L)).thenReturn(true);

        boolean result = productService.existsById(1L);

        assertTrue(result);
        verify(productRepository, times(1)).existsById(1L);
    }

    @Test
    void testUpdateStock() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        productService.updateStock(1L, 150);

        assertEquals(150, testProduct.getStockQuantity());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(testProduct);
    }

    @Test
    void testUpdateStockProductNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> productService.updateStock(999L, 150));

        assertEquals("Product not found", exception.getMessage());
        verify(productRepository, times(1)).findById(999L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testReduceStock() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        productService.reduceStock(1L, 30);

        assertEquals(70, testProduct.getStockQuantity());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(testProduct);
    }

    @Test
    void testReduceStockInsufficientStock() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> productService.reduceStock(1L, 150));

        assertTrue(exception.getMessage().contains("Insufficient stock"));
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testReduceStockProductNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> productService.reduceStock(999L, 30));

        assertEquals("Product not found", exception.getMessage());
        verify(productRepository, times(1)).findById(999L);
    }
}