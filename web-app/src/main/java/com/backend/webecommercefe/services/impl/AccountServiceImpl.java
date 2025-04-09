/*
 * @(#) $(NAME).java    1.0     3/21/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package com.backend.webecommercefe.services.impl;

/*
 * @description
 * @author: Tran Tan Dat
 * @version: 1.0
 * @created: 21-March-2025 10:06 PM
 */

import com.backend.webecommercefe.entities.Account;
import com.backend.webecommercefe.services.AccountService;
import com.backend.webecommercefe.untils.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    private RestClient restClient;
    private ObjectMapper objectMapper;
    private static final String ENDPOINT = "http://localhost:8080/api";

    public AccountServiceImpl(RestClient restClient, ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public ApiResponse login(String username, String password) {
        try {
            Map<String, String> credentials = Map.of(
                    "username", username,
                    "password", password
            );

            ResponseEntity<String> responseEntity = restClient.post()
                    .uri(ENDPOINT + "/account/sign-in")
                    .header("Content-Type", "application/json")
                    .body(credentials)
                    .retrieve()
                    .toEntity(String.class);

            String rawResponse = responseEntity.getBody();
            log.info("Login raw response: {}", rawResponse);

            if (rawResponse != null && !rawResponse.isEmpty()) {
                ApiResponse apiResponse = objectMapper.readValue(rawResponse, ApiResponse.class);

                // Nếu token có trong data
                if (apiResponse.getData() instanceof Map<?, ?> dataMap) {
                    Object token = dataMap.get("token");
                    log.info("JWT Token: {}", token);
                }

                return apiResponse;
            } else {
                return new ApiResponse(200, null, "No data received from server");
            }
        } catch (HttpClientErrorException e) {
            log.error("Login failed: {}", e.getResponseBodyAsString());
            return new ApiResponse(e.getStatusCode().value(), null, "Login failed: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Unexpected login error: {}", e.getMessage());
            return new ApiResponse(500, null, "Unexpected error: " + e.getMessage());
        }
    }


    @Override
    public ApiResponse register(Account account) {
        try {
            log.info("Sending registration request with account: {}", account);
            ResponseEntity<String> responseEntity = restClient.post()
                    .uri(ENDPOINT + "/account/sign-up")
                    .header("Content-Type", "application/json")
                    .body(account)
                    .retrieve()
                    .toEntity(String.class);

            String rawResponse = responseEntity.getBody();
            log.info("Raw register response: {}", rawResponse);

            if (rawResponse != null && !rawResponse.isEmpty()) {
                ApiResponse apiResponse = objectMapper.readValue(rawResponse, ApiResponse.class);
                log.info("Parsed register response: {}", apiResponse);

                // Nếu data chứa statusCodeValue, ưu tiên sử dụng nó
                if (apiResponse.getData() instanceof Map<?,?>) {
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
}
