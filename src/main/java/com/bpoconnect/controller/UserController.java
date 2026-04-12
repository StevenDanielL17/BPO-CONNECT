package com.bpoconnect.controller;

import com.bpoconnect.model.User;
import com.bpoconnect.service.AuditService;
import com.bpoconnect.service.UserService;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final String SESSION_USER_ID = "AUTH_USER_ID";

    private final UserService userService;
    private final AuditService auditService;


    public UserController(UserService userService, AuditService auditService) {
        this.userService = userService;
        this.auditService = auditService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials, HttpSession session) {
        User authenticatedUser = userService.authenticate(credentials.get("email"), credentials.get("password"));
        session.setAttribute(SESSION_USER_ID, authenticatedUser.getUserId());
        auditService.log(authenticatedUser.getUserId(), null, "LOGIN", "User authenticated successfully");
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Login successful");
        response.put("user", authenticatedUser);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> payload, HttpSession session) {
        User user = userService.registerClient(payload.get("name"), payload.get("email"), payload.get("password"));
        session.setAttribute(SESSION_USER_ID, user.getUserId());
        auditService.log(user.getUserId(), null, "REGISTER", "Client account created");
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Account created successfully");
        response.put("user", user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {
        Object userId = session.getAttribute(SESSION_USER_ID);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Not authenticated"));
        }

        User user = userService.getRequiredUser(userId.toString());
        return ResponseEntity.ok(Map.of("user", user));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        Object userId = session.getAttribute(SESSION_USER_ID);
        if (userId != null) {
            auditService.log(userId.toString(), null, "LOGOUT", "User logged out");
        }
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "Logged out"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> payload) {
        String token = userService.createPasswordResetToken(payload.get("email"));
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Password reset token generated. Use it on the reset page.");
        response.put("resetToken", token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");
        String newPassword = payload.get("newPassword");
        String confirmPassword = payload.get("confirmPassword");

        if (token == null || token.isBlank() || newPassword == null || newPassword.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Token and new password are required"));
        }
        if (!newPassword.equals(confirmPassword)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Passwords do not match"));
        }

        userService.resetPassword(token, newPassword);
        return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> payload, HttpSession session) {
        Object userId = session.getAttribute(SESSION_USER_ID);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Not authenticated"));
        }

        userService.changePasswordForUser(userId.toString(), payload.get("currentPassword"), payload.get("newPassword"));
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }
}
