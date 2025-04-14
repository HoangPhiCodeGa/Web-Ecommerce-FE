package com.backend.webecommercefe.services.impl;

import com.backend.webecommercefe.entities.Account;
import com.backend.webecommercefe.entities.Role;
import com.backend.webecommercefe.services.AccountService;
import com.backend.webecommercefe.untils.ApiResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    private RestClient restClient;
    private ObjectMapper objectMapper;
    private static final String AUTH_ENDPOINT = "http://localhost:9996/api"; // Cổng của auth-service
    private static final String USER_ENDPOINT = "http://localhost:9995/api"; // Cổng của user-service (chưa sử dụng trong file này)

    public AccountServiceImpl(RestClient restClient, ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    private String getJwtToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String token = (String) session.getAttribute("jwtToken");
            return token != null ? "Bearer " + token : null;
        }
        return null;
    }

    @Override
    public ApiResponse login(String username, String password) {
        try {
            Map<String, String> credentials = Map.of(
                    "username", username,
                    "password", password
            );
            log.info("Attempting to login with credentials: {}", credentials);

            ResponseEntity<String> responseEntity = restClient.post()
                    .uri(AUTH_ENDPOINT + "/account/sign-in") // Sử dụng AUTH_ENDPOINT
                    .header("Content-Type", "application/json")
                    .body(credentials)
                    .retrieve()
                    .toEntity(String.class);

            String rawResponse = responseEntity.getBody();
            log.info("Login raw response: {}", rawResponse);

            if (rawResponse != null && !rawResponse.isEmpty()) {
                ApiResponse apiResponse = objectMapper.readValue(rawResponse, ApiResponse.class);
                log.info("Parsed login response: {}", apiResponse);

                // Trích xuất token từ data.body.token
                if (apiResponse.getData() instanceof Map) {
                    Map<String, Object> data = (Map<String, Object>) apiResponse.getData();
                    if (data.containsKey("body") && data.get("body") instanceof Map) {
                        Map<String, Object> body = (Map<String, Object>) data.get("body");
                        Object token = body.get("token");
                        log.info("JWT Token: {}", token);
                    }
                }

                return apiResponse;
            } else {
                log.warn("Empty response from server");
                return new ApiResponse(200, null, "No data received from server");
            }
        } catch (HttpClientErrorException e) {
            log.error("Login failed: {}", e.getResponseBodyAsString());
            return new ApiResponse(e.getStatusCode().value(), null, "Login failed: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Unexpected login error: {}", e.getMessage(), e);
            return new ApiResponse(500, null, "Unexpected error: " + e.getMessage());
        }
    }

    @Override
    public long getTotalAccounts(String keyword, HttpServletRequest request) {
        try {
            String url = AUTH_ENDPOINT + "/account?page=0&size=1"; // Sử dụng AUTH_ENDPOINT
            if (keyword != null && !keyword.isEmpty()) {
                url += "&keyword=" + keyword;
            }

            String token = getJwtToken(request);
            if (token == null) {
                log.error("No JWT token found in session");
                return 0;
            }

            ResponseEntity<String> responseEntity = restClient.get()
                    .uri(url)
                    .header("Content-Type", "application/json")
                    .header("Authorization", token)
                    .retrieve()
                    .toEntity(String.class);

            String rawResponse = responseEntity.getBody();
            if (rawResponse != null && !rawResponse.isEmpty()) {
                // Parse JSON thô
                Map<String, Object> responseMap = objectMapper.readValue(rawResponse, new TypeReference<Map<String, Object>>() {});
                Map<String, Object> page = (Map<String, Object>) responseMap.get("page");
                if (page != null && page.containsKey("total_elements")) {
                    return ((Number) page.get("total_elements")).longValue();
                }
            }
            return 0;
        } catch (Exception e) {
            log.error("Error fetching total accounts: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public ApiResponse register(Account account) {
        try {
            log.info("Sending registration request with account: {}", account);
            ResponseEntity<String> responseEntity = restClient.post()
                    .uri(AUTH_ENDPOINT + "/account/sign-up") // Sử dụng AUTH_ENDPOINT
                    .header("Content-Type", "application/json")
                    .body(account)
                    .retrieve()
                    .toEntity(String.class);

            String rawResponse = responseEntity.getBody();
            log.info("Raw register response: {}", rawResponse);

            if (rawResponse != null && !rawResponse.isEmpty()) {
                ApiResponse apiResponse = objectMapper.readValue(rawResponse, ApiResponse.class);
                log.info("Parsed register response: {}", apiResponse);

                if (apiResponse.getData() instanceof Map<?, ?>) {
                    Map<String, Object> data = (Map<String, Object>) apiResponse.getData();
                    Object statusCodeValue = data.get("statusCodeValue");
                    if (statusCodeValue instanceof Integer) {
                        apiResponse.setStatus((Integer) statusCodeValue);
                    }
                }
                return apiResponse;
            } else {
                log.warn("Empty response from server");
                return new ApiResponse(200, null, "No data received from server");
            }
        } catch (HttpClientErrorException e) {
            log.error("HTTP error during registration: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            String errorMessage = e.getResponseBodyAsString().isEmpty()
                    ? "Error " + e.getStatusCode() + ": No details provided"
                    : e.getResponseBodyAsString();
            return new ApiResponse(e.getStatusCode().value(), null, errorMessage);
        } catch (Exception e) {
            log.error("Unexpected error during registration: {}", e.getMessage());
            return new ApiResponse(500, null, "Registration failed: " + e.getMessage());
        }
    }

    @Override
    public List<Account> getAccounts(String keyword, int page, int size, HttpServletRequest request) {
        try {
            String url = AUTH_ENDPOINT + "/account?page=" + page + "&size=" + size; // Sử dụng AUTH_ENDPOINT
            if (keyword != null && !keyword.isEmpty()) {
                url += "&keyword=" + keyword;
            }
            log.info("Calling API: {}", url);

            String token = getJwtToken(request);
            if (token == null) {
                log.error("No JWT token found in session");
                return List.of();
            }

            ResponseEntity<String> responseEntity = restClient.get()
                    .uri(url)
                    .header("Content-Type", "application/json")
                    .header("Authorization", token)
                    .retrieve()
                    .toEntity(String.class);

            String rawResponse = responseEntity.getBody();
            log.info("Get accounts raw response: {}", rawResponse);

            if (rawResponse != null && !rawResponse.isEmpty()) {
                // Parse JSON thô
                Map<String, Object> responseMap = objectMapper.readValue(rawResponse, new TypeReference<Map<String, Object>>() {});
                Map<String, Object> embedded = (Map<String, Object>) responseMap.get("_embedded");
                if (embedded != null && embedded.containsKey("account")) {
                    List<Map<String, Object>> dataList = (List<Map<String, Object>>) embedded.get("account");
                    return dataList.stream().map(data -> {
                        Account account = new Account();
                        // Lấy accountId từ _links.self.href
                        Map<String, Object> links = (Map<String, Object>) data.get("_links");
                        Map<String, Object> self = (Map<String, Object>) links.get("self");
                        String href = (String) self.get("href");
                        String[] hrefParts = href.split("/");
                        String accountIdStr = hrefParts[hrefParts.length - 1];
                        account.setAccountId(Long.parseLong(accountIdStr));
                        account.setUsername((String) data.get("username"));
                        account.setPassword((String) data.get("password"));
                        account.setEmail((String) data.get("email"));

                        // Gọi API bổ sung để lấy danh sách quyền
                        Map<String, Object> rolesLink = (Map<String, Object>) links.get("roles");
                        String rolesUrl = (String) rolesLink.get("href");
                        List<Role> roles = fetchRoles(rolesUrl, token);
                        account.setRoles(roles);

                        return account;
                    }).toList();
                } else {
                    log.warn("No accounts found in API response");
                }
            } else {
                log.warn("Empty response from API");
            }
            return List.of();
        } catch (Exception e) {
            log.error("Error fetching accounts: {}", e.getMessage(), e);
            return List.of();
        }
    }

    private List<Role> fetchRoles(String rolesUrl, String token) {
        try {
            ResponseEntity<String> responseEntity = restClient.get()
                    .uri(rolesUrl)
                    .header("Content-Type", "application/json")
                    .header("Authorization", token)
                    .retrieve()
                    .toEntity(String.class);

            String rawResponse = responseEntity.getBody();
            log.info("Roles raw response for URL {}: {}", rolesUrl, rawResponse);

            if (rawResponse != null && !rawResponse.isEmpty()) {
                Map<String, Object> responseMap = objectMapper.readValue(rawResponse, new TypeReference<Map<String, Object>>() {});
                Map<String, Object> embedded = (Map<String, Object>) responseMap.get("_embedded");
                if (embedded != null && embedded.containsKey("role")) {
                    List<Map<String, Object>> rolesData = (List<Map<String, Object>>) embedded.get("role");
                    return rolesData.stream().map(roleData -> {
                        Role role = new Role();
                        // Lấy roleId từ _links.self.href
                        Map<String, Object> links = (Map<String, Object>) roleData.get("_links");
                        Map<String, Object> self = (Map<String, Object>) links.get("self");
                        String href = (String) self.get("href");
                        String[] hrefParts = href.split("/");
                        String roleIdStr = hrefParts[hrefParts.length - 1];
                        role.setRoleId(Long.parseLong(roleIdStr));
                        role.setName((String) roleData.get("name"));
                        return role;
                    }).toList();
                } else {
                    log.warn("No roles found in response for URL: {}", rolesUrl);
                }
            } else {
                log.warn("Empty response from roles API: {}", rolesUrl);
            }
            return List.of();
        } catch (Exception e) {
            log.error("Error fetching roles from {}: {}", rolesUrl, e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public Account getAccountById(Long id, HttpServletRequest request) {
        try {
            String url = AUTH_ENDPOINT + "/account/" + id; // Sử dụng AUTH_ENDPOINT
            log.info("Calling API: {}", url);

            String token = getJwtToken(request);
            if (token == null) {
                log.error("No JWT token found in session");
                return null;
            }

            ResponseEntity<String> responseEntity = restClient.get()
                    .uri(url)
                    .header("Content-Type", "application/json")
                    .header("Authorization", token)
                    .retrieve()
                    .toEntity(String.class);

            String rawResponse = responseEntity.getBody();
            log.info("Get account raw response: {}", rawResponse);

            if (rawResponse != null && !rawResponse.isEmpty()) {
                Map<String, Object> data = objectMapper.readValue(rawResponse, new TypeReference<Map<String, Object>>() {});
                Account account = new Account();
                account.setAccountId(id);
                account.setUsername((String) data.get("username"));
                account.setPassword((String) data.get("password"));
                account.setEmail((String) data.get("email"));

                // Lấy danh sách quyền
                Map<String, Object> links = (Map<String, Object>) data.get("_links");
                Map<String, Object> rolesLink = (Map<String, Object>) links.get("roles");
                String rolesUrl = (String) rolesLink.get("href");
                List<Role> roles = fetchRoles(rolesUrl, token);
                account.setRoles(roles);

                return account;
            } else {
                log.warn("Empty response from API");
                return null;
            }
        } catch (Exception e) {
            log.error("Error fetching account with ID {}: {}", id, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void updateAccount(Account account, HttpServletRequest request) {
        try {
            String token = getJwtToken(request);
            if (token == null) {
                log.error("No JWT token found in session");
                throw new RuntimeException("No JWT token found");
            }

            restClient.put()
                    .uri(AUTH_ENDPOINT + "/account/" + account.getAccountId()) // Sử dụng AUTH_ENDPOINT
                    .header("Content-Type", "application/json")
                    .header("Authorization", token)
                    .body(account)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("Error updating account: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update account", e);
        }
    }

    @Override
    public void addRole(String username, String roleName, HttpServletRequest request) {
        try {
            String token = getJwtToken(request);
            if (token == null) {
                log.error("No JWT token found in session");
                throw new RuntimeException("No JWT token found");
            }

            restClient.post()
                    .uri(AUTH_ENDPOINT + "/account/" + username + "/roles") // Sử dụng AUTH_ENDPOINT
                    .header("Content-Type", "application/json")
                    .header("Authorization", token)
                    .body(Map.of("roleName", roleName))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("Error adding role: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to add role", e);
        }
    }

    public void deleteRole(String username, String roleName, HttpServletRequest request) {
        Account account = getAccountByUsername(username, request);
        if (account == null) {
            throw new RuntimeException("Account not found for username: " + username);
        }

        String token = getJwtToken(request);
        if (token == null) {
            throw new RuntimeException("No JWT token found in session");
        }

        String rolesUrl = "http://localhost:9996/api/account/" + account.getAccountId() + "/roles/" + roleName;
        log.info("Calling API to delete role: {}", rolesUrl);

        ResponseEntity<String> responseEntity = restClient.delete()
                .uri(rolesUrl)
                .header("Content-Type", "application/json")
                .header("Authorization", token)
                .retrieve()
                .toEntity(String.class);

        log.info("Delete role response status: {}", responseEntity.getStatusCode());
        log.info("Delete role response body: {}", responseEntity.getBody());

        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to delete role: " + responseEntity.getStatusCode());
        }
    }
    public Account getAccountByUsername(String username, HttpServletRequest request) {
        String url = "http://localhost:9996/api/account/search/findByUsername?username=" + username;
        String token = getJwtToken(request);
        if (token == null) {
            log.error("No JWT token found in session");
            return null;
        }

        ResponseEntity<String> responseEntity = restClient.get()
                .uri(url)
                .header("Content-Type", "application/json")
                .header("Authorization", token)
                .retrieve()
                .toEntity(String.class);

        String rawResponse = responseEntity.getBody();
        log.info("Get account raw response: {}", rawResponse);

        if (responseEntity.getStatusCode().is2xxSuccessful() && rawResponse != null && !rawResponse.isEmpty()) {
            try {
                Map<String, Object> responseMap = objectMapper.readValue(rawResponse, new TypeReference<Map<String, Object>>() {});
                Map<String, Object> embedded = (Map<String, Object>) responseMap.get("_embedded");
                if (embedded != null && embedded.containsKey("account")) {
                    List<Map<String, Object>> accounts = (List<Map<String, Object>>) embedded.get("account");
                    if (!accounts.isEmpty()) {
                        Map<String, Object> data = accounts.get(0);
                        Account account = new Account();
                        account.setAccountId(Long.valueOf(String.valueOf(data.get("account_id"))));
                        account.setUsername((String) data.get("username"));
                        account.setPassword((String) data.get("password"));
                        account.setEmail((String) data.get("email"));

                        // Kiểm tra trường _links trước khi truy cập
                        Map<String, Object> links = (Map<String, Object>) data.get("_links");
                        String rolesUrl = null;
                        if (links != null && links.containsKey("roles")) {
                            Map<String, Object> rolesLink = (Map<String, Object>) links.get("roles");
                            rolesUrl = (String) rolesLink.get("href");
                        }

                        if (rolesUrl != null) {
                            ResponseEntity<String> rolesResponse = restClient.get()
                                    .uri(rolesUrl)
                                    .header("Content-Type", "application/json")
                                    .header("Authorization", token)
                                    .retrieve()
                                    .toEntity(String.class);

                            String rolesRawResponse = rolesResponse.getBody();
                            log.info("Roles raw response for URL {}: {}", rolesUrl, rolesRawResponse);

                            if (rolesResponse.getStatusCode().is2xxSuccessful() && rolesRawResponse != null && !rolesRawResponse.isEmpty()) {
                                Map<String, Object> rolesResponseMap = objectMapper.readValue(rolesRawResponse, new TypeReference<Map<String, Object>>() {});
                                Map<String, Object> rolesEmbedded = (Map<String, Object>) rolesResponseMap.get("_embedded");
                                if (rolesEmbedded != null && rolesEmbedded.containsKey("role")) {
                                    List<Map<String, Object>> rolesData = (List<Map<String, Object>>) rolesEmbedded.get("role");
                                    List<Role> roles = new ArrayList<>();
                                    for (Map<String, Object> roleData : rolesData) {
                                        Role role = new Role();
                                        role.setRoleId(Long.valueOf(String.valueOf(roleData.get("role_id"))));
                                        role.setName((String) roleData.get("name"));
                                        roles.add(role);
                                    }
                                    account.setRoles(roles);
                                }
                            }
                        }

                        return account;
                    }
                }
                log.warn("Account not found for username: {}", username);
                return null;
            } catch (Exception e) {
                log.error("Error fetching account with username {}: {}", username, e.getMessage(), e);
                throw new RuntimeException("Failed to fetch account: " + e.getMessage(), e);
            }
        }
        log.warn("Account not found for username: {}", username);
        return null;
    }

    public List<Role> getAllRoles(HttpServletRequest request) {
        String url = "http://localhost:9996/api/roles";
        String token = getJwtToken(request);
        if (token == null) {
            log.error("No JWT token found in session");
            return List.of();
        }
        try {
            ResponseEntity<String> responseEntity = restClient.get()
                    .uri(url)
                    .header("Content-Type", "application/json")
                    .header("Authorization", token)
                    .retrieve()
                    .toEntity(String.class);

            String rawResponse = responseEntity.getBody();
            if (responseEntity.getStatusCode().is2xxSuccessful() && rawResponse != null && !rawResponse.isEmpty()) {
                Map<String, Object> responseMap = objectMapper.readValue(rawResponse, new TypeReference<Map<String, Object>>() {});
                Map<String, Object> embedded = (Map<String, Object>) responseMap.get("_embedded");
                if (embedded != null && embedded.containsKey("role")) {
                    List<Map<String, Object>> rolesData = (List<Map<String, Object>>) embedded.get("role");
                    List<Role> roles = new ArrayList<>();
                    for (Map<String, Object> roleData : rolesData) {
                        Role role = new Role();
                        role.setRoleId(Long.valueOf(String.valueOf(roleData.get("role_id"))));
                        role.setName((String) roleData.get("name"));
                        roles.add(role);
                    }
                    return roles;
                }
            }
            return List.of();
        } catch (Exception e) {
            log.error("Error fetching all roles: {}", e.getMessage(), e);
            return List.of();
        }
    }
}