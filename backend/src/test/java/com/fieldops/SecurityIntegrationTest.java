package com.fieldops;


import com.fieldops.entity.User;
import com.fieldops.repository.UserRepository;
import com.fieldops.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("production")

class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    

    @Test
    void unauthenticatedRequestIsRejected() throws Exception {
        mockMvc.perform(get("/api/equipment"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticatedEngineerCanAccessEquipment() throws Exception {
        User user = findEngineer();

        String token = jwtService.generateToken(user);

        mockMvc.perform(
                get("/api/equipment")
                        .header("Authorization", "Bearer " + token)
        ).andExpect(status().isOk());
    }

    @Test
    void engineerCannotAccessAdminUsersEndpoint() throws Exception {
        User user = findEngineer();

        String token = jwtService.generateToken(user);

        mockMvc.perform(
                get("/api/users")
                        .header("Authorization", "Bearer " + token)
        ).andExpect(status().isForbidden());
    }

    private User findEngineer() {
        return userRepository.findByEmail("jacob@fieldops.local")
                .orElseThrow();
    }
}