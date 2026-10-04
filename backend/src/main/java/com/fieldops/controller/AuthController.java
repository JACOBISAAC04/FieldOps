package com.fieldops.controller;

import com.fieldops.dto.LoginRequest;
import com.fieldops.dto.LoginResponse;
import com.fieldops.entity.User;
import com.fieldops.service.AuthenticationService;
import com.fieldops.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fieldops.dto.ChangePasswordRequest;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    public AuthController(
            AuthenticationService authenticationService,
            JwtService jwtService) {
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        User user = authenticationService.authenticate(request);

        String token = jwtService.generateToken(user);

        LoginResponse response = new LoginResponse(
                token,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );

        return ResponseEntity.ok(response);
    }
    @PutMapping("/password")
public ResponseEntity<Void> changePassword(
        @Valid @RequestBody ChangePasswordRequest request,
        Authentication authentication) {

    User user = (User) authentication.getPrincipal();

    authenticationService.changePassword(
            user.getId(),
            request.getCurrentPassword(),
            request.getNewPassword()
    );

    return ResponseEntity.noContent().build();
}
}