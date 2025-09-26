package com.ecommerce.repository;

import com.ecommerce.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User adminUser;
    private User regularUser;
    private User anotherUser;

    @BeforeEach
    void setUp() {
        // Create test users
        adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setPassword("password123");
        adminUser.setEmail("admin@example.com");
        adminUser.setRole(User.Role.ADMIN);

        regularUser = new User();
        regularUser.setUsername("user1");
        regularUser.setPassword("password123");
        regularUser.setEmail("user1@example.com");
        regularUser.setRole(User.Role.USER);

        anotherUser = new User();
        anotherUser.setUsername("user2");
        anotherUser.setPassword("password123");
        anotherUser.setEmail("user2@example.com");
        anotherUser.setRole(User.Role.USER);

        // Persist the entities
        entityManager.persistAndFlush(adminUser);
        entityManager.persistAndFlush(regularUser);
        entityManager.persistAndFlush(anotherUser);
    }

    @Test
    void testFindByUsername() {
        Optional<User> result = userRepository.findByUsername("admin");

        assertTrue(result.isPresent());
        assertEquals("admin", result.get().getUsername());
        assertEquals("admin@example.com", result.get().getEmail());
        assertEquals(User.Role.ADMIN, result.get().getRole());
    }

    @Test
    void testFindByUsernameNotFound() {
        Optional<User> result = userRepository.findByUsername("nonexistent");

        assertFalse(result.isPresent());
    }

    @Test
    void testFindByUsernameCaseSensitive() {
        Optional<User> result = userRepository.findByUsername("ADMIN");

        assertFalse(result.isPresent());
    }

    @Test
    void testFindByEmail() {
        Optional<User> result = userRepository.findByEmail("user1@example.com");

        assertTrue(result.isPresent());
        assertEquals("user1", result.get().getUsername());
        assertEquals("user1@example.com", result.get().getEmail());
        assertEquals(User.Role.USER, result.get().getRole());
    }

    @Test
    void testFindByEmailNotFound() {
        Optional<User> result = userRepository.findByEmail("nonexistent@example.com");

        assertFalse(result.isPresent());
    }

    @Test
    void testExistsByUsername() {
        assertTrue(userRepository.existsByUsername("admin"));
        assertTrue(userRepository.existsByUsername("user1"));
        assertTrue(userRepository.existsByUsername("user2"));
        assertFalse(userRepository.existsByUsername("nonexistent"));
    }

    @Test
    void testExistsByEmail() {
        assertTrue(userRepository.existsByEmail("admin@example.com"));
        assertTrue(userRepository.existsByEmail("user1@example.com"));
        assertTrue(userRepository.existsByEmail("user2@example.com"));
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
    }

    @Test
    void testFindAllAdmins() {
        List<User> admins = userRepository.findAllAdmins();

        assertEquals(1, admins.size());
        assertEquals("admin", admins.get(0).getUsername());
        assertEquals(User.Role.ADMIN, admins.get(0).getRole());
    }

    @Test
    void testFindAllUsers() {
        List<User> users = userRepository.findAllUsers();

        assertEquals(2, users.size());
        assertTrue(users.stream().allMatch(u -> u.getRole() == User.Role.USER));
        assertTrue(users.stream().anyMatch(u -> u.getUsername().equals("user1")));
        assertTrue(users.stream().anyMatch(u -> u.getUsername().equals("user2")));
    }

    @Test
    void testBasicCrudOperations() {
        // Test save
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("newpassword");
        newUser.setEmail("newuser@example.com");
        newUser.setRole(User.Role.USER);

        User saved = userRepository.save(newUser);
        assertNotNull(saved.getId());
        assertEquals("newuser", saved.getUsername());

        // Test findById
        Optional<User> found = userRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("newuser", found.get().getUsername());

        // Test update
        found.get().setEmail("updated@example.com");
        User updated = userRepository.save(found.get());
        assertEquals("updated@example.com", updated.getEmail());

        // Test delete
        userRepository.deleteById(saved.getId());
        Optional<User> deleted = userRepository.findById(saved.getId());
        assertFalse(deleted.isPresent());
    }

    @Test
    void testCount() {
        long count = userRepository.count();
        assertEquals(3, count);
    }

    @Test
    void testFindAll() {
        List<User> all = userRepository.findAll();
        assertEquals(3, all.size());
    }

    @Test
    void testUniqueConstraints() {
        // Test duplicate username
        User duplicateUsername = new User();
        duplicateUsername.setUsername("admin"); // Same as existing admin
        duplicateUsername.setPassword("password");
        duplicateUsername.setEmail("different@example.com");
        duplicateUsername.setRole(User.Role.USER);

        // This should fail due to unique constraint on username
        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(duplicateUsername);
        });
    }

    @Test
    void testUserDefaults() {
        User user = new User();
        user.setUsername("testdefaults");
        user.setPassword("password");
        user.setEmail("defaults@example.com");

        User saved = userRepository.save(user);

        assertEquals(User.Role.USER, saved.getRole());
        assertTrue(saved.isAccountNonExpired());
        assertTrue(saved.isAccountNonLocked());
        assertTrue(saved.isCredentialsNonExpired());
        assertTrue(saved.isEnabled());
    }

    @Test
    void testPrePersistCallback() {
        User user = new User();
        user.setUsername("timestamptest");
        user.setPassword("password");
        user.setEmail("timestamp@example.com");

        User saved = userRepository.save(user);

        assertNotNull(saved.getCreatedAt());
    }
}