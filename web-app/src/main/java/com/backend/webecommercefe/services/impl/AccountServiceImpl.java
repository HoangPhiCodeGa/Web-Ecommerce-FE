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

import com.backend.webecommercefe.services.AccountService;
import com.backend.webecommercefe.untils.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AccountServiceImpl implements AccountService {

    private RestClient restClient;
    private ObjectMapper objectMapper;
    private static final String ENDPOINT = "http://localhost:9996/api";

    public AccountServiceImpl(RestClient restClient, ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public ApiResponse login(String username, String password) {
        return restClient.get().uri(ENDPOINT + "/account/login?username={username}&password={password}", username, password)
                .exchange((request, response) -> {
                    ApiResponse apiResponse = null;
                    if (response.getBody().available() > 0) {
                        apiResponse = objectMapper.readValue(response.getBody(), ApiResponse.class);
                    }
                    return apiResponse;
                });
    }

    @Override
    public ApiResponse register(String username, String email, String password) {
        return restClient.post().uri(ENDPOINT + "/account/register?username={username}&email={email}&password={password}", username, email, password)
                .exchange((request, response) -> {
                    ApiResponse apiResponse = null;
                    if (response.getBody().available() > 0) {
                        apiResponse = objectMapper.readValue(response.getBody(), ApiResponse.class);
                    }
                    return apiResponse;
                });
    }
}
