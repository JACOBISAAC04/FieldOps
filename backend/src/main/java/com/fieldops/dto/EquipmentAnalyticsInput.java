package com.fieldops.dto;

import java.time.LocalDate;

public class EquipmentAnalyticsInput {

    private Long equipmentId;
    private LocalDate installationDate;
    private LocalDate nextMaintenanceDate;
    private String status;
    private int openWorkOrders;
    private int overdueWorkOrders;
    private int highPriorityWorkOrders;
    private int completedWorkOrders;

    public EquipmentAnalyticsInput() {
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public LocalDate getInstallationDate() {
        return installationDate;
    }

    public LocalDate getNextMaintenanceDate() {
        return nextMaintenanceDate;
    }

    public String getStatus() {
        return status;
    }

    public int getOpenWorkOrders() {
        return openWorkOrders;
    }

    public int getOverdueWorkOrders() {
        return overdueWorkOrders;
    }

    public int getHighPriorityWorkOrders() {
        return highPriorityWorkOrders;
    }

    public int getCompletedWorkOrders() {
        return completedWorkOrders;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public void setInstallationDate(LocalDate installationDate) {
        this.installationDate = installationDate;
    }

    public void setNextMaintenanceDate(LocalDate nextMaintenanceDate) {
        this.nextMaintenanceDate = nextMaintenanceDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setOpenWorkOrders(int openWorkOrders) {
        this.openWorkOrders = openWorkOrders;
    }

    public void setOverdueWorkOrders(int overdueWorkOrders) {
        this.overdueWorkOrders = overdueWorkOrders;
    }

    public void setHighPriorityWorkOrders(int highPriorityWorkOrders) {
        this.highPriorityWorkOrders = highPriorityWorkOrders;
    }

    public void setCompletedWorkOrders(int completedWorkOrders) {
        this.completedWorkOrders = completedWorkOrders;
    }
}