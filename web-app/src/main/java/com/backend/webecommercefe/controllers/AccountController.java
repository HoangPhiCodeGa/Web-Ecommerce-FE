package com.backend.webecommercefe.controllers;

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

/**
 * Controller xử lý các yêu cầu liên quan đến đăng nhập, đăng ký và quên mật khẩu.
 */
@Controller
@RequestMapping("/account")
@Slf4j
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Hiển thị form đăng nhập.
     *
     * @param model Đối tượng ModelAndView để truyền dữ liệu sang view
     * @return Tên template (account/login.html)
     */
    @GetMapping("/login")
    public ModelAndView login(ModelAndView model) {
        model.addObject("account", new Account());
        model.setViewName("account/login");
        return model;
    }

    /**
     * Hiển thị form đăng ký.
     *
     * @param model Đối tượng ModelAndView để truyền dữ liệu sang view
     * @return Tên template (account/register.html)
     */
    @GetMapping("/register")
    public ModelAndView showRegisterForm(ModelAndView model) {
        model.addObject("account", new Account());
        model.setViewName("account/register");
        return model;
    }

    /**
     * Hiển thị form quên mật khẩu.
     *
     * @param model Đối tượng ModelAndView để truyền dữ liệu sang view
     * @return Tên template (account/forgot-password.html)
     */
    @GetMapping("/forgot-password")
    public ModelAndView logout(ModelAndView model) {
        model.setViewName("account/forgot-password");
        return model;
    }

    /**
     * Xử lý đăng ký tài khoản.
     *
     * @param account Đối tượng Account chứa thông tin đăng ký
     * @param model Đối tượng ModelAndView để truyền dữ liệu sang view
     * @return Chuyển hướng về trang đăng nhập nếu thành công, hoặc quay lại form đăng ký nếu thất bại
     */
    @PostMapping("/register")
    public ModelAndView registerSubmit(@ModelAttribute("account") Account account, ModelAndView model) {
        try {
            ApiResponse response = accountService.register(account);
            log.info("Registration response: {}", response);

            // Kiểm tra status 200 hoặc 201
            boolean isSuccess = response.getStatus() == HttpStatus.OK.value() ||
                    response.getStatus() == HttpStatus.CREATED.value();

            // Nếu data chứa statusCodeValue = 201, cũng coi là thành công
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

    /**
     * Xử lý đăng nhập.
     *
     * @param account Đối tượng Account chứa thông tin đăng nhập (username, password)
     * @param session Đối tượng HttpSession để lưu JWT token
     * @param redirectAttributes Đối tượng để truyền thông báo qua redirect
     * @return Chuyển hướng về trang Admin nếu thành công, hoặc quay lại form đăng nhập nếu thất bại
     */
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
                    String token = (String) data.get("token"); // Lấy token trực tiếp từ data
                    if (token != null) {
                        session.setAttribute("jwtToken", token); // Lưu token vào session
                        log.info("JWT token saved to session: {}", token);
                        return "redirect:/admin/customer"; // Chuyển hướng đến trang khách hàng
                    } else {
                        redirectAttributes.addFlashAttribute("error", "Token not found in response");
                        return "redirect:/account/login";
                    }
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
    /**
     * Xử lý đăng xuất.
     *
     * @param session Đối tượng HttpSession để xóa token
     * @return Chuyển hướng về trang đăng nhập
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Xóa session, bao gồm token
        log.info("User logged out, session invalidated");
        return "redirect:/account/login";
    }
}