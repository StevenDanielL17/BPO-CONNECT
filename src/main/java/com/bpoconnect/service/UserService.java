package com.bpoconnect.service;

import com.bpoconnect.model.User;
import com.bpoconnect.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean authenticate(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.isPresent() && user.get().login(password);
    }

    public User getUser(String userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }
}
