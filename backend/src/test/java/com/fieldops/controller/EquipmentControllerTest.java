package com.fieldops.controller;
import com.fieldops.config.SecurityConfig;
import com.fieldops.dto.EquipmentResponse;
import com.fieldops.exception.GlobalExceptionHandler;
import com.fieldops.exception.ResourceNotFoundException;
import com.fieldops.service.EquipmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EquipmentController.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class})class EquipmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EquipmentService equipmentService;

    private EquipmentResponse createResponse() {
        return new EquipmentResponse(
                1L,
                "Generator Unit C",
                "Generator",
                "Facility C",
                "OPERATIONAL",
                LocalDate.of(2025, 3, 10),
                LocalDate.of(2026, 11, 10)
        );
    }

    @Test
    void getAllEquipmentReturnsOk() throws Exception {
        when(equipmentService.searchEquipment(null, null, null, null))
                .thenReturn(List.of(createResponse()));

        mockMvc.perform(get("/api/equipment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Generator Unit C"))
                .andExpect(jsonPath("$[0].status").value("OPERATIONAL"));
    }

    @Test
    void getAllEquipmentSupportsSearchFilters() throws Exception {
        when(equipmentService.searchEquipment(
                "OPERATIONAL",
                "Facility C",
                "Generator",
                "Generator Unit C"
        )).thenReturn(List.of(createResponse()));

        mockMvc.perform(get("/api/equipment")
                        .param("status", "OPERATIONAL")
                        .param("location", "Facility C")
                        .param("type", "Generator")
                        .param("name", "Generator Unit C"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name")
                        .value("Generator Unit C"))
                .andExpect(jsonPath("$[0].type")
                        .value("Generator"))
                .andExpect(jsonPath("$[0].location")
                        .value("Facility C"));
    }

    @Test
    void getEquipmentByIdReturnsOk() throws Exception {
        when(equipmentService.getEquipmentById(1L))
                .thenReturn(createResponse());

        mockMvc.perform(get("/api/equipment/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Generator Unit C"));
    }

    @Test
    void getEquipmentByIdReturns404WhenNotFound() throws Exception {
        when(equipmentService.getEquipmentById(9999L))
                .thenThrow(new ResourceNotFoundException(
                        "Equipment not found with id: 9999"
                ));

        mockMvc.perform(get("/api/equipment/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message")
                        .value("Equipment not found with id: 9999"));
    }

    @Test
    void createEquipmentReturnsCreated() throws Exception {
        when(equipmentService.createEquipment(any()))
                .thenReturn(createResponse());

        String request = """
                {
                    "name": "Generator Unit C",
                    "type": "Generator",
                    "location": "Facility C",
                    "status": "OPERATIONAL",
                    "installationDate": "2025-03-10",
                    "nextMaintenanceDate": "2026-11-10"
                }
                """;

        mockMvc.perform(post("/api/equipment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name")
                        .value("Generator Unit C"))
                .andExpect(jsonPath("$.type")
                        .value("Generator"))
                .andExpect(jsonPath("$.status")
                        .value("OPERATIONAL"));
    }

    @Test
    void createEquipmentRejectsInvalidRequest() throws Exception {
        String request = """
                {
                    "name": "",
                    "type": "",
                    "location": "",
                    "status": "",
                    "installationDate": null,
                    "nextMaintenanceDate": null
                }
                """;

        mockMvc.perform(post("/api/equipment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateEquipmentReturnsOk() throws Exception {
        EquipmentResponse response = new EquipmentResponse(
                1L,
                "Updated Generator",
                "Generator",
                "Facility D",
                "MAINTENANCE_REQUIRED",
                LocalDate.of(2025, 3, 10),
                LocalDate.of(2026, 12, 10)
        );

        when(equipmentService.updateEquipment(eq(1L), any()))
                .thenReturn(response);

        String request = """
                {
                    "name": "Updated Generator",
                    "type": "Generator",
                    "location": "Facility D",
                    "status": "MAINTENANCE_REQUIRED",
                    "installationDate": "2025-03-10",
                    "nextMaintenanceDate": "2026-12-10"
                }
                """;

        mockMvc.perform(put("/api/equipment/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name")
                        .value("Updated Generator"))
                .andExpect(jsonPath("$.location")
                        .value("Facility D"))
                .andExpect(jsonPath("$.status")
                        .value("MAINTENANCE_REQUIRED"));
    }

    @Test
    void deactivateEquipmentReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/equipment/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getMaintenanceDueEquipmentReturnsOk() throws Exception {
        EquipmentResponse response = new EquipmentResponse(
                2L,
                "Pump Unit D",
                "Pump",
                "Facility D",
                "MAINTENANCE_REQUIRED",
                LocalDate.of(2024, 5, 10),
                LocalDate.of(2026, 9, 30)
        );

        when(equipmentService.getMaintenanceDueEquipment())
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/equipment/maintenance-due"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].name")
                        .value("Pump Unit D"))
                .andExpect(jsonPath("$[0].status")
                        .value("MAINTENANCE_REQUIRED"));
    }
}