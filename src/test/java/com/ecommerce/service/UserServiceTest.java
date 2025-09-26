package com.ecommerce.service;

import com.ecommerce.entity.User;
import com.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("rawpassword");
        testUser.setEmail("test@example.com");
        testUser.setRole(User.Role.USER);
    }

    @Test
    void testLoadUserByUsername() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        UserDetails result = userService.loadUserByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals(testUser, result);
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testLoadUserByUsernameNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("nonexistent"));

        assertEquals("User not found: nonexistent", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    void testRegisterUser() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("rawpassword")).thenReturn("encodedpassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.registerUser(testUser);

        assertNotNull(result);
        assertEquals(User.Role.USER, testUser.getRole());
        verify(userRepository, times(1)).existsByUsername("testuser");
        verify(userRepository, times(1)).existsByEmail("test@example.com");
        verify(passwordEncoder, times(1)).encode("rawpassword");
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testRegisterUserUsernameExists() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.registerUser(testUser));

        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, times(1)).existsByUsername("testuser");
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterUserEmailExists() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.registerUser(testUser));

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, times(1)).existsByUsername("testuser");
        verify(userRepository, times(1)).existsByEmail("test@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testCreateAdmin() {
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setPassword("adminpassword");
        adminUser.setEmail("admin@example.com");

        when(userRepository.existsByUsername("admin")).thenReturn(false);
        when(userRepository.existsByEmail("admin@example.com")).thenReturn(false);
        when(passwordEncoder.encode("adminpassword")).thenReturn("encodedadminpassword");
        when(userRepository.save(any(User.class))).thenReturn(adminUser);

        User result = userService.createAdmin(adminUser);

        assertNotNull(result);
        assertEquals(User.Role.ADMIN, adminUser.getRole());
        verify(userRepository, times(1)).existsByUsername("admin");
        verify(userRepository, times(1)).existsByEmail("admin@example.com");
        verify(passwordEncoder, times(1)).encode("adminpassword");
        verify(userRepository, times(1)).save(adminUser);
    }

    @Test
    void testCreateAdminUsernameExists() {
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@example.com");

        when(userRepository.existsByUsername("admin")).thenReturn(true);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.createAdmin(adminUser));

        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, times(1)).existsByUsername("admin");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testFindByUsername() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findByUsername("testuser");

        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testFindByUsernameNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByUsername("nonexistent");

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    void testFindByEmail() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testFindById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testFindAllUsers() {
        List<User> users = Arrays.asList(testUser, new User());
        when(userRepository.findAllUsers()).thenReturn(users);

        List<User> result = userService.findAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAllUsers();
    }

    @Test
    void testFindAllAdmins() {
        User adminUser = new User();
        adminUser.setRole(User.Role.ADMIN);
        List<User> admins = Arrays.asList(adminUser);
        when(userRepository.findAllAdmins()).thenReturn(admins);

        List<User> result = userService.findAllAdmins();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findAllAdmins();
    }

    @Test
    void testExistsByUsername() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        boolean result = userService.existsByUsername("testuser");

        assertTrue(result);
        verify(userRepository, times(1)).existsByUsername("testuser");
    }

    @Test
    void testExistsByUsernameNotFound() {
        when(userRepository.existsByUsername("nonexistent")).thenReturn(false);

        boolean result = userService.existsByUsername("nonexistent");

        assertFalse(result);
        verify(userRepository, times(1)).existsByUsername("nonexistent");
    }
}