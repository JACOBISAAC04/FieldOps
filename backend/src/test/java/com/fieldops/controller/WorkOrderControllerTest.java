package com.fieldops.controller;

import com.fieldops.dto.WorkOrderResponse;
import com.fieldops.service.WorkOrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorkOrderController.class)
class WorkOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WorkOrderService workOrderService;

    @Test
    void getAllWorkOrders() throws Exception {
        WorkOrderResponse response = new WorkOrderResponse();
        response.setId(1L);
        response.setEquipmentId(10L);
        response.setPriority("HIGH");
        response.setDescription("Inspect hydraulic system");
        response.setStatus("OPEN");

        when(workOrderService.getAllWorkOrders())
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/work-orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].equipmentId").value(10))
                .andExpect(jsonPath("$[0].priority").value("HIGH"))
                .andExpect(jsonPath("$[0].status").value("OPEN"));
    }

    @Test
    void getWorkOrderById() throws Exception {
        WorkOrderResponse response = new WorkOrderResponse();
        response.setId(1L);
        response.setEquipmentId(10L);
        response.setPriority("MEDIUM");
        response.setDescription("Routine maintenance");
        response.setStatus("ASSIGNED");

        when(workOrderService.getWorkOrderById(1L))
                .thenReturn(response);

        mockMvc.perform(get("/api/work-orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.equipmentId").value(10))
                .andExpect(jsonPath("$.status").value("ASSIGNED"));
    }

    @Test
    void createWorkOrder() throws Exception {
        WorkOrderResponse response = new WorkOrderResponse();
        response.setId(1L);
        response.setEquipmentId(10L);
        response.setPriority("HIGH");
        response.setDescription("Inspect engine");
        response.setStatus("OPEN");

        when(workOrderService.createWorkOrder(any()))
                .thenReturn(response);

        String request = """
                {
                    "equipmentId": 10,
                    "priority": "HIGH",
                    "description": "Inspect engine"
                }
                """;

        mockMvc.perform(post("/api/work-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.equipmentId").value(10))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void assignEngineer() throws Exception {
        WorkOrderResponse response = new WorkOrderResponse();
        response.setId(1L);
        response.setEquipmentId(10L);
        response.setEngineerId(5L);
        response.setPriority("HIGH");
        response.setDescription("Repair pump");
        response.setStatus("ASSIGNED");

        when(workOrderService.assignEngineer(1L, 5L))
                .thenReturn(response);

        mockMvc.perform(put("/api/work-orders/1/assign/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.engineerId").value(5))
                .andExpect(jsonPath("$.status").value("ASSIGNED"));
    }

    @Test
    void updateStatus() throws Exception {
        WorkOrderResponse response = new WorkOrderResponse();
        response.setId(1L);
        response.setEquipmentId(10L);
        response.setPriority("HIGH");
        response.setDescription("Repair pump");
        response.setStatus("IN_PROGRESS");

        when(workOrderService.updateStatus(1L, "IN_PROGRESS"))
                .thenReturn(response);

        mockMvc.perform(put("/api/work-orders/1/status")
                        .param("status", "IN_PROGRESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void getByStatus() throws Exception {
        WorkOrderResponse response = new WorkOrderResponse();
        response.setId(1L);
        response.setStatus("OPEN");

        when(workOrderService.getByStatus("OPEN"))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/work-orders/status/OPEN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("OPEN"));
    }
}