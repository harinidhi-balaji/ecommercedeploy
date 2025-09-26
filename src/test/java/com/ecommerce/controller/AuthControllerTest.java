package com.ecommerce.controller;

import com.ecommerce.entity.User;
import com.ecommerce.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private User testUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setEmail("test@example.com");
    }

    @Test
    void testHome() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    void testHomeDirectCall() {
        String result = authController.home();
        assertEquals("redirect:/dashboard", result);
    }

    @Test
    void testLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    void testLoginPageDirectCall() {
        String result = authController.loginPage(model);
        assertEquals("auth/login", result);
    }

    @Test
    void testRegisterPage() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void testRegisterPageDirectCall() {
        String result = authController.registerPage(model);

        assertEquals("auth/register", result);
        verify(model, times(1)).addAttribute(eq("user"), any(User.class));
    }

    @Test
    void testRegisterUserSuccess() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.registerUser(testUser)).thenReturn(testUser);

        String result = authController.registerUser(testUser, bindingResult, redirectAttributes);

        assertEquals("redirect:/login", result);
        verify(userService, times(1)).registerUser(testUser);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("success", "Registration successful! Please login.");
    }

    @Test
    void testRegisterUserWithValidationErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String result = authController.registerUser(testUser, bindingResult, redirectAttributes);

        assertEquals("auth/register", result);
        verify(userService, never()).registerUser(any(User.class));
        verify(redirectAttributes, never()).addFlashAttribute(anyString(), anyString());
    }

    @Test
    void testRegisterUserWithServiceException() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.registerUser(testUser))
                .thenThrow(new RuntimeException("Username already exists"));

        String result = authController.registerUser(testUser, bindingResult, redirectAttributes);

        assertEquals("auth/register", result);
        verify(userService, times(1)).registerUser(testUser);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Username already exists");
    }

    @Test
    void testRegisterUserWithUnexpectedException() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.registerUser(testUser))
                .thenThrow(new RuntimeException("Database connection error"));

        String result = authController.registerUser(testUser, bindingResult, redirectAttributes);

        assertEquals("auth/register", result);
        verify(userService, times(1)).registerUser(testUser);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Database connection error");
    }

    @Test
    void testRegisterUserMockMvcSuccess() throws Exception {
        mockMvc.perform(post("/register")
                .param("username", "testuser")
                .param("password", "password123")
                .param("email", "test@example.com"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void testRegisterUserMockMvcWithValidationError() throws Exception {
        mockMvc.perform(post("/register")
                .param("username", "u") // Too short
                .param("password", "123") // Too short
                .param("email", "invalid-email"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"));
    }
}