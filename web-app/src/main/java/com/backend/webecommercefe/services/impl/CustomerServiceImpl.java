package com.backend.webecommercefe.services.impl;

import com.backend.webecommercefe.entities.User;
import com.backend.webecommercefe.services.CustomerService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CustomerServiceImpl implements CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);

    @Autowired
    private RestClient restClient;

    @Autowired
    private ObjectMapper objectMapper;

    private String getJwtToken(HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("jwtToken");
        if (token != null && !token.startsWith("Bearer ")) {
            token = "Bearer " + token;
        } else if (token == null) {
            log.error("No JWT token found in session");
        }
        return token;
    }

    @Override
    public List<User> getCustomers(String keyword, int pageNo, int pageSize, HttpServletRequest request) {
        StringBuilder url = new StringBuilder("http://localhost:9995/api/user?page=" + pageNo + "&size=" + pageSize);
        if (keyword != null && !keyword.isEmpty()) {
            url.append("&keyword=").append(keyword);
        }

        try {
            String response = restClient.get()
                    .uri(url.toString())
                    .header("Content-Type", "application/json")
                    // Không cần gửi token vì /api/user đã bỏ qua xác thực
                    .retrieve()
                    .body(String.class);

            log.info("Get users raw response: {}", response);

            Map<String, Object> responseMap = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});
            if (responseMap.containsKey("data")) {
                return objectMapper.convertValue(responseMap.get("data"), new TypeReference<List<User>>() {});
            }
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("Error fetching users: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public long getTotalCustomers(String keyword, HttpServletRequest request) {
        StringBuilder url = new StringBuilder("http://localhost:9995/api/user?page=0&size=1");
        if (keyword != null && !keyword.isEmpty()) {
            url.append("&keyword=").append(keyword);
        }

        try {
            String response = restClient.get()
                    .uri(url.toString())
                    .header("Content-Type", "application/json")
                    // Không cần gửi token vì /api/user đã bỏ qua xác thực
                    .retrieve()
                    .body(String.class);

            Map<String, Object> responseMap = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});
            if (responseMap.containsKey("totalElements")) {
                return ((Number) responseMap.get("totalElements")).longValue();
            }
            return 0;
        } catch (Exception e) {
            log.error("Error fetching total users: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public User getCustomerById(Long id, HttpServletRequest request) {
        String url = "http://localhost:9995/api/user/" + id;

        try {
            String response = restClient.get()
                    .uri(url)
                    .header("Content-Type", "application/json")
                    // Không cần gửi token vì /api/user đã bỏ qua xác thực
                    .retrieve()
                    .body(String.class);

            log.info("Get user raw response: {}", response);

            Map<String, Object> responseMap = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});
            if (responseMap.containsKey("data")) {
                return objectMapper.convertValue(responseMap.get("data"), User.class);
            }
            return null;
        } catch (Exception e) {
            log.error("Error fetching user with ID {}: {}", id, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void updateCustomer(User user, HttpServletRequest request) {
        String url = "http://localhost:9995/api/user/" + user.getUserId();
        String token = getJwtToken(request);
        if (token == null) {
            throw new RuntimeException("No JWT token found in session");
        }

        try {
            restClient.put()
                    .uri(url)
                    .header("Content-Type", "application/json")
                    .header("Authorization", token)
                    .body(user)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("Error updating user with ID {}: {}", user.getUserId(), e.getMessage(), e);
            throw new RuntimeException("Failed to update user: " + e.getMessage());
        }
    }
}