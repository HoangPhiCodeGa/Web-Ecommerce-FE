package com.backend.webecommercefe.services;

import com.backend.webecommercefe.entities.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface CustomerService {
    List<User> getCustomers(String keyword, int pageNo, int pageSize, HttpServletRequest request);
    long getTotalCustomers(String keyword, HttpServletRequest request);
    User getCustomerById(Long id, HttpServletRequest request);
    void updateCustomer(User user, HttpServletRequest request);
}