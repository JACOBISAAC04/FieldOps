package com.fieldops.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class WorkOrderRequest {

    @NotNull
    private Long equipmentId;

    private Long engineerId;

    @NotBlank
    private String priority;

    @NotBlank
    private String description;

    private String status;

    private LocalDateTime dueDate;

    public WorkOrderRequest() {
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public Long getEngineerId() {
        return engineerId;
    }

    public String getPriority() {
        return priority;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public void setEngineerId(Long engineerId) {
        this.engineerId = engineerId;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }
}