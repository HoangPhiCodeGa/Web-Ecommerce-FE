package com.backend.webecommercefe.controllers.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestClient;

@Controller
public class OrderController {
    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private RestClient restClient;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/admin/order")
    public String getAdmin(){
        log.error("trang quản lý order");
        return "/admin/order/index";
    }
}
