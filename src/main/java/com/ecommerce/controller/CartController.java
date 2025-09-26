package com.ecommerce.controller;

import com.ecommerce.entity.Cart;
import com.ecommerce.entity.User;
import com.ecommerce.service.CartService;
import com.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/cart")
@PreAuthorize("hasRole('USER')")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String viewCart(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        List<Cart> cartItems = cartService.getCartItems(user);
        BigDecimal cartTotal = cartService.getCartTotal(user);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", cartTotal);
        model.addAttribute("cartItemCount", cartItems.size());

        return "user/cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam("productId") Long productId,
                            @RequestParam("quantity") Integer quantity,
                            Authentication authentication,
                            RedirectAttributes redirectAttributes) {

        User user = userService.findByUsername(authentication.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        try {
            cartService.addToCart(user, productId, quantity);
            redirectAttributes.addFlashAttribute("success", "Product added to cart successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/products/" + productId;
    }

    @PostMapping("/remove/{cartId}")
    public String removeFromCart(@PathVariable Long cartId, RedirectAttributes redirectAttributes) {
        try {
            cartService.removeFromCart(cartId);
            redirectAttributes.addFlashAttribute("success", "Item removed from cart!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/update/{cartId}")
    public String updateQuantity(@PathVariable Long cartId,
                                 @RequestParam("quantity") Integer quantity,
                                 RedirectAttributes redirectAttributes) {
        try {
            cartService.updateQuantity(cartId, quantity);
            redirectAttributes.addFlashAttribute("success", "Cart updated successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/increase/{cartId}")
    public String increaseQuantity(@PathVariable Long cartId, RedirectAttributes redirectAttributes) {
        try {
            cartService.increaseQuantity(cartId);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/decrease/{cartId}")
    public String decreaseQuantity(@PathVariable Long cartId, RedirectAttributes redirectAttributes) {
        try {
            cartService.decreaseQuantity(cartId);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(Authentication authentication, RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(authentication.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        cartService.clearCart(user);
        redirectAttributes.addFlashAttribute("success", "Cart cleared successfully!");
        return "redirect:/cart";
    }
}