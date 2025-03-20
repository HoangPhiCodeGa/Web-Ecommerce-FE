/*
 * @(#) $(NAME).java    1.0     3/20/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package com.backend.webecommercefe.controllers;

/*
 * @description
 * @author: Tran Tan Dat
 * @version: 1.0
 * @created: 20-March-2025 7:22 PM
 */

import com.backend.webecommercefe.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class UserController {

    @Autowired
    public UserService userService;

    @Autowired
    private ObjectMapper objectMapper;
}
