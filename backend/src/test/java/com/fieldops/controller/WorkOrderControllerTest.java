package com.fieldops.controller;
import com.fieldops.config.SecurityConfig;

import com.fieldops.dto.WorkOrderResponse;
import com.fieldops.exception.GlobalExceptionHandler;
import com.fieldops.exception.ResourceNotFoundException;
import com.fieldops.service.WorkOrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
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

@WebMvcTest(WorkOrderController.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
class WorkOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WorkOrderService workOrderService;

    private WorkOrderResponse createResponse() {
        WorkOrderResponse response = new WorkOrderResponse();
        response.setId(1L);
        response.setEquipmentId(10L);
        response.setEngineerId(5L);
        response.setPriority("HIGH");
        response.setDescription("Inspect hydraulic system");
        response.setStatus("OPEN");
        return response;
    }

    @Test
    void getAllWorkOrders() throws Exception {
        when(workOrderService.getAllWorkOrders())
                .thenReturn(List.of(createResponse()));

        mockMvc.perform(get("/api/work-orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].equipmentId").value(10))
                .andExpect(jsonPath("$[0].priority").value("HIGH"))
                .andExpect(jsonPath("$[0].status").value("OPEN"));
    }

    @Test
    void getWorkOrderById() throws Exception {
        when(workOrderService.getWorkOrderById(1L))
                .thenReturn(createResponse());

        mockMvc.perform(get("/api/work-orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.equipmentId").value(10))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void getWorkOrderByIdReturns404WhenNotFound() throws Exception {
        when(workOrderService.getWorkOrderById(999L))
                .thenThrow(new ResourceNotFoundException(
                        "Work order not found with id: 999"
                ));

        mockMvc.perform(get("/api/work-orders/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message")
                        .value("Work order not found with id: 999"));
    }

    @Test
    void createWorkOrder() throws Exception {
        when(workOrderService.createWorkOrder(any()))
                .thenReturn(createResponse());

        String request = """
                {
                    "equipmentId": 10,
                    "engineerId": 5,
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
    void createWorkOrderRejectsInvalidRequest() throws Exception {
        String request = """
                {
                    "equipmentId": null,
                    "priority": "",
                    "description": ""
                }
                """;

        mockMvc.perform(post("/api/work-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createWorkOrderAllowsOptionalEngineerAndDueDate() throws Exception {
        when(workOrderService.createWorkOrder(any()))
                .thenReturn(createResponse());

        String request = """
                {
                    "equipmentId": 10,
                    "priority": "MEDIUM",
                    "description": "Routine inspection"
                }
                """;

        mockMvc.perform(post("/api/work-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated());
    }

    @Test
        void updateWorkOrder() throws Exception {
        WorkOrderResponse response = createResponse();
        response.setPriority("MEDIUM");
        response.setDescription("Updated hydraulic inspection");

        when(workOrderService.updateWorkOrder(any(Long.class), any()))
                .thenReturn(response);

        String request = """
                {
                        "equipmentId": 10,
                        "engineerId": 5,
                        "priority": "MEDIUM",
                        "description": "Updated hydraulic inspection"
                }
                """;

        mockMvc.perform(put("/api/work-orders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.priority").value("MEDIUM"))
                .andExpect(jsonPath("$.description")
                        .value("Updated hydraulic inspection"));
        }

    @Test
    void assignEngineer() throws Exception {
        WorkOrderResponse response = createResponse();
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
        WorkOrderResponse response = createResponse();
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
        void updateStatusRejectsInvalidStatus() throws Exception {
        when(workOrderService.updateStatus(1L, "INVALID_STATUS"))
                .thenThrow(new IllegalStateException(
                        "Invalid work order transition"
                ));

        mockMvc.perform(put("/api/work-orders/1/status")
                        .param("status", "INVALID_STATUS"))
                .andExpect(status().isBadRequest());
        }

    @Test
    void getByStatus() throws Exception {
        WorkOrderResponse response = createResponse();
        response.setStatus("OPEN");

        when(workOrderService.getByStatus("OPEN"))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/work-orders/status/OPEN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("OPEN"));
    }
    @Test
        void getByPriority() throws Exception {
        WorkOrderResponse response = createResponse();
        response.setPriority("HIGH");

        when(workOrderService.getByPriority("HIGH"))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/work-orders/priority/HIGH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].priority").value("HIGH"))
                .andExpect(jsonPath("$[0].id").value(1));
        }
        @Test
        void getByEquipment() throws Exception {
        WorkOrderResponse response = createResponse();
        response.setEquipmentId(10L);

        when(workOrderService.getByEquipment(10L))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/work-orders/equipment/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].equipmentId").value(10))
                .andExpect(jsonPath("$[0].id").value(1));
        }
        @Test
        void getMaintenanceHistory() throws Exception {
        WorkOrderResponse response = createResponse();
        response.setStatus("COMPLETED");

        when(workOrderService.getMaintenanceHistory(10L))
                .thenReturn(List.of(response));

        mockMvc.perform(
                get("/api/work-orders/equipment/10/history")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("COMPLETED"));
        }
        @Test
        void getByEngineer() throws Exception {
        WorkOrderResponse response = createResponse();
        response.setEngineerId(5L);

        when(workOrderService.getByEngineer(5L))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/work-orders/engineer/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].engineerId").value(5))
                .andExpect(jsonPath("$[0].id").value(1));
        }

    @Test
    void assignEngineerReturns404WhenWorkOrderNotFound() throws Exception {
        when(workOrderService.assignEngineer(999L, 5L))
                .thenThrow(new ResourceNotFoundException(
                        "Work order not found with id: 999"
                ));

        mockMvc.perform(put("/api/work-orders/999/assign/5"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void updateStatusReturns404WhenWorkOrderNotFound() throws Exception {
        when(workOrderService.updateStatus(999L, "IN_PROGRESS"))
                .thenThrow(new ResourceNotFoundException(
                        "Work order not found with id: 999"
                ));

        mockMvc.perform(put("/api/work-orders/999/status")
                        .param("status", "IN_PROGRESS"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}