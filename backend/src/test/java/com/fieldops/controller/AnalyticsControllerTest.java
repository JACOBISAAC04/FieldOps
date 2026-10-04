package com.fieldops.controller;
import com.fieldops.config.SecurityConfig;
import com.fieldops.client.AnalyticsClient;
import com.fieldops.dto.EquipmentResponse;
import com.fieldops.dto.EquipmentRiskResponse;
import com.fieldops.dto.WorkOrderAnalyticsSummary;
import com.fieldops.exception.AnalyticsServiceException;
import com.fieldops.exception.GlobalExceptionHandler;
import com.fieldops.exception.ResourceNotFoundException;
import com.fieldops.service.EquipmentService;
import com.fieldops.service.WorkOrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AnalyticsController.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
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

        EquipmentResponse equipment = createEquipment(1L);

        WorkOrderAnalyticsSummary summary =
                new WorkOrderAnalyticsSummary(
                        4,
                        2,
                        1,
                        12
                );

        EquipmentRiskResponse risk = new EquipmentRiskResponse();
        risk.setEquipmentId(1L);
        risk.setRiskLevel("HIGH");
        risk.setMaintenanceDue(true);
        risk.setReasons(
                java.util.List.of(
                        "Maintenance due",
                        "High priority work order"
                )
        );

        when(equipmentService.getEquipmentById(1L))
                .thenReturn(equipment);

        when(workOrderService.getAnalyticsSummary(1L))
                .thenReturn(summary);

        when(analyticsClient.getEquipmentRisk(any()))
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
        verify(workOrderService).getAnalyticsSummary(1L);
        verify(analyticsClient).getEquipmentRisk(any());
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

        EquipmentResponse equipment = createEquipment(1L);

        WorkOrderAnalyticsSummary summary =
                new WorkOrderAnalyticsSummary(
                        2,
                        1,
                        0,
                        5
                );

        when(equipmentService.getEquipmentById(1L))
                .thenReturn(equipment);

        when(workOrderService.getAnalyticsSummary(1L))
                .thenReturn(summary);

        when(analyticsClient.getEquipmentRisk(any()))
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
        verify(workOrderService).getAnalyticsSummary(1L);
        verify(analyticsClient).getEquipmentRisk(any());
    }

    @Test
    void getEquipmentRiskPassesWorkOrderSummaryToAnalyticsClient()
            throws Exception {

        EquipmentResponse equipment = createEquipment(5L);

        WorkOrderAnalyticsSummary summary =
                new WorkOrderAnalyticsSummary(
                        7,
                        3,
                        2,
                        15
                );

        EquipmentRiskResponse risk = new EquipmentRiskResponse();
        risk.setEquipmentId(5L);
        risk.setRiskLevel("MEDIUM");
        risk.setMaintenanceDue(false);
        risk.setReasons(
                java.util.List.of("No immediate risk")
        );

        when(equipmentService.getEquipmentById(5L))
                .thenReturn(equipment);

        when(workOrderService.getAnalyticsSummary(5L))
                .thenReturn(summary);

        when(analyticsClient.getEquipmentRisk(any()))
                .thenReturn(risk);

        mockMvc.perform(
                get("/api/analytics/equipment/5")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.equipmentId").value(5))
        .andExpect(jsonPath("$.riskLevel").value("MEDIUM"));

        verify(equipmentService).getEquipmentById(5L);
        verify(workOrderService).getAnalyticsSummary(5L);
        verify(analyticsClient).getEquipmentRisk(any());
    }

    private EquipmentResponse createEquipment(Long id) {
        EquipmentResponse equipment = new EquipmentResponse();

        equipment.setId(id);
        equipment.setStatus("ACTIVE");
        equipment.setInstallationDate(
                java.time.LocalDate.of(2024, 1, 15)
        );
        equipment.setNextMaintenanceDate(
                java.time.LocalDate.of(2026, 10, 10)
        );

        return equipment;
    }
}