package com.ecommerce.service;

import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ServiceIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Product testProduct;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Clean up repositories
        productRepository.deleteAll();
        userRepository.deleteAll();

        // Create test product
        testProduct = new Product();
        testProduct.setName("Integration Test Product");
        testProduct.setDescription("Product for integration testing");
        testProduct.setPrice(new BigDecimal("99.99"));
        testProduct.setStockQuantity(50);
        testProduct.setImageUrl("test-product.jpg");

        // Create test user
        testUser = new User();
        testUser.setUsername("integrationuser");
        testUser.setPassword("password123");
        testUser.setEmail("integration@test.com");
    }

    @Test
    void testProductServiceIntegration() {
        // Test create product
        Product created = productService.createProduct(testProduct);
        assertNotNull(created.getId());
        assertEquals("Integration Test Product", created.getName());

        // Test find by ID
        Optional<Product> found = productService.findById(created.getId());
        assertTrue(found.isPresent());
        assertEquals(created.getName(), found.get().getName());

        // Test update product
        found.get().setName("Updated Product Name");
        Product updated = productService.updateProduct(found.get());
        assertEquals("Updated Product Name", updated.getName());

        // Test search products
        List<Product> searchResults = productService.searchProducts("Updated");
        assertEquals(1, searchResults.size());
        assertEquals("Updated Product Name", searchResults.get(0).getName());

        // Test stock operations
        productService.reduceStock(created.getId(), 20);
        Optional<Product> afterReduction = productService.findById(created.getId());
        assertTrue(afterReduction.isPresent());
        assertEquals(30, afterReduction.get().getStockQuantity());

        // Test find all in stock
        List<Product> inStockProducts = productService.findAllInStock();
        assertEquals(1, inStockProducts.size());

        // Test price range search
        List<Product> priceRangeResults = productService.findByPriceBetween(
                new BigDecimal("50.00"), new BigDecimal("150.00"));
        assertEquals(1, priceRangeResults.size());

        // Test delete product
        productService.deleteProduct(created.getId());
        assertFalse(productService.existsById(created.getId()));
    }

    @Test
    void testUserServiceIntegration() {
        // Test user registration
        User registered = userService.registerUser(testUser);
        assertNotNull(registered.getId());
        assertEquals("integrationuser", registered.getUsername());
        assertEquals(User.Role.USER, registered.getRole());
        assertTrue(passwordEncoder.matches("password123", registered.getPassword()));

        // Test find by username
        Optional<User> foundByUsername = userService.findByUsername("integrationuser");
        assertTrue(foundByUsername.isPresent());
        assertEquals(registered.getId(), foundByUsername.get().getId());

        // Test find by email
        Optional<User> foundByEmail = userService.findByEmail("integration@test.com");
        assertTrue(foundByEmail.isPresent());
        assertEquals(registered.getId(), foundByEmail.get().getId());

        // Test load user by username (UserDetails)
        var userDetails = userService.loadUserByUsername("integrationuser");
        assertNotNull(userDetails);
        assertEquals("integrationuser", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));

        // Test exists by username
        assertTrue(userService.existsByUsername("integrationuser"));
        assertFalse(userService.existsByUsername("nonexistent"));

        // Test find all users
        List<User> allUsers = userService.findAllUsers();
        assertEquals(1, allUsers.size());
        assertEquals(User.Role.USER, allUsers.get(0).getRole());
    }

    @Test
    void testUserServiceAdminCreation() {
        User adminUser = new User();
        adminUser.setUsername("adminuser");
        adminUser.setPassword("adminpass123");
        adminUser.setEmail("admin@test.com");

        User createdAdmin = userService.createAdmin(adminUser);
        assertNotNull(createdAdmin.getId());
        assertEquals(User.Role.ADMIN, createdAdmin.getRole());

        // Test find all admins
        List<User> allAdmins = userService.findAllAdmins();
        assertEquals(1, allAdmins.size());
        assertEquals(User.Role.ADMIN, allAdmins.get(0).getRole());
    }

    @Test
    void testUserServiceDuplicateUsername() {
        // Register first user
        userService.registerUser(testUser);

        // Try to register another user with same username
        User duplicateUser = new User();
        duplicateUser.setUsername("integrationuser"); // Same username
        duplicateUser.setPassword("differentpass");
        duplicateUser.setEmail("different@test.com");

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.registerUser(duplicateUser));
        assertEquals("Username already exists", exception.getMessage());
    }

    @Test
    void testUserServiceDuplicateEmail() {
        // Register first user
        userService.registerUser(testUser);

        // Try to register another user with same email
        User duplicateUser = new User();
        duplicateUser.setUsername("differentuser");
        duplicateUser.setPassword("password123");
        duplicateUser.setEmail("integration@test.com"); // Same email

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.registerUser(duplicateUser));
        assertEquals("Email already exists", exception.getMessage());
    }

    @Test
    void testProductServiceStockOperationsExceptions() {
        Product created = productService.createProduct(testProduct);

        // Test reduce stock with insufficient quantity
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> productService.reduceStock(created.getId(), 100) // More than available
        );
        assertTrue(exception.getMessage().contains("Insufficient stock"));

        // Test operations on non-existent product
        RuntimeException notFoundException = assertThrows(
                RuntimeException.class,
                () -> productService.updateStock(999L, 10));
        assertEquals("Product not found", notFoundException.getMessage());
    }

    @Test
    void testConcurrentOperations() {
        // Create multiple products and users to test concurrent operations
        for (int i = 0; i < 5; i++) {
            Product product = new Product();
            product.setName("Product " + i);
            product.setDescription("Description " + i);
            product.setPrice(new BigDecimal("10.00").add(new BigDecimal(i)));
            product.setStockQuantity(10 + i);
            productService.createProduct(product);

            User user = new User();
            user.setUsername("user" + i);
            user.setPassword("password" + i);
            user.setEmail("user" + i + "@test.com");
            userService.registerUser(user);
        }

        // Test bulk operations
        List<Product> allProducts = productService.findAll();
        assertEquals(5, allProducts.size());

        List<User> allUsers = userService.findAllUsers();
        assertEquals(5, allUsers.size());

        // Test search operations
        List<Product> searchResults = productService.searchProducts("Product");
        assertEquals(5, searchResults.size());
    }
}