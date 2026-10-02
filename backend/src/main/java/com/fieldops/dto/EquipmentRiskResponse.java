package com.fieldops.dto;

import java.util.List;

public class EquipmentRiskResponse {

    private Long equipmentId;
    private String riskLevel;
    private boolean maintenanceDue;
    private List<String> reasons;

    public EquipmentRiskResponse() {
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public boolean isMaintenanceDue() {
        return maintenanceDue;
    }

    public void setMaintenanceDue(boolean maintenanceDue) {
        this.maintenanceDue = maintenanceDue;
    }

    public List<String> getReasons() {
        return reasons;
    }

    public void setReasons(List<String> reasons) {
        this.reasons = reasons;
    }
}