package com.ecommerce.controller;

import com.ecommerce.entity.Product;
import com.ecommerce.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private Model model;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(new BigDecimal("19.99"));
        testProduct.setStockQuantity(100);
    }

    @Test
    void testHome() throws Exception {
        List<Product> products = Arrays.asList(testProduct);
        when(productService.findAllInStock()).thenReturn(products);

        mockMvc.perform(get("/user/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/home"))
                .andExpect(model().attributeExists("products"));

        verify(productService, times(1)).findAllInStock();
    }

    @Test
    void testHomeDirectCall() {
        List<Product> products = Arrays.asList(testProduct);
        when(productService.findAllInStock()).thenReturn(products);

        String viewName = productController.home(model);

        assertEquals("user/home", viewName);
        verify(productService, times(1)).findAllInStock();
        verify(model, times(1)).addAttribute("products", products);
    }

    @Test
    void testListProductsWithoutSearch() throws Exception {
        List<Product> products = Arrays.asList(testProduct);
        when(productService.findAllInStock()).thenReturn(products);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/products"))
                .andExpect(model().attributeExists("products"));

        verify(productService, times(1)).findAllInStock();
        verify(productService, never()).searchProducts(anyString());
    }

    @Test
    void testListProductsWithSearch() throws Exception {
        String searchTerm = "test";
        List<Product> products = Arrays.asList(testProduct);
        when(productService.searchProducts(searchTerm)).thenReturn(products);

        mockMvc.perform(get("/products").param("search", searchTerm))
                .andExpect(status().isOk())
                .andExpect(view().name("user/products"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("searchTerm", searchTerm));

        verify(productService, times(1)).searchProducts(searchTerm);
        verify(productService, never()).findAllInStock();
    }

    @Test
    void testListProductsWithEmptySearch() throws Exception {
        List<Product> products = Arrays.asList(testProduct);
        when(productService.findAllInStock()).thenReturn(products);

        mockMvc.perform(get("/products").param("search", "   "))
                .andExpect(status().isOk())
                .andExpect(view().name("user/products"))
                .andExpect(model().attributeExists("products"));

        verify(productService, times(1)).findAllInStock();
        verify(productService, never()).searchProducts(anyString());
    }

    @Test
    void testListProductsDirectCallWithSearch() {
        String searchTerm = "test";
        List<Product> products = Arrays.asList(testProduct);
        when(productService.searchProducts(searchTerm.trim())).thenReturn(products);

        String viewName = productController.listProducts(searchTerm, model);

        assertEquals("user/products", viewName);
        verify(productService, times(1)).searchProducts(searchTerm.trim());
        verify(model, times(1)).addAttribute("products", products);
        verify(model, times(1)).addAttribute("searchTerm", searchTerm);
    }

    @Test
    void testListProductsDirectCallWithoutSearch() {
        List<Product> products = Arrays.asList(testProduct);
        when(productService.findAllInStock()).thenReturn(products);

        String viewName = productController.listProducts(null, model);

        assertEquals("user/products", viewName);
        verify(productService, times(1)).findAllInStock();
        verify(model, times(1)).addAttribute("products", products);
        verify(model, never()).addAttribute(eq("searchTerm"), any());
    }

    @Test
    void testViewProduct() throws Exception {
        when(productService.findById(1L)).thenReturn(Optional.of(testProduct));

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/product-detail"))
                .andExpect(model().attributeExists("product"));

        verify(productService, times(1)).findById(1L);
    }

    @Test
    void testViewProductNotFound() throws Exception {
        when(productService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/products/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"));

        verify(productService, times(1)).findById(999L);
    }

    @Test
    void testViewProductDirectCall() {
        when(productService.findById(1L)).thenReturn(Optional.of(testProduct));

        String viewName = productController.viewProduct(1L, model);

        assertEquals("user/product-detail", viewName);
        verify(productService, times(1)).findById(1L);
        verify(model, times(1)).addAttribute("product", testProduct);
    }

    @Test
    void testViewProductDirectCallNotFound() {
        when(productService.findById(999L)).thenReturn(Optional.empty());

        String viewName = productController.viewProduct(999L, model);

        assertEquals("redirect:/products", viewName);
        verify(productService, times(1)).findById(999L);
        verify(model, never()).addAttribute(eq("product"), any());
    }
}