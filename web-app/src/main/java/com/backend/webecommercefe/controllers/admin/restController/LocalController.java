package com.backend.webecommercefe.controllers.admin.restController;

import com.backend.webecommercefe.untils.Utilfunctions;
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

//            Utilfunctions.UTIL_ROLE = role;
//            Utilfunctions.UTIL_TOKEN = token;
//
//            log.error("token add = " + token);
//            log.error("role add = " + role);
//            Utilfunctions.WRITE_TOKEN_LOCAL(token);

            session.setAttribute("TOKEN", token);
            session.setAttribute("ROLE", role);
            session.setAttribute("loggedIn", true);

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
//            String token = Utilfunctions.GET_TOKEN();
//            String role = Utilfunctions.GET_ROLE();

//            log.error("token = " + Utilfunctions.UTIL_TOKEN);
//            log.error("role = " + Utilfunctions.UTIL_ROLE);
            return "true";
        }catch (Exception e){
            return "fail";
        }
    }
}
