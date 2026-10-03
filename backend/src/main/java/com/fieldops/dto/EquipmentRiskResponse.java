package com.fieldops.dto;

import java.util.List;

public class EquipmentRiskResponse {

    private Long equipmentId;
    private int riskScore;
    private String riskLevel;
    private int healthScore;
    private boolean maintenanceDue;
    private int openWorkOrders;
    private int overdueWorkOrders;
    private int highPriorityWorkOrders;
    private int completedWorkOrders;
    private List<String> reasons;

    public EquipmentRiskResponse() {
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public int getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(int riskScore) {
        this.riskScore = riskScore;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public int getHealthScore() {
        return healthScore;
    }

    public void setHealthScore(int healthScore) {
        this.healthScore = healthScore;
    }

    public boolean isMaintenanceDue() {
        return maintenanceDue;
    }

    public void setMaintenanceDue(boolean maintenanceDue) {
        this.maintenanceDue = maintenanceDue;
    }

    public int getOpenWorkOrders() {
        return openWorkOrders;
    }

    public void setOpenWorkOrders(int openWorkOrders) {
        this.openWorkOrders = openWorkOrders;
    }

    public int getOverdueWorkOrders() {
        return overdueWorkOrders;
    }

    public void setOverdueWorkOrders(int overdueWorkOrders) {
        this.overdueWorkOrders = overdueWorkOrders;
    }

    public int getHighPriorityWorkOrders() {
        return highPriorityWorkOrders;
    }

    public void setHighPriorityWorkOrders(int highPriorityWorkOrders) {
        this.highPriorityWorkOrders = highPriorityWorkOrders;
    }

    public int getCompletedWorkOrders() {
        return completedWorkOrders;
    }

    public void setCompletedWorkOrders(int completedWorkOrders) {
        this.completedWorkOrders = completedWorkOrders;
    }

    public List<String> getReasons() {
        return reasons;
    }

    public void setReasons(List<String> reasons) {
        this.reasons = reasons;
    }
}