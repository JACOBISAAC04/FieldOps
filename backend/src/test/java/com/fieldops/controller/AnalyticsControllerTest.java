package com.fieldops.controller;

import com.fieldops.client.AnalyticsClient;
import com.fieldops.dto.EquipmentResponse;
import com.fieldops.dto.EquipmentRiskResponse;
import com.fieldops.exception.AnalyticsServiceException;
import com.fieldops.exception.GlobalExceptionHandler;
import com.fieldops.exception.ResourceNotFoundException;
import com.fieldops.service.EquipmentService;
import com.fieldops.service.WorkOrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AnalyticsController.class)
@Import(GlobalExceptionHandler.class)
class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AnalyticsClient analyticsClient;

    @MockitoBean
    private EquipmentService equipmentService;

    @MockitoBean
    private WorkOrderService workOrderService;

    @Test
    void getEquipmentRiskReturnsRiskResponse() throws Exception {

        EquipmentResponse equipment = new EquipmentResponse();
        equipment.setId(1L);
        equipment.setStatus("ACTIVE");

        EquipmentRiskResponse risk = new EquipmentRiskResponse();
        risk.setEquipmentId(1L);
        risk.setRiskLevel("HIGH");
        risk.setMaintenanceDue(true);
        risk.setReasons(List.of("Maintenance due", "High priority work order"));

        when(equipmentService.getEquipmentById(1L))
                .thenReturn(equipment);

        when(workOrderService.hasHighPriorityActiveWorkOrder(1L))
                .thenReturn(true);

        when(analyticsClient.getEquipmentRisk(equipment, true))
                .thenReturn(risk);

        mockMvc.perform(
                get("/api/analytics/equipment/1")
                        .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.equipmentId").value(1))
        .andExpect(jsonPath("$.riskLevel").value("HIGH"))
        .andExpect(jsonPath("$.maintenanceDue").value(true))
        .andExpect(jsonPath("$.reasons[0]").value("Maintenance due"))
        .andExpect(jsonPath("$.reasons[1]").value("High priority work order"));

        verify(equipmentService).getEquipmentById(1L);
        verify(workOrderService).hasHighPriorityActiveWorkOrder(1L);
        verify(analyticsClient).getEquipmentRisk(equipment, true);
    }

    @Test
    void getEquipmentRiskReturns404WhenEquipmentDoesNotExist()
            throws Exception {

        when(equipmentService.getEquipmentById(999L))
                .thenThrow(
                        new ResourceNotFoundException(
                                "Equipment not found with id: 999"
                        )
                );

        mockMvc.perform(
                get("/api/analytics/equipment/999")
                        .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("Not Found"))
        .andExpect(
                jsonPath("$.message")
                        .value("Equipment not found with id: 999")
        );

        verify(equipmentService).getEquipmentById(999L);
        verifyNoInteractions(workOrderService, analyticsClient);
    }

    @Test
    void getEquipmentRiskReturns503WhenAnalyticsServiceIsUnavailable()
            throws Exception {

        EquipmentResponse equipment = new EquipmentResponse();
        equipment.setId(1L);
        equipment.setStatus("ACTIVE");

        when(equipmentService.getEquipmentById(1L))
                .thenReturn(equipment);

        when(workOrderService.hasHighPriorityActiveWorkOrder(1L))
                .thenReturn(false);

        when(analyticsClient.getEquipmentRisk(equipment, false))
                .thenThrow(
                        new AnalyticsServiceException(
                                "Analytics service is unavailable",
                                new RuntimeException("Connection refused")
                        )
                );

        mockMvc.perform(
                get("/api/analytics/equipment/1")
                        .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isServiceUnavailable())
        .andExpect(jsonPath("$.status").value(503))
        .andExpect(
                jsonPath("$.error")
                        .value("Analytics Service Unavailable")
        )
        .andExpect(
                jsonPath("$.message")
                        .value("Analytics service is unavailable")
        );

        verify(equipmentService).getEquipmentById(1L);
        verify(workOrderService).hasHighPriorityActiveWorkOrder(1L);
        verify(analyticsClient).getEquipmentRisk(equipment, false);
    }

    @Test
    void getEquipmentRiskPassesHighPriorityFlagToAnalyticsClient()
            throws Exception {

        EquipmentResponse equipment = new EquipmentResponse();
        equipment.setId(5L);
        equipment.setStatus("ACTIVE");

        EquipmentRiskResponse risk = new EquipmentRiskResponse();
        risk.setEquipmentId(5L);
        risk.setRiskLevel("MEDIUM");
        risk.setMaintenanceDue(false);
        risk.setReasons(List.of("No immediate risk"));

        when(equipmentService.getEquipmentById(5L))
                .thenReturn(equipment);

        when(workOrderService.hasHighPriorityActiveWorkOrder(5L))
                .thenReturn(false);

        when(analyticsClient.getEquipmentRisk(equipment, false))
                .thenReturn(risk);

        mockMvc.perform(
                get("/api/analytics/equipment/5")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.equipmentId").value(5))
        .andExpect(jsonPath("$.riskLevel").value("MEDIUM"));

        verify(analyticsClient).getEquipmentRisk(equipment, false);
    }
}