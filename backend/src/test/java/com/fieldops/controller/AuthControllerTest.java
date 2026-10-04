package com.fieldops.controller;

import com.fieldops.dto.LoginResponse;
import com.fieldops.entity.User;
import com.fieldops.exception.AuthenticationException;
import com.fieldops.exception.GlobalExceptionHandler;
import com.fieldops.service.AuthenticationService;
import com.fieldops.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({GlobalExceptionHandler.class, com.fieldops.config.SecurityConfig.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationService authenticationService;

    @MockitoBean
    private JwtService jwtService;

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setName("Jacob Isaac");
        user.setEmail("jacob@fieldops.com");
        user.setPasswordHash("$2a$10$hashedpassword");
        user.setRole("ENGINEER");
        user.setStatus("ACTIVE");
        return user;
    }

    @Test
    void loginReturnsToken() throws Exception {
        User user = createUser();

        when(authenticationService.authenticate(any()))
                .thenReturn(user);

        when(jwtService.generateToken(user))
                .thenReturn("test-jwt-token");

        String request = """
                {
                    "email": "jacob@fieldops.com",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-jwt-token"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.name").value("Jacob Isaac"))
                .andExpect(jsonPath("$.email").value("jacob@fieldops.com"))
                .andExpect(jsonPath("$.role").value("ENGINEER"));
    }

    @Test
    void loginRejectsInvalidCredentials() throws Exception {
        when(authenticationService.authenticate(any()))
                .thenThrow(new AuthenticationException(
                        "Invalid email or password"));

        String request = """
                {
                    "email": "jacob@fieldops.com",
                    "password": "wrongpassword"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message")
                        .value("Invalid email or password"));
    }

    @Test
    void loginRejectsInactiveUser() throws Exception {
        when(authenticationService.authenticate(any()))
                .thenThrow(new AuthenticationException(
                        "User account is not active"));

        String request = """
                {
                    "email": "jacob@fieldops.com",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message")
                        .value("User account is not active"));
    }

    @Test
    void loginRejectsInvalidRequest() throws Exception {
        String request = """
                {
                    "email": "",
                    "password": ""
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }
}