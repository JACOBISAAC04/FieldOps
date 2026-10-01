package com.fieldops.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class EngineerRequest {

    @NotNull
    private Long userId;

    @NotBlank
    private String specialization;

    @NotBlank
    private String location;

    @NotBlank
    private String availability;

    public EngineerRequest() {
    }

    public Long getUserId() {
        return userId;
    }

    public String getSpecialization() {
        return specialization;
    }

    public String getLocation() {
        return location;
    }

    public String getAvailability() {
        return availability;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }
}