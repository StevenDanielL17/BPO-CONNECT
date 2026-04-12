package com.bpoconnect.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping({"/login", "/login.html"})
    public String login() {
        return "forward:/login.html";
    }

    @GetMapping({"/signup", "/signup.html"})
    public String signup() {
        return "forward:/signup.html";
    }

    @GetMapping({"/forgot-password", "/forgot-password.html"})
    public String forgotPassword() {
        return "forward:/forgot-password.html";
    }

    @GetMapping({"/reset-password", "/reset-password.html"})
    public String resetPassword() {
        return "forward:/reset-password.html";
    }

    @GetMapping({"/client-portal", "/client-portal.html"})
    public String clientPortal() {
        return "forward:/client-portal.html";
    }

    @GetMapping({"/agent-home", "/agent-home.html"})
    public String agentHome() {
        return "forward:/agent-home.html";
    }

    @GetMapping({"/calls", "/calls.html"})
    public String calls() {
        return "forward:/calls.html";
    }

    @GetMapping({"/tickets", "/tickets.html"})
    public String tickets() {
        return "forward:/tickets.html";
    }

    @GetMapping({"/knowledge-base", "/knowledge-base.html"})
    public String knowledgeBase() {
        return "forward:/knowledge-base.html";
    }

    @GetMapping({"/dashboard", "/dashboard.html"})
    public String dashboard() {
        return "forward:/dashboard.html";
    }

    @GetMapping({"/reports", "/reports.html"})
    public String reports() {
        return "forward:/reports.html";
    }

    @GetMapping({"/qa", "/qa.html"})
    public String qa() {
        return "forward:/qa.html";
    }

    @GetMapping({"/admin-dashboard", "/admin-dashboard.html"})
    public String adminDashboard() {
        return "forward:/admin-dashboard.html";
    }
}