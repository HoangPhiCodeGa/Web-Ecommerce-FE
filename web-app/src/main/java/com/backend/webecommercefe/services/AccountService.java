package com.backend.webecommercefe.services;

import com.backend.webecommercefe.entities.Account;
import com.backend.webecommercefe.entities.Role;
import com.backend.webecommercefe.entities.User;
import com.backend.webecommercefe.untils.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface AccountService {

    ApiResponse login(String username, String password);
    long getTotalAccounts(String keyword, HttpServletRequest request);
    ApiResponse register(Account account);
    List<Account> getAccounts(String keyword, int page, int size, HttpServletRequest request);
    Account getAccountById(Long id, HttpServletRequest request);
    void updateAccount(Account account, HttpServletRequest request);
    void addRole(String username, String roleName, HttpServletRequest request);
    void deleteRole(String username, String roleName, HttpServletRequest request);
    Account getAccountByUsername(String username, HttpServletRequest request);
    List<Role> getAllRoles(HttpServletRequest request);
    List<User> getAllUsers(String keyword, int page, int size, HttpServletRequest request);
    long getTotalUsers(String keyword, HttpServletRequest request);
    User getUserById(Long id, HttpServletRequest request);
    User getUserByUsername(String username, HttpServletRequest request);
    User createUser(User user, HttpServletRequest request);
    User updateUser(Long id, User user, HttpServletRequest request);
    void deleteUser(Long id, HttpServletRequest request);
}