package com.shopcart.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.shopcart.entities.User;
import com.shopcart.exceptions.UserNotFound;
import com.shopcart.exceptions.UserNotFoundByUsername;
import com.shopcart.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> getAllUser() {
        return this.userRepository.findAll();
    }

    public User getUserById(UUID id) {
        return this.userRepository.findById(id)
                .orElseThrow(() -> new UserNotFound(id));
    }

    public User getUserByUsername(String username) {
        return this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundByUsername(username));
    }

    public void changeRefreshToken(String username, String refreshToken) {
        User userChange = this.getUserByUsername(username);
        if (userChange != null) {
            userChange.setRefreshToken(refreshToken);
            this.userRepository.save(userChange);
        }
    }
}
