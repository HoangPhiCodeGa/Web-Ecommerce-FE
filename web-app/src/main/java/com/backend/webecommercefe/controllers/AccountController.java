/*
 * @(#) $(NAME).java    1.0     3/21/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package com.backend.webecommercefe.controllers;

/*
 * @description
 * @author: Tran Tan Dat
 * @version: 1.0
 * @created: 21-March-2025 10:07 PM
 */

import com.backend.webecommercefe.entities.Account;
import com.backend.webecommercefe.services.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/login")
    public ModelAndView login(ModelAndView model) {
        model.setViewName("account/login");
        return model;
    }

    @GetMapping("/register")
    public ModelAndView register(ModelAndView model) {
        model.setViewName("account/register");
        return model;
    }

    @GetMapping("/forgot-password")
    public ModelAndView logout(ModelAndView model) {
        model.setViewName("account/forgot-password");
        return model;
    }
}
