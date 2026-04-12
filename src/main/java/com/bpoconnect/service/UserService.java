package com.bpoconnect.service;

import com.bpoconnect.model.ClientUser;
import com.bpoconnect.model.User;
import com.bpoconnect.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User authenticate(String email, String password) {
        User user = userRepository.findByEmailIgnoreCase(normalizeEmail(email))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!user.login(password)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        userRepository.save(user);
        return user;
    }

    public User registerClient(String fullName, String email, String password) {
        String normalizedEmail = normalizeEmail(email);
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with this email already exists");
        }

        String username = (fullName == null || fullName.trim().isEmpty())
                ? normalizedEmail.substring(0, normalizedEmail.indexOf('@'))
                : fullName.trim();
        String userId = "U-" + UUID.randomUUID().toString().substring(0, 8);
        ClientUser user = new ClientUser(userId, username, normalizedEmail, password);
        return userRepository.save(user);
    }

    public User getUser(String userId) {
        return userRepository.findById(Objects.requireNonNull(userId, "userId")).orElse(null);
    }

    public User getRequiredUser(String userId) {
        return userRepository.findById(Objects.requireNonNull(userId, "userId"))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session is invalid"));
    }

    public User saveUser(User user) {
        return userRepository.save(Objects.requireNonNull(user, "user"));
    }

    public String createPasswordResetToken(String email) {
        User user = userRepository.findByEmailIgnoreCase(normalizeEmail(email))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No account found for that email"));

        String token = UUID.randomUUID().toString();
        user.startPasswordReset(token, LocalDateTime.now().plusMinutes(30));
        userRepository.save(user);
        return token;
    }

    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid password reset token"));

        if (user.getPasswordResetTokenExpiry() == null || user.getPasswordResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password reset token has expired");
        }

        user.setPassword(newPassword);
        user.clearPasswordReset();
        userRepository.save(user);
    }

    public void changePassword(String email, String currentPassword, String newPassword) {
        User user = authenticate(email, currentPassword);
        user.setPassword(newPassword);
        user.clearPasswordReset();
        userRepository.save(user);
    }

    public void changePasswordForUser(String userId, String currentPassword, String newPassword) {
        User user = getRequiredUser(userId);
        if (!user.login(currentPassword)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Current password is incorrect");
        }

        user.setPassword(newPassword);
        user.clearPasswordReset();
        userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(normalizeEmail(email));
    }

    private String normalizeEmail(String email) {
        if (email == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }
}
