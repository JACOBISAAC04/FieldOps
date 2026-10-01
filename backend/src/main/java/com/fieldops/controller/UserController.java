package com.fieldops.controller;

import com.fieldops.dto.UserResponse;
import com.fieldops.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/available-engineers")
    public ResponseEntity<List<UserResponse>> getAvailableEngineers() {
        return ResponseEntity.ok(userService.getAvailableEngineers());
    }
}