package com.bpoconnect.controller;

import com.bpoconnect.model.User;
import com.bpoconnect.service.UserService;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> credentials) {
        boolean authenticated = userService.authenticate(credentials.get("username"), credentials.get("password"));
        return authenticated ? "Login Successful" : "Login Failed";
    }

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userService.saveUser(user);
    }
}
