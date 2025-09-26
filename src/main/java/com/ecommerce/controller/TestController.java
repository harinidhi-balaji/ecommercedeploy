package com.ecommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
public class TestController {

    @GetMapping("/template")
    public String testTemplate(Model model) {
        System.out.println("DEBUG: Test template endpoint called");
        model.addAttribute("message", "Template test successful!");
        return "user/order-detail-simple";
    }

    @GetMapping("/debug")
    public String debugPage(Model model) {
        System.out.println("DEBUG: Debug endpoint called");
        model.addAttribute("message", "Debug page working");
        return "debug";
    }
}