package com.fieldops.controller;

import com.fieldops.client.AnalyticsClient;
import com.fieldops.dto.EquipmentAnalyticsInput;
import com.fieldops.dto.EquipmentResponse;
import com.fieldops.dto.EquipmentRiskResponse;
import com.fieldops.dto.WorkOrderAnalyticsSummary;
import com.fieldops.service.EquipmentService;
import com.fieldops.service.WorkOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsClient analyticsClient;
    private final EquipmentService equipmentService;
    private final WorkOrderService workOrderService;

    public AnalyticsController(
            AnalyticsClient analyticsClient,
            EquipmentService equipmentService,
            WorkOrderService workOrderService) {
        this.analyticsClient = analyticsClient;
        this.equipmentService = equipmentService;
        this.workOrderService = workOrderService;
    }

    @GetMapping("/equipment/{id}")
    public ResponseEntity<EquipmentRiskResponse> getEquipmentRisk(
            @PathVariable Long id) {

        EquipmentResponse equipment =
                equipmentService.getEquipmentById(id);

        WorkOrderAnalyticsSummary summary =
                workOrderService.getAnalyticsSummary(id);

        EquipmentAnalyticsInput input =
                new EquipmentAnalyticsInput();

        input.setEquipmentId(equipment.getId());
        input.setInstallationDate(equipment.getInstallationDate());
        input.setNextMaintenanceDate(equipment.getNextMaintenanceDate());
        input.setStatus(equipment.getStatus());
        input.setOpenWorkOrders(summary.getOpenWorkOrders());
        input.setOverdueWorkOrders(summary.getOverdueWorkOrders());
        input.setHighPriorityWorkOrders(summary.getHighPriorityWorkOrders());
        input.setCompletedWorkOrders(summary.getCompletedWorkOrders());

        EquipmentRiskResponse risk =
                analyticsClient.getEquipmentRisk(input);

        return ResponseEntity.ok(risk);
    }
}