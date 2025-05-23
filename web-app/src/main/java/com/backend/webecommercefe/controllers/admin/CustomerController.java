package com.backend.webecommercefe.controllers.admin;

import com.backend.webecommercefe.entities.User;
import com.backend.webecommercefe.services.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/customer")
@Slf4j
public class CustomerController {

    @Autowired
    private AccountService accountService;

    @GetMapping
    public ModelAndView index(ModelAndView model,
                              @RequestParam(value = "keyword", required = false) String keyword,
                              @RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "size", defaultValue = "10") int size,
                              HttpServletRequest request) {
        List<User> users = accountService.getAllUsers(keyword, page, size, request);

        List<User> tmp = new ArrayList<>();
        if(keyword != null && !keyword.isEmpty()){
            for (User item: users){
                if((item.getFullName() != null && item.getFullName().contains(keyword))
                        || (item.getUsername() != null && item.getUsername().contains(keyword)) )
                    tmp.add(item);

            }
            users = tmp;
        }

        log.info("Fetched users: {}", users);

        long totalUsers = accountService.getTotalUsers(keyword, request); // Sử dụng getTotalUsers từ AccountService
        int totalPages = (int) Math.ceil((double) totalUsers / size);

        log.error("dub lieu user >>> " + users);
        model.addObject("users", users);
        model.addObject("khachHangs", users);
        model.addObject("totalUsers", totalUsers);
        model.addObject("totalPages", totalPages);
        model.addObject("currentPage", page);
        model.addObject("keyword", keyword);
        model.setViewName("admin/customer/index");
        return model;
    }
}