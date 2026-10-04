package com.fieldops.service;

import com.fieldops.dto.LoginRequest;
import com.fieldops.entity.User;
import com.fieldops.exception.AuthenticationException;
import com.fieldops.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User authenticate(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthenticationException(
                        "Invalid email or password"));

        String storedPassword = user.getPasswordHash();

        if (isBcryptHash(storedPassword)) {
            if (!passwordEncoder.matches(
                    request.getPassword(),
                    storedPassword)) {
                throw new AuthenticationException(
                        "Invalid email or password");
            }
        } else {
            if (!request.getPassword().equals(storedPassword)) {
                throw new AuthenticationException(
                        "Invalid email or password");
            }

            user.setPasswordHash(
                    passwordEncoder.encode(request.getPassword())
            );

            userRepository.save(user);
        }

        if (!"ACTIVE".equals(user.getStatus())) {
            throw new AuthenticationException(
                    "User account is not active");
        }

        return user;
    }

    public void changePassword(
            Long userId,
            String currentPassword,
            String newPassword) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthenticationException(
                        "User not found"));

        if (!passwordEncoder.matches(
                currentPassword,
                user.getPasswordHash())) {
            throw new AuthenticationException(
                    "Current password is incorrect");
        }

        if (passwordEncoder.matches(
                newPassword,
                user.getPasswordHash())) {
            throw new AuthenticationException(
                    "New password must be different from current password");
        }

        user.setPasswordHash(
                passwordEncoder.encode(newPassword)
        );

        userRepository.save(user);
    }

    private boolean isBcryptHash(String password) {
        return password.startsWith("$2a$") ||
                password.startsWith("$2b$") ||
                password.startsWith("$2y$");
    }
}