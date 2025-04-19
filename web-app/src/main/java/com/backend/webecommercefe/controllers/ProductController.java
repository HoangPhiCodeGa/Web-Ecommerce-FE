package com.backend.webecommercefe.controllers;

import com.backend.webecommercefe.entities.Product;
import com.backend.webecommercefe.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.CrossOrigin;  // Import annotation @CrossOrigin

import java.util.List;

@Controller
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:9090")  // Thêm annotation @CrossOrigin tại controller level
public class ProductController {

    private final ProductService productService;

    @GetMapping("/home")
    public String getAllProducts(Model model) {
        List<Product> products = productService.getAllProducts();  // Gọi API từ BE
        model.addAttribute("listProduct", products);  // Đưa sản phẩm vào model
        return "user/home/index";  // Trả về view
    }
}
