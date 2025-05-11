/*
 * @(#) $(NAME).java    1.0     3/20/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package com.backend.webecommercefe.services;


import com.backend.webecommercefe.entities.User;
import com.backend.webecommercefe.untils.ApiResponse;

/*
 * @description
 * @author: Tran Tan Dat
 * @version: 1.0
 * @created: 20-March-2025 7:15 PM
 */
public interface UserService {
    public ApiResponse findById(int id);
    public ApiResponse findAll();
    public ApiResponse save(User user);
    public ApiResponse update(int id, User user);
    public ApiResponse delete(int id);
}

    