package com.fieldops.dto;

import java.time.LocalDate;

public class EquipmentResponse {

    private Long id;
    private String name;
    private String type;
    private String location;
    private String status;
    private LocalDate installationDate;
    private LocalDate nextMaintenanceDate;

    public EquipmentResponse() {
    }

    public EquipmentResponse(
            Long id,
            String name,
            String type,
            String location,
            String status,
            LocalDate installationDate,
            LocalDate nextMaintenanceDate) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.location = location;
        this.status = status;
        this.installationDate = installationDate;
        this.nextMaintenanceDate = nextMaintenanceDate;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getLocation() {
        return location;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getInstallationDate() {
        return installationDate;
    }

    public LocalDate getNextMaintenanceDate() {
        return nextMaintenanceDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setInstallationDate(LocalDate installationDate) {
        this.installationDate = installationDate;
    }

    public void setNextMaintenanceDate(LocalDate nextMaintenanceDate) {
        this.nextMaintenanceDate = nextMaintenanceDate;
    }
}