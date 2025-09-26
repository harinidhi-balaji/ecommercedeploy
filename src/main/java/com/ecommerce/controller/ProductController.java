package com.ecommerce.controller;

import com.ecommerce.entity.Product;
import com.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/user/home")
    public String home(Model model) {
        List<Product> products = productService.findAllInStock();
        model.addAttribute("products", products);
        return "user/home";
    }

    @GetMapping("/products")
    public String listProducts(@RequestParam(value = "search", required = false) String search, Model model) {
        List<Product> products;

        if (search != null && !search.trim().isEmpty()) {
            products = productService.searchProducts(search.trim());
            model.addAttribute("searchTerm", search);
        } else {
            products = productService.findAllInStock();
        }

        model.addAttribute("products", products);
        return "user/products";
    }

    @GetMapping("/products/{id}")
    public String viewProduct(@PathVariable Long id, Model model) {
        Product product = productService.findById(id).orElse(null);
        if (product == null) {
            return "redirect:/products";
        }
        model.addAttribute("product", product);
        return "user/product-detail";
    }
}