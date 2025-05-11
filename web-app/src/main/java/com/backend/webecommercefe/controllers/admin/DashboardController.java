package com.backend.webecommercefe.controllers.admin;


import com.backend.webecommercefe.services.impl.StaticService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestClient;

import java.text.DecimalFormat;

@Controller
public class DashboardController {
    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private RestClient restClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StaticService staticService;

    @GetMapping("/admin/dashboard")
    public String getAdmin(Model model){
        Long totalUsers = staticService.countUser();
        Double totalMoney = staticService.countRenue();
        DecimalFormat formatter = new DecimalFormat("#,###");
        String formattedMoney = formatter.format(totalMoney);

        Long cntOrder = staticService.countOrder();

        log.error("So tien === " + formattedMoney);

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalRenu", formattedMoney);
        model.addAttribute("totalOrder", cntOrder);
        return "/admin/dashboard/index";
    }
}
