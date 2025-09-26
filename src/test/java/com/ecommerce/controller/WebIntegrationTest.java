package com.ecommerce.controller;

import com.ecommerce.entity.Product;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class WebIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        userRepository.deleteAll();

        testProduct = new Product();
        testProduct.setName("Web Test Product");
        testProduct.setDescription("Product for web integration testing");
        testProduct.setPrice(new BigDecimal("29.99"));
        testProduct.setStockQuantity(25);
        testProduct.setImageUrl("web-test-product.jpg");
        testProduct = productRepository.save(testProduct);
    }

    @Test
    @WithMockUser
    void testHomePageLoads() throws Exception {
        mockMvc.perform(get("/user/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/home"))
                .andExpect(model().attributeExists("products"));
    }

    @Test
    @WithMockUser
    void testProductsPageLoads() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/products"))
                .andExpect(model().attributeExists("products"));
    }

    @Test
    @WithMockUser
    void testProductsPageWithSearch() throws Exception {
        mockMvc.perform(get("/products").param("search", "Web"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/products"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("searchTerm", "Web"));
    }

    @Test
    @WithMockUser
    void testProductDetailPage() throws Exception {
        mockMvc.perform(get("/products/{id}", testProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("user/product-detail"))
                .andExpect(model().attributeExists("product"));
    }

    @Test
    @WithMockUser
    void testProductDetailPageNotFound() throws Exception {
        mockMvc.perform(get("/products/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"));
    }

    @Test
    void testLoginPageLoads() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    void testRegisterPageLoads() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void testUserRegistration() throws Exception {
        mockMvc.perform(post("/register")
                .param("username", "newuser")
                .param("password", "password123")
                .param("email", "newuser@test.com")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void testUserRegistrationWithValidationErrors() throws Exception {
        mockMvc.perform(post("/register")
                .param("username", "u") // Too short
                .param("password", "123") // Too short
                .param("email", "invalid-email") // Invalid format
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"));
    }

    @Test
    void testHomeRedirect() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAdminAccess() throws Exception {
        // This test verifies that admin users can access the system
        // The actual admin pages would need to be tested based on your AdminController
        mockMvc.perform(get("/user/home"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testUserAccess() throws Exception {
        mockMvc.perform(get("/user/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/home"));
    }

    @Test
    void testUnauthenticatedAccessRedirect() throws Exception {
        // Without @WithMockUser, this should redirect to login
        mockMvc.perform(get("/user/home"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser
    void testCsrfProtection() throws Exception {
        // Test that POST requests without CSRF token are rejected
        mockMvc.perform(post("/register")
                .param("username", "testuser")
                .param("password", "password123")
                .param("email", "test@example.com"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void testProductSearchFunctionality() throws Exception {
        // Test empty search
        mockMvc.perform(get("/products").param("search", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("user/products"))
                .andExpect(model().attributeExists("products"));

        // Test search with results
        mockMvc.perform(get("/products").param("search", "Web"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/products"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("searchTerm", "Web"));

        // Test search with no results
        mockMvc.perform(get("/products").param("search", "NonExistentProduct"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/products"))
                .andExpect(model().attributeExists("products"));
    }
}