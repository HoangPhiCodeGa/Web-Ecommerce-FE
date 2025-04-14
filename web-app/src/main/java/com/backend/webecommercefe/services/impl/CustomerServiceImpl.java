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
        StringBuilder url = new StringBuilder("http://localhost:9996/api/customer?page=" + pageNo + "&size=" + pageSize);
        if (keyword != null && !keyword.isEmpty()) {
            url.append("&keyword=").append(keyword);
        }

        try {
            String response = restClient.get()
                    .uri(url.toString())
                    .header("Content-Type", "application/json")
                    // Không cần gửi token vì /api/customer đã bỏ qua xác thực
                    .retrieve()
                    .body(String.class);

            log.info("Get customers raw response: {}", response);

            Map<String, Object> responseMap = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});
            Map<String, Object> embedded = (Map<String, Object>) responseMap.get("_embedded");
            if (embedded != null && embedded.containsKey("customer")) {
                List<Map<String, Object>> customers = (List<Map<String, Object>>) embedded.get("customer");
                List<User> users = new ArrayList<>();
                for (Map<String, Object> customerData : customers) {
                    User user = new User();
                    user.setUserId(Long.valueOf(String.valueOf(customerData.get("id"))));
                    user.setFullName((String) customerData.get("tenKH"));
                    user.setPhoneNumber((String) customerData.get("sdt"));
                    user.setDateOfBirth(customerData.get("dob") != null ? LocalDate.parse((String) customerData.get("dob")) : null);
                    user.setGender((Boolean) customerData.get("gioiTinh"));
                    user.setAddress((String) customerData.get("diaChi"));
                    user.setEmail((String) customerData.get("email"));
                    users.add(user);
                }
                return users;
            }
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("Error fetching customers: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public long getTotalCustomers(String keyword, HttpServletRequest request) {
        StringBuilder url = new StringBuilder("http://localhost:9996/api/customer");
        if (keyword != null && !keyword.isEmpty()) {
            url.append("?keyword=").append(keyword);
        }

        try {
            String response = restClient.get()
                    .uri(url.toString())
                    .header("Content-Type", "application/json")
                    // Không cần gửi token vì /api/customer đã bỏ qua xác thực
                    .retrieve()
                    .body(String.class);

            Map<String, Object> responseMap = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});
            Map<String, Object> embedded = (Map<String, Object>) responseMap.get("_embedded");
            if (embedded != null && embedded.containsKey("customer")) {
                List<Map<String, Object>> customers = (List<Map<String, Object>>) embedded.get("customer");
                return customers.size();
            }
            return 0;
        } catch (Exception e) {
            log.error("Error fetching total customers: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public User getCustomerById(Long id, HttpServletRequest request) {
        String url = "http://localhost:9996/api/customer/" + id;

        try {
            String response = restClient.get()
                    .uri(url)
                    .header("Content-Type", "application/json")
                    // Không cần gửi token vì /api/customer đã bỏ qua xác thực
                    .retrieve()
                    .body(String.class);

            log.info("Get customer raw response: {}", response);

            Map<String, Object> responseMap = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});
            User user = new User();
            user.setUserId(Long.valueOf(String.valueOf(responseMap.get("id"))));
            user.setFullName((String) responseMap.get("tenKH"));
            user.setPhoneNumber((String) responseMap.get("sdt"));
            user.setDateOfBirth(responseMap.get("dob") != null ? LocalDate.parse((String) responseMap.get("dob")) : null);
            user.setGender((Boolean) responseMap.get("gioiTinh"));
            user.setAddress((String) responseMap.get("diaChi"));
            user.setEmail((String) responseMap.get("email"));
            return user;
        } catch (Exception e) {
            log.error("Error fetching customer with ID {}: {}", id, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void updateCustomer(User user, HttpServletRequest request) {
        String url = "http://localhost:9996/api/customer/" + user.getUserId();
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
            log.error("Error updating customer with ID {}: {}", user.getUserId(), e.getMessage(), e);
            throw new RuntimeException("Failed to update customer: " + e.getMessage());
        }
    }
}