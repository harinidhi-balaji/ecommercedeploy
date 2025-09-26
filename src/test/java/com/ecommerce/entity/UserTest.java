package com.ecommerce.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("test@example.com");
        user.setRole(User.Role.USER);
    }

    @Test
    void testUserCreation() {
        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals("test@example.com", user.getEmail());
        assertEquals(User.Role.USER, user.getRole());
    }

    @Test
    void testUserAuthoritiesForUser() {
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void testUserAuthoritiesForAdmin() {
        user.setRole(User.Role.ADMIN);
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void testUserDetailsDefaults() {
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
    }

    @Test
    void testAccountStates() {
        user.setAccountNonExpired(false);
        user.setAccountNonLocked(false);
        user.setCredentialsNonExpired(false);
        user.setEnabled(false);

        assertFalse(user.isAccountNonExpired());
        assertFalse(user.isAccountNonLocked());
        assertFalse(user.isCredentialsNonExpired());
        assertFalse(user.isEnabled());
    }

    @Test
    void testPrePersistSetsCreatedAt() {
        LocalDateTime beforeCreation = LocalDateTime.now();
        user.onCreate();
        LocalDateTime afterCreation = LocalDateTime.now();

        assertNotNull(user.getCreatedAt());
        assertTrue(user.getCreatedAt().isAfter(beforeCreation) || user.getCreatedAt().isEqual(beforeCreation));
        assertTrue(user.getCreatedAt().isBefore(afterCreation) || user.getCreatedAt().isEqual(afterCreation));
    }

    @Test
    void testRoleEnum() {
        assertEquals(2, User.Role.values().length);
        assertEquals(User.Role.USER, User.Role.valueOf("USER"));
        assertEquals(User.Role.ADMIN, User.Role.valueOf("ADMIN"));
    }
}