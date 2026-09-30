package com.fieldops.controller;

import com.fieldops.dto.EquipmentRequest;
import com.fieldops.dto.EquipmentResponse;
import com.fieldops.service.EquipmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {

    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @GetMapping
    public ResponseEntity<List<EquipmentResponse>> getAllEquipment(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String name) {

        return ResponseEntity.ok(
                equipmentService.searchEquipment(status, location, type, name)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipmentResponse> getEquipmentById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                equipmentService.getEquipmentById(id)
        );
    }

    @PostMapping
    public ResponseEntity<EquipmentResponse> createEquipment(
            @Valid @RequestBody EquipmentRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(equipmentService.createEquipment(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EquipmentResponse> updateEquipment(
            @PathVariable Long id,
            @Valid @RequestBody EquipmentRequest request) {

        return ResponseEntity.ok(
                equipmentService.updateEquipment(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateEquipment(
            @PathVariable Long id) {

        equipmentService.deactivateEquipment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/maintenance-due")
    public ResponseEntity<List<EquipmentResponse>> getMaintenanceDueEquipment() {

        return ResponseEntity.ok(
                equipmentService.getMaintenanceDueEquipment()
        );
    }
}