package com.fieldops.controller;

import com.fieldops.dto.EngineerRequest;
import com.fieldops.dto.EngineerResponse;
import com.fieldops.service.EngineerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/engineers")
public class EngineerController {

    private final EngineerService engineerService;

    public EngineerController(EngineerService engineerService) {
        this.engineerService = engineerService;
    }

    @GetMapping
    public ResponseEntity<List<EngineerResponse>> getAllEngineers() {
        return ResponseEntity.ok(engineerService.getAllEngineers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EngineerResponse> getEngineerById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                engineerService.getEngineerById(id)
        );
    }

    @PostMapping
    public ResponseEntity<EngineerResponse> createEngineer(
            @Valid @RequestBody EngineerRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(engineerService.createEngineer(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EngineerResponse> updateEngineer(
            @PathVariable Long id,
            @Valid @RequestBody EngineerRequest request) {

        return ResponseEntity.ok(
                engineerService.updateEngineer(id, request)
        );
    }

    @GetMapping("/specialization/{specialization}")
    public ResponseEntity<List<EngineerResponse>> getBySpecialization(
            @PathVariable String specialization) {

        return ResponseEntity.ok(
                engineerService.getBySpecialization(specialization)
        );
    }

    @GetMapping("/location/{location}")
    public ResponseEntity<List<EngineerResponse>> getByLocation(
            @PathVariable String location) {

        return ResponseEntity.ok(
                engineerService.getByLocation(location)
        );
    }

    @GetMapping("/availability/{availability}")
    public ResponseEntity<List<EngineerResponse>> getByAvailability(
            @PathVariable String availability) {

        return ResponseEntity.ok(
                engineerService.getByAvailability(availability)
        );
    }
}