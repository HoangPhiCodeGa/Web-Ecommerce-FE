//package com.backend.webecommercefe.configs;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")  // Áp dụng cho tất cả các endpoint
//                .allowedOrigins("http://localhost:9090")  // Cho phép yêu cầu từ cổng 9090
//                .allowedMethods("GET", "POST", "PUT", "DELETE")  // Cho phép các phương thức HTTP
//                .allowedHeaders("*");  // Cho phép tất cả các header
//    }
//}
