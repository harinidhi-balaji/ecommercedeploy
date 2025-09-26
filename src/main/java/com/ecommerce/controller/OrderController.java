package com.ecommerce.controller;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.User;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/orders")
@PreAuthorize("hasRole('USER')")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String viewOrders(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        List<Order> orders = orderService.findByUser(user);
        model.addAttribute("orders", orders);
        return "user/orders";
    }

    @GetMapping("/{id}")
    public String viewOrder(@PathVariable Long id,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        System.out.println("DEBUG: ViewOrder called with ID: " + id);

        User user = userService.findByUsername(authentication.getName()).orElse(null);
        if (user == null) {
            System.out.println("DEBUG: User not found, redirecting to login");
            return "redirect:/login";
        }

        System.out.println("DEBUG: User found: " + user.getUsername());

        Order order = orderService.findByIdWithItems(id).orElse(null);
        if (order == null) {
            System.out.println("DEBUG: Order not found with ID: " + id);
            redirectAttributes.addFlashAttribute("error", "Order not found!");
            return "redirect:/orders";
        }

        if (!order.getUser().getId().equals(user.getId())) {
            System.out.println("DEBUG: Order user mismatch. Order user: " + order.getUser().getId() + ", Current user: "
                    + user.getId());
            redirectAttributes.addFlashAttribute("error", "Order not found!");
            return "redirect:/orders";
        }

        System.out.println(
                "DEBUG: Order found successfully. Status: " + order.getStatus() + ", Total: " + order.getTotalAmount());
        model.addAttribute("order", order);
        return "user/order-static";
    }

    @PostMapping("/create")
    public String createOrder(Authentication authentication, RedirectAttributes redirectAttributes) {
        System.out.println("DEBUG: CreateOrder called");

        User user = userService.findByUsername(authentication.getName()).orElse(null);
        if (user == null) {
            System.out.println("DEBUG: User not found in createOrder");
            return "redirect:/login";
        }

        System.out.println("DEBUG: Creating order for user: " + user.getUsername());

        try {
            Order order = orderService.createOrderFromCart(user);
            System.out.println("DEBUG: Order created successfully with ID: " + order.getId());
            redirectAttributes.addFlashAttribute("success", "Order placed successfully! Order ID: " + order.getId());
            String redirectUrl = "redirect:/orders/" + order.getId();
            System.out.println("DEBUG: Redirecting to: " + redirectUrl);
            return redirectUrl;
        } catch (RuntimeException e) {
            System.out.println("DEBUG: Error creating order: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/cart";
        }
    }

    @PostMapping("/{id}/cancel")
    public String cancelOrder(@PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        User user = userService.findByUsername(authentication.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        try {
            Order order = orderService.findById(id).orElse(null);
            if (order == null || !order.getUser().getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("error", "Order not found!");
                return "redirect:/orders";
            }

            orderService.cancelOrder(id);
            redirectAttributes.addFlashAttribute("success", "Order cancelled successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/orders/" + id;
    }
}