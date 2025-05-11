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
import com.backend.webecommercefe.untils.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/login")
    public ModelAndView login(ModelAndView model) {
        model.addObject("account", new Account());
        model.setViewName("account/login");
        return model;
    }

    @GetMapping("/register")
    public ModelAndView showRegisterForm(ModelAndView model) {
        model.addObject("account", new Account());
        model.setViewName("account/register");
        return model;
    }

    @GetMapping("/forgot-password")
    public ModelAndView logout(ModelAndView model) {
        model.setViewName("account/forgot-password");
        return model;
    }

    @PostMapping("/register")
    public ModelAndView registerSubmit(@ModelAttribute("account") Account account, ModelAndView model) {
        try {
            ApiResponse response = accountService.register(account);
            log.info("Registration response: {}", response);

            boolean isSuccess = response.getStatus() == HttpStatus.OK.value() ||
                    response.getStatus() == HttpStatus.CREATED.value();

            if (!isSuccess && response.getData() instanceof Map) {
                Map<String, Object> data = (Map<String, Object>) response.getData();
                Object statusCodeValue = data.get("statusCodeValue");
                if (statusCodeValue instanceof Integer && (Integer) statusCodeValue == HttpStatus.CREATED.value()) {
                    isSuccess = true;
                }
            }

            if (isSuccess) {
                model.setViewName("redirect:/account/login");
            } else {
                model.addObject("error", response.getErrors() != null ? response.getErrors() : "");
                model.setViewName("account/register");
            }
        } catch (Exception e) {
            log.error("Error in registerSubmit: {}", e.getMessage());
            model.addObject("error", "An error occurred during registration");
            model.setViewName("account/register");
        }
        return model;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> loginSubmit(@ModelAttribute("account") Account account,
                                                   HttpSession session) {
        try {
            // Kiểm tra dữ liệu đầu vào
            if (account.getUsername() == null || account.getUsername().isEmpty() ||
                    account.getPassword() == null || account.getPassword().isEmpty()) {
                log.warn("Invalid login input: username={}, password={}", account.getUsername(), account.getPassword());
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Username and password are required", null));
            }

            ApiResponse response = accountService.login(account.getUsername(), account.getPassword());
            log.info("Login response: {}", response);

            if (response.getStatus() == HttpStatus.OK.value()) {
                // Lưu thông tin vào session (tùy chọn, nếu cần)
                session.setAttribute("account", response.getData());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(response.getStatus()).body(response);
            }
        } catch (Exception e) {
            log.error("Error in loginSubmit: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An error occurred during login: " + e.getMessage(), null));
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        log.info("User logged out, session invalidated");
        return "redirect:/account/login";
    }
}