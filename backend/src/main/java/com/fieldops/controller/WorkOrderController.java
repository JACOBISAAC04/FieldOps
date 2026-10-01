package com.fieldops.controller;

import com.fieldops.dto.WorkOrderRequest;
import com.fieldops.dto.WorkOrderResponse;
import com.fieldops.service.WorkOrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/work-orders")
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    public WorkOrderController(WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }

    @GetMapping
    public ResponseEntity<List<WorkOrderResponse>> getAllWorkOrders() {
        return ResponseEntity.ok(workOrderService.getAllWorkOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkOrderResponse> getWorkOrderById(
            @PathVariable Long id) {
        return ResponseEntity.ok(workOrderService.getWorkOrderById(id));
    }

    @PostMapping
    public ResponseEntity<WorkOrderResponse> createWorkOrder(
            @Valid @RequestBody WorkOrderRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(workOrderService.createWorkOrder(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkOrderResponse> updateWorkOrder(
            @PathVariable Long id,
            @Valid @RequestBody WorkOrderRequest request) {

        return ResponseEntity.ok(
                workOrderService.updateWorkOrder(id, request)
        );
    }

    @PutMapping("/{id}/assign/{engineerId}")
    public ResponseEntity<WorkOrderResponse> assignEngineer(
            @PathVariable Long id,
            @PathVariable Long engineerId) {

        return ResponseEntity.ok(
                workOrderService.assignEngineer(id, engineerId)
        );
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<WorkOrderResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return ResponseEntity.ok(
                workOrderService.updateStatus(id, status)
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<WorkOrderResponse>> getByStatus(
            @PathVariable String status) {

        return ResponseEntity.ok(
                workOrderService.getByStatus(status)
        );
    }

    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<WorkOrderResponse>> getByPriority(
            @PathVariable String priority) {

        return ResponseEntity.ok(
                workOrderService.getByPriority(priority)
        );
    }

    @GetMapping("/equipment/{equipmentId}")
    public ResponseEntity<List<WorkOrderResponse>> getByEquipment(
            @PathVariable Long equipmentId) {

        return ResponseEntity.ok(
                workOrderService.getByEquipment(equipmentId)
        );
    }

    @GetMapping("/engineer/{engineerId}")
    public ResponseEntity<List<WorkOrderResponse>> getByEngineer(
            @PathVariable Long engineerId) {

        return ResponseEntity.ok(
                workOrderService.getByEngineer(engineerId)
        );
    }
}