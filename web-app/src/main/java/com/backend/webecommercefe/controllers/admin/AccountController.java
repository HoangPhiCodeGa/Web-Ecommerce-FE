package com.backend.webecommercefe.controllers.admin;

import com.backend.webecommercefe.entities.Account;
import com.backend.webecommercefe.services.AccountService;
import com.backend.webecommercefe.untils.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/account")
@Slf4j
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
                model.addObject("error", response.getErrors() != null ? response.getErrors() : "Registration failed");
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
    public String loginSubmit(@ModelAttribute("account") Account account,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        try {
            ApiResponse response = accountService.login(account.getUsername(), account.getPassword());
            log.info("Login response: {}", response);

            int status = response.getStatus();
            if (status == HttpStatus.OK.value() || status == HttpStatus.CREATED.value()) {
                if (response.getData() instanceof Map) {
                    Map<String, Object> data = (Map<String, Object>) response.getData();
                    if (data.containsKey("body") && data.get("body") instanceof Map) {
                        Map<String, Object> body = (Map<String, Object>) data.get("body");
                        String token = (String) body.get("token");
                        if (token != null) {
                            session.setAttribute("jwtToken", token);
                            log.info("JWT token saved to session: {}", token);
                            return "redirect:/admin/customer";
                        }
                    }
                    redirectAttributes.addFlashAttribute("error", "Token not found in response");
                    return "redirect:/account/login";
                }
                redirectAttributes.addFlashAttribute("error", "Invalid response format");
                return "redirect:/account/login";
            }
            redirectAttributes.addFlashAttribute("error", response.getErrors() != null ? response.getErrors() : "Login failed");
            return "redirect:/account/login";
        } catch (Exception e) {
            log.error("Error in loginSubmit: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "An error occurred during login");
            return "redirect:/account/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        log.info("User logged out, session invalidated");
        return "redirect:/account/login";
    }
}