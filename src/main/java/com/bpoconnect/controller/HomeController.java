package com.bpoconnect.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
    
    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }

    @PostMapping("/api/logout")
    @ResponseBody
    public String logout(HttpSession session) {
        session.invalidate();
        return "{\"status\":\"logged out\"}";
    }
}
