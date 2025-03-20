/*
 * @(#) $(NAME).java    1.0     3/20/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package com.backend.webecommercefe.services.impl;

/*
 * @description
 * @author: Tran Tan Dat
 * @version: 1.0
 * @created: 20-March-2025 7:16 PM
 */

import com.backend.webecommercefe.entities.User;
import com.backend.webecommercefe.services.UserService;
import com.backend.webecommercefe.untils.ApiResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private RestClient restClient;
    private ObjectMapper objectMapper;
    private static final String ENDPOINT = "http://localhost:9995/api";

    public UserServiceImpl(RestClient restClient, ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public ApiResponse findById(int id) {
        return restClient.get().uri(ENDPOINT + "/users/{id}", id).accept(MediaType.APPLICATION_JSON)
                .exchange((request, response) -> {
                    ApiResponse apiResponse = null;
                    if (response.getBody().available() > 0) {
                        apiResponse = objectMapper.readValue(response.getBody(), ApiResponse.class);
                        apiResponse.setData(objectMapper.convertValue(apiResponse.getData(), new TypeReference<User>() {
                        }));
                    }
                    return apiResponse;
                });
    }

    @Override
    public ApiResponse findAll() {
        return restClient.get().uri(ENDPOINT + "/users").accept(MediaType.APPLICATION_JSON)
                .exchange((request, response) -> {
                    ApiResponse apiResponse = null;
                    if (response.getBody().available() > 0) {
                        apiResponse = objectMapper.readValue(response.getBody(), ApiResponse.class);
                        apiResponse.setData(
                                objectMapper.convertValue(apiResponse.getData(), new TypeReference<List<User>>() {
                                }));
                    }
                    return apiResponse;
                });
    }


    @Override
    public ApiResponse save(User user) {
        return restClient.post().uri(ENDPOINT + "/users").accept(MediaType.APPLICATION_JSON).body(user)
                .exchange((request, response) -> {
                    ApiResponse apiResponse = null;
                    if (response.getBody().available() > 0) {
                        apiResponse = objectMapper.readValue(response.getBody(), ApiResponse.class);
                    }
                    return apiResponse;
                });
    }


    @Override
    public ApiResponse update(int id, User user) {
        return restClient.put().uri(ENDPOINT + "/users/{id}", id).accept(MediaType.APPLICATION_JSON).body(user)
                .exchange((request, response) -> {
                    ApiResponse apiResponse = null;
                    if (response.getBody().available() > 0) {
                        apiResponse = objectMapper.readValue(response.getBody(), ApiResponse.class);
                    }
                    return apiResponse;
                });
    }

    @Override
    public ApiResponse delete(int id) {
        return restClient.delete().uri(ENDPOINT + "/users/{id}", id).accept(MediaType.APPLICATION_JSON)
                .exchange((request, response) -> {
                    ApiResponse apiResponse = null;
                    if (response.getBody().available() > 0) {
                        apiResponse = objectMapper.readValue(response.getBody(), ApiResponse.class);
                    }

                    return apiResponse;
                });
    }

}
