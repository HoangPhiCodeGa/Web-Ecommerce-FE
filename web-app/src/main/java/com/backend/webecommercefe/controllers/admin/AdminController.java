package com.backend.webecommercefe.controllers.admin;

import com.backend.webecommercefe.dto.EditUserDTO;
import com.backend.webecommercefe.entities.Account;
import com.backend.webecommercefe.entities.Role;
import com.backend.webecommercefe.services.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller xử lý các yêu cầu liên quan đến trang quản lý tài khoản Admin.
 */
@Controller
@RequestMapping("/admin/account")
@Slf4j
public class AdminController {

    private static final int PAGE_SIZE = 10; // Số tài khoản trên mỗi trang

    @Autowired
    private AccountService accountService;

    /**
     * Hiển thị danh sách tài khoản Admin.
     *
     * @param page Trang hiện tại (mặc định là 0)
     * @param keyword Từ khóa tìm kiếm (nếu có)
     * @param request Đối tượng HttpServletRequest để lấy token từ session
     * @param model Đối tượng Model để truyền dữ liệu sang view
     * @return Tên template (admin/account/index.html)
     */


    @GetMapping
    public String index(@RequestParam(value = "page", defaultValue = "0") int page,
                        @RequestParam(value = "keyword", required = false) String keyword,
                        HttpServletRequest request,
                        Model model) {
        List<Account> accounts = accountService.getAccounts(keyword, page, PAGE_SIZE, request);
        long totalAccounts = accountService.getTotalAccounts(keyword, request);
        int totalPages = (int) Math.ceil((double) totalAccounts / PAGE_SIZE);

        log.info("Fetched accounts: {}", accounts);
        accounts.forEach(account -> log.info("Account: {}, Roles: {}", account.getUsername(), account.getRoles()));
        log.info("Total accounts: {}, Total pages: {}", totalAccounts, totalPages);

        model.addAttribute("accounts", accounts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("keyword", keyword);
        return "admin/account/index";
    }

    /**
     * Hiển thị form chỉnh sửa tài khoản.
     *
     * @param id ID của tài khoản cần chỉnh sửa
     * @param request Đối tượng HttpServletRequest để lấy token từ session
     * @param model Đối tượng Model để truyền dữ liệu sang view
     * @return Tên template (admin/account/form-edit-account.html)
     */
    @GetMapping("/edit")
    public String editAccount(@RequestParam("id") Long id, HttpServletRequest request, Model model) {
        try {
            Account account = accountService.getAccountById(id, request);
            if (account == null) {
                log.warn("Account with ID {} not found", id);
                return "redirect:/admin/account?error=AccountNotFound";
            }

            // Chuyển đổi Account thành EditUserDTO
            EditUserDTO editUserDTO = new EditUserDTO();
            editUserDTO.setId(account.getAccountId());
            editUserDTO.setUsername(account.getUsername());
            editUserDTO.setEmail(account.getEmail());
            editUserDTO.setEnabled(true); // Giả sử tài khoản luôn enabled
            editUserDTO.setUserRoles(account.getRoles());

            // Lấy danh sách tất cả quyền từ backend
            List<Role> allRoles = accountService.getAllRoles(request);
            // Thêm log để kiểm tra
            log.info("All roles for account ID {}: {}", id, allRoles);
            log.info("Account roles for account ID {}: {}", id, account.getRoles());

            // Lấy danh sách quyền chưa có của tài khoản
            List<Role> rolesNotInUser = allRoles.stream()
                    .filter(role -> !account.getRoles().stream().anyMatch(r -> r.getName().equals(role.getName())))
                    .collect(Collectors.toList());

            // Thêm log để kiểm tra rolesNotInUser
            log.info("Roles not in user for account ID {}: {}", id, rolesNotInUser);

            model.addAttribute("editUserDTO", editUserDTO);
            model.addAttribute("rolesNotInUser", rolesNotInUser);
            return "admin/account/form-edit-account";
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Token expired") || e.getMessage().contains("Invalid token")) {
                return "redirect:/account/login?error=TokenExpired";
            }
            log.error("Error fetching account with ID {}: {}", id, e.getMessage(), e);
            return "redirect:/admin/account?error=ErrorFetchingAccount";
        }
    }
    /**
     * Xử lý cập nhật tài khoản.
     *
     * @param account Đối tượng Account chứa thông tin cập nhật
     * @param request Đối tượng HttpServletRequest để lấy token từ session
     * @param redirectAttributes Đối tượng để truyền thông báo qua redirect
     * @return Chuyển hướng về trang danh sách tài khoản
     */
    @PostMapping("/update")
    public String updateAccount(@ModelAttribute Account account, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        try {
            accountService.updateAccount(account, request);
            redirectAttributes.addFlashAttribute("message", "Cập nhật tài khoản thành công!");
        } catch (Exception e) {
            log.error("Error updating account: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Cập nhật tài khoản thất bại: " + e.getMessage());
        }
        return "redirect:/admin/account";
    }

    /**
     * Xử lý thêm quyền cho tài khoản.
     *
     * @param username Username của tài khoản
     * @param roleName Tên quyền cần thêm (ví dụ: ROLE_ADMIN)
     * @param request Đối tượng HttpServletRequest để lấy token từ session
     * @param redirectAttributes Đối tượng để truyền thông báo qua redirect
     * @return Chuyển hướng về trang chỉnh sửa tài khoản
     */
    @PostMapping("/add-role")
    public String addRole(@RequestParam("username") String username,
                          @RequestParam("roleName") String roleName,
                          HttpServletRequest request,
                          RedirectAttributes redirectAttributes) {
        try {
            accountService.addRole(username, roleName, request);
            redirectAttributes.addFlashAttribute("message", "Thêm quyền thành công!");
        } catch (Exception e) {
            log.error("Error adding role: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Thêm quyền thất bại: " + e.getMessage());
        }
        Account account = accountService.getAccountByUsername(username, request);
        if (account == null) {
            return "redirect:/admin/account?error=AccountNotFound";
        }
        return "redirect:/admin/account/edit?id=" + account.getAccountId();
    }

    /**
     * Xử lý xóa quyền khỏi tài khoản.
     *
     * @param accountId ID của tài khoản
     * @param roleName Tên quyền cần xóa
     * @param username Username của tài khoản
     * @param request Đối tượng HttpServletRequest để lấy token từ session
     * @param redirectAttributes Đối tượng để truyền thông báo qua redirect
     * @return Chuyển hướng về trang chỉnh sửa tài khoản
     */
    @PostMapping("/delete-role")
    public String deleteRole(@RequestParam("accountId") Long accountId,
                             @RequestParam("roleName") String roleName,
                             @RequestParam("username") String username,
                             HttpServletRequest request,
                             RedirectAttributes redirectAttributes) {
        try {
            accountService.deleteRole(username, roleName, request);
            redirectAttributes.addFlashAttribute("message", "Xóa quyền thành công!");
        } catch (Exception e) {
            log.error("Error deleting role: {}", e.getMessage(), e);
            if (e.getMessage().contains("404 Not Found")) {
                redirectAttributes.addFlashAttribute("error", "Quyền " + roleName + " không tồn tại trong tài khoản này.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Xóa quyền thất bại: " + e.getMessage());
            }
        }
        return "redirect:/admin/account/edit?id=" + accountId;
    }
}