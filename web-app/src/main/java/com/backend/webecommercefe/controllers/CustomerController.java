package com.backend.webecommercefe.controllers;

import com.backend.webecommercefe.entities.User;
import com.backend.webecommercefe.services.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/customer")
public class CustomerController {

    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);
    private static final int PAGE_SIZE = 10;

    @Autowired
    private CustomerService customerService;

    // Hiển thị danh sách khách hàng
    @GetMapping
    public String index(@RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
                        @RequestParam(value = "keyword", required = false) String keyword,
                        HttpServletRequest request,
                        Model model) {
        pageNo = pageNo < 1 ? 1 : pageNo;
        List<User> customers = customerService.getCustomers(keyword, pageNo - 1, PAGE_SIZE, request);
        long totalCustomers = customerService.getTotalCustomers(keyword, request);
        int totalPage = (int) Math.ceil((double) totalCustomers / PAGE_SIZE);

        log.info("Fetched customers: {}", customers);
        log.info("Total customers: {}, Total pages: {}", totalCustomers, totalPage);

        model.addAttribute("khachHangs", customers);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("keyword", keyword);
        return "admin/customer/index";
    }

    // Hiển thị trang chỉnh sửa khách hàng
    @GetMapping("/form-edit-khach-hang/{id}")
    public String editCustomer(@PathVariable("id") Long id, HttpServletRequest request, Model model) {
        User customer = customerService.getCustomerById(id, request);
        if (customer == null) {
            return "redirect:/admin/customer?error=CustomerNotFound";
        }

        model.addAttribute("customerDTO", customer);
        return "admin/customer/form-edit-khach-hang";
    }

    // Cập nhật thông tin khách hàng
    @PostMapping("/update")
    public String updateCustomer(@ModelAttribute("customerDTO") User customer,
                                 HttpServletRequest request,
                                 RedirectAttributes redirectAttributes) {
        try {
            customerService.updateCustomer(customer, request);
            redirectAttributes.addFlashAttribute("flag_success", true);
            redirectAttributes.addFlashAttribute("message", "Cập nhật khách hàng thành công!");
        } catch (Exception e) {
            log.error("Error updating customer: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Cập nhật khách hàng thất bại: " + e.getMessage());
        }
        return "redirect:/admin/customer";
    }

    // Hiển thị chi tiết khách hàng (trong modal)
    @GetMapping("/detail/{id}")
    @ResponseBody
    public String getCustomerDetail(@PathVariable("id") Long id, HttpServletRequest request) {
        User customer = customerService.getCustomerById(id, request);
        if (customer == null) {
            return "<p>Không tìm thấy thông tin khách hàng.</p>";
        }

        return "<p><strong>ID:</strong> " + customer.getUserId() + "</p>" +
                "<p><strong>Họ và tên:</strong> " + customer.getFullName() + "</p>" +
                "<p><strong>Số điện thoại:</strong> " + customer.getPhoneNumber() + "</p>" +
                "<p><strong>Ngày sinh:</strong> " + customer.getDateOfBirth() + "</p>" +
                "<p><strong>Giới tính:</strong> " + (customer.getGender() ? "Nam" : "Nữ") + "</p>" +
                "<p><strong>Địa chỉ:</strong> " + customer.getAddress() + "</p>" +
                "<p><strong>Email:</strong> " + customer.getEmail() + "</p>";
    }
}