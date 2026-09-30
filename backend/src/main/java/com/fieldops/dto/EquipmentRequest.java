package com.fieldops.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class EquipmentRequest {

    @NotBlank
    @Size(max = 150)
    private String name;

    @NotBlank
    @Size(max = 100)
    private String type;

    @NotBlank
    @Size(max = 150)
    private String location;

    @NotBlank
    @Size(max = 50)
    private String status;

    @NotNull
    private LocalDate installationDate;

    private LocalDate nextMaintenanceDate;

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