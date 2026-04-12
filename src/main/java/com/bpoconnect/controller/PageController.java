package com.bpoconnect.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/login")
    public String login() {
        return "forward:/login.html";
    }

    @GetMapping("/signup")
    public String signup() {
        return "forward:/signup.html";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forward:/forgot-password.html";
    }

    @GetMapping("/reset-password")
    public String resetPassword() {
        return "forward:/reset-password.html";
    }

    @GetMapping("/client-portal")
    public String clientPortal() {
        return "forward:/client-portal.html";
    }

    @GetMapping("/agent-home")
    public String agentHome() {
        return "forward:/agent-home.html";
    }

    @GetMapping("/calls")
    public String calls() {
        return "forward:/calls.html";
    }

    @GetMapping("/tickets")
    public String tickets() {
        return "forward:/tickets.html";
    }

    @GetMapping("/knowledge-base")
    public String knowledgeBase() {
        return "forward:/knowledge-base.html";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "forward:/dashboard.html";
    }

    @GetMapping("/reports")
    public String reports() {
        return "forward:/reports.html";
    }

    @GetMapping("/qa")
    public String qa() {
        return "forward:/qa.html";
    }

    @GetMapping("/admin-dashboard")
    public String adminDashboard() {
        return "forward:/admin-dashboard.html";
    }
}