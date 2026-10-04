package com.fieldops.controller;
import com.fieldops.config.SecurityConfig;
import com.fieldops.dto.EngineerResponse;
import com.fieldops.exception.GlobalExceptionHandler;
import com.fieldops.exception.ResourceNotFoundException;
import com.fieldops.service.EngineerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EngineerController.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
class EngineerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EngineerService engineerService;

    private EngineerResponse createResponse() {
        EngineerResponse response = new EngineerResponse();
        response.setId(10L);
        response.setUserId(1L);
        response.setName("John Engineer");
        response.setEmail("john@fieldops.com");
        response.setSpecialization("Mechanical");
        response.setLocation("Kerala");
        response.setAvailability("AVAILABLE");
        return response;
    }

    @Test
    void getAllEngineersReturnsOk() throws Exception {
        when(engineerService.getAllEngineers())
                .thenReturn(List.of(createResponse()));

        mockMvc.perform(get("/api/engineers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].name").value("John Engineer"))
                .andExpect(jsonPath("$[0].specialization").value("Mechanical"))
                .andExpect(jsonPath("$[0].location").value("Kerala"))
                .andExpect(jsonPath("$[0].availability").value("AVAILABLE"));
    }

    @Test
    void getEngineerByIdReturnsOk() throws Exception {
        when(engineerService.getEngineerById(10L))
                .thenReturn(createResponse());

        mockMvc.perform(get("/api/engineers/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("John Engineer"))
                .andExpect(jsonPath("$.email").value("john@fieldops.com"))
                .andExpect(jsonPath("$.specialization").value("Mechanical"));
    }

    @Test
    void getEngineerByIdReturns404WhenNotFound() throws Exception {
        when(engineerService.getEngineerById(999L))
                .thenThrow(new ResourceNotFoundException(
                        "Engineer not found with id: 999"
                ));

        mockMvc.perform(get("/api/engineers/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message")
                        .value("Engineer not found with id: 999"));
    }

    @Test
    void createEngineerReturnsCreated() throws Exception {
        when(engineerService.createEngineer(any()))
                .thenReturn(createResponse());

        String request = """
                {
                    "userId": 1,
                    "specialization": "Mechanical",
                    "location": "Kerala",
                    "availability": "AVAILABLE"
                }
                """;

        mockMvc.perform(post("/api/engineers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.name").value("John Engineer"))
                .andExpect(jsonPath("$.specialization").value("Mechanical"))
                .andExpect(jsonPath("$.location").value("Kerala"))
                .andExpect(jsonPath("$.availability").value("AVAILABLE"));
    }

    @Test
    void createEngineerRejectsInvalidRequest() throws Exception {
        String request = """
                {
                    "userId": null,
                    "specialization": "",
                    "location": "",
                    "availability": ""
                }
                """;

        mockMvc.perform(post("/api/engineers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateEngineerReturnsOk() throws Exception {
        EngineerResponse response = createResponse();
        response.setSpecialization("Electrical");
        response.setLocation("Tamil Nadu");
        response.setAvailability("BUSY");

        when(engineerService.updateEngineer(any(Long.class), any()))
                .thenReturn(response);

        String request = """
                {
                    "userId": 1,
                    "specialization": "Electrical",
                    "location": "Tamil Nadu",
                    "availability": "BUSY"
                }
                """;

        mockMvc.perform(put("/api/engineers/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.specialization").value("Electrical"))
                .andExpect(jsonPath("$.location").value("Tamil Nadu"))
                .andExpect(jsonPath("$.availability").value("BUSY"));
    }

    @Test
    void getBySpecializationReturnsOk() throws Exception {
        when(engineerService.getBySpecialization("Mechanical"))
                .thenReturn(List.of(createResponse()));

        mockMvc.perform(
                get("/api/engineers/specialization/Mechanical")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].specialization")
                        .value("Mechanical"));
    }

    @Test
    void getByLocationReturnsOk() throws Exception {
        when(engineerService.getByLocation("Kerala"))
                .thenReturn(List.of(createResponse()));

        mockMvc.perform(
                get("/api/engineers/location/Kerala")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].location")
                        .value("Kerala"));
    }

    @Test
    void getByAvailabilityReturnsOk() throws Exception {
        when(engineerService.getByAvailability("AVAILABLE"))
                .thenReturn(List.of(createResponse()));

        mockMvc.perform(
                get("/api/engineers/availability/AVAILABLE")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].availability")
                        .value("AVAILABLE"));
    }
}