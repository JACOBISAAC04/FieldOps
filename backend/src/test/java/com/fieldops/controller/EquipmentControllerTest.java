package com.fieldops.controller;

import com.fieldops.dto.EquipmentResponse;
import com.fieldops.exception.GlobalExceptionHandler;
import com.fieldops.exception.ResourceNotFoundException;
import com.fieldops.service.EquipmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EquipmentController.class)
@Import(GlobalExceptionHandler.class)
class EquipmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EquipmentService equipmentService;

    @Test
    void getAllEquipmentReturnsOk() throws Exception {
        EquipmentResponse response = new EquipmentResponse(
                1L,
                "Generator Unit C",
                "Generator",
                "Facility C",
                "OPERATIONAL",
                LocalDate.of(2025, 3, 10),
                LocalDate.of(2026, 11, 10)
        );

        when(equipmentService.searchEquipment(null, null, null, null))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/equipment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Generator Unit C"))
                .andExpect(jsonPath("$[0].status").value("OPERATIONAL"));
    }

    @Test
    void getEquipmentByIdReturnsOk() throws Exception {
        EquipmentResponse response = new EquipmentResponse(
                1L,
                "Generator Unit C",
                "Generator",
                "Facility C",
                "OPERATIONAL",
                LocalDate.of(2025, 3, 10),
                LocalDate.of(2026, 11, 10)
        );

        when(equipmentService.getEquipmentById(1L))
                .thenReturn(response);

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
}