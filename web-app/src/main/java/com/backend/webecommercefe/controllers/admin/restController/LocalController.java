package com.backend.webecommercefe.controllers.admin.restController;

import com.backend.webecommercefe.util.Utilfunctions;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LocalController {

    private static final Logger log = LoggerFactory.getLogger(LocalController.class);

    @GetMapping("/add-local")
    public String addLocal(@RequestParam("token") String token, @RequestParam("role") String role, HttpSession session){
        try {

            Utilfunctions.WRITE_TOKEN_LOCAL(token);
            session.setAttribute("TOKEN", token);
            session.setAttribute("ROLE", role);

            session.setMaxInactiveInterval(30 * 60);
            Utilfunctions.WRITE_TOKEN_LOCAL(token);

            return "ok";
        }catch (Exception e){
            return "fail";
        }
    }

    @GetMapping("/get-local")
    public String getLocal(){
        try {
            return "true";
        }catch (Exception e){
            return "fail";
        }
    }
}
