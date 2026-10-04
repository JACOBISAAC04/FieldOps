package com.fieldops.controller;
import com.fieldops.config.SecurityConfig;
import com.fieldops.dto.DashboardResponse;
import com.fieldops.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fieldops.exception.GlobalExceptionHandler;

@WebMvcTest(DashboardController.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DashboardService dashboardService;

    @Test
    void getSummaryReturnsDashboardSummary() throws Exception {
        DashboardResponse response = new DashboardResponse();

        response.setTotalEquipment(20);
        response.setOperationalEquipment(14);
        response.setMaintenanceRequiredEquipment(3);
        response.setDeactivatedEquipment(3);
        response.setTotalWorkOrders(15);
        response.setOpenWorkOrders(8);
        response.setHighPriorityWorkOrders(4);
        response.setAvailableEngineers(5);
        response.setBusyEngineers(3);
        response.setUnavailableEngineers(2);
        response.setMaintenanceDueEquipment(3);

        when(dashboardService.getSummary())
                .thenReturn(response);

        mockMvc.perform(get("/api/dashboard/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEquipment").value(20))
                .andExpect(jsonPath("$.operationalEquipment").value(14))
                .andExpect(jsonPath("$.maintenanceRequiredEquipment").value(3))
                .andExpect(jsonPath("$.deactivatedEquipment").value(3))
                .andExpect(jsonPath("$.totalWorkOrders").value(15))
                .andExpect(jsonPath("$.openWorkOrders").value(8))
                .andExpect(jsonPath("$.highPriorityWorkOrders").value(4))
                .andExpect(jsonPath("$.availableEngineers").value(5))
                .andExpect(jsonPath("$.busyEngineers").value(3))
                .andExpect(jsonPath("$.unavailableEngineers").value(2))
                .andExpect(jsonPath("$.maintenanceDueEquipment").value(3));
    }
}