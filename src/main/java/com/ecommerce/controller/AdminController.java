package com.ecommerce.controller;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Product> lowStockProducts = productService.findLowStockProducts(10);
        List<Order> recentOrders = orderService.findAll().stream().limit(10).toList();
        Double totalRevenue = orderService.getTotalRevenue();

        model.addAttribute("lowStockProducts", lowStockProducts);
        model.addAttribute("recentOrders", recentOrders);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalProducts", productService.findAll().size());
        model.addAttribute("totalOrders", orderService.findAll().size());

        return "admin/dashboard";
    }

    @GetMapping("/products")
    public String manageProducts(Model model) {
        List<Product> products = productService.findAllOrderByCreatedAtDesc();
        model.addAttribute("products", products);
        return "admin/manage-products";
    }

    @GetMapping("/products/add")
    public String addProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "admin/add-product";
    }

    @PostMapping("/products/add")
    public String addProduct(@Valid @ModelAttribute("product") Product product,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "admin/add-product";
        }

        try {
            productService.createProduct(product);
            redirectAttributes.addFlashAttribute("success", "Product added successfully!");
            return "redirect:/admin/products";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding product: " + e.getMessage());
            return "redirect:/admin/products/add";
        }
    }

    @GetMapping("/products/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Product product = productService.findById(id).orElse(null);
        if (product == null) {
            redirectAttributes.addFlashAttribute("error", "Product not found!");
            return "redirect:/admin/products";
        }
        model.addAttribute("product", product);
        return "admin/edit-product";
    }

    @PostMapping("/products/edit/{id}")
    public String editProduct(@PathVariable Long id,
                              @Valid @ModelAttribute("product") Product product,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "admin/edit-product";
        }

        try {
            product.setId(id);
            productService.updateProduct(product);
            redirectAttributes.addFlashAttribute("success", "Product updated successfully!");
            return "redirect:/admin/products";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating product: " + e.getMessage());
            return "redirect:/admin/products/edit/" + id;
        }
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("success", "Product deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting product: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/orders")
    public String manageOrders(Model model) {
        List<Order> orders = orderService.findAll();
        model.addAttribute("orders", orders);
        return "admin/manage-orders";
    }

    @GetMapping("/orders/{id}")
    public String viewOrder(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Order order = orderService.findById(id).orElse(null);
        if (order == null) {
            redirectAttributes.addFlashAttribute("error", "Order not found!");
            return "redirect:/admin/orders";
        }
        model.addAttribute("order", order);
        return "admin/view-order";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable Long id,
                                    @RequestParam("status") Order.OrderStatus status,
                                    RedirectAttributes redirectAttributes) {
        try {
            orderService.updateOrderStatus(id, status);
            redirectAttributes.addFlashAttribute("success", "Order status updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating order status: " + e.getMessage());
        }
        return "redirect:/admin/orders/" + id;
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "admin/manage-users";
    }
}