package com.backend.webecommercefe.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    // Định nghĩa bean RestTemplate để Spring quản lý
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
