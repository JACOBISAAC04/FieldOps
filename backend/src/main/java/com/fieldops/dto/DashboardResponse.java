package com.fieldops.dto;

public class DashboardResponse {

    private long totalEquipment;
    private long operationalEquipment;
    private long maintenanceRequiredEquipment;
    private long deactivatedEquipment;

    private long totalWorkOrders;
    private long openWorkOrders;
    private long assignedWorkOrders;
    private long inProgressWorkOrders;
    private long overdueWorkOrders;
    private long highPriorityWorkOrders;
    private long criticalWorkOrders;

    private long availableEngineers;
    private long busyEngineers;
    private long unavailableEngineers;

    private long maintenanceDueEquipment;

    public DashboardResponse() {
    }

    public long getTotalEquipment() {
        return totalEquipment;
    }

    public void setTotalEquipment(long totalEquipment) {
        this.totalEquipment = totalEquipment;
    }

    public long getOperationalEquipment() {
        return operationalEquipment;
    }

    public void setOperationalEquipment(long operationalEquipment) {
        this.operationalEquipment = operationalEquipment;
    }

    public long getMaintenanceRequiredEquipment() {
        return maintenanceRequiredEquipment;
    }

    public void setMaintenanceRequiredEquipment(long maintenanceRequiredEquipment) {
        this.maintenanceRequiredEquipment = maintenanceRequiredEquipment;
    }

    public long getDeactivatedEquipment() {
        return deactivatedEquipment;
    }

    public void setDeactivatedEquipment(long deactivatedEquipment) {
        this.deactivatedEquipment = deactivatedEquipment;
    }

    public long getTotalWorkOrders() {
        return totalWorkOrders;
    }

    public void setTotalWorkOrders(long totalWorkOrders) {
        this.totalWorkOrders = totalWorkOrders;
    }

    public long getOpenWorkOrders() {
        return openWorkOrders;
    }

    public void setOpenWorkOrders(long openWorkOrders) {
        this.openWorkOrders = openWorkOrders;
    }

    public long getAssignedWorkOrders() {
        return assignedWorkOrders;
    }

    public void setAssignedWorkOrders(long assignedWorkOrders) {
        this.assignedWorkOrders = assignedWorkOrders;
    }

    public long getInProgressWorkOrders() {
        return inProgressWorkOrders;
    }

    public void setInProgressWorkOrders(long inProgressWorkOrders) {
        this.inProgressWorkOrders = inProgressWorkOrders;
    }

    public long getOverdueWorkOrders() {
        return overdueWorkOrders;
    }

    public void setOverdueWorkOrders(long overdueWorkOrders) {
        this.overdueWorkOrders = overdueWorkOrders;
    }

    public long getHighPriorityWorkOrders() {
        return highPriorityWorkOrders;
    }

    public void setHighPriorityWorkOrders(long highPriorityWorkOrders) {
        this.highPriorityWorkOrders = highPriorityWorkOrders;
    }

    public long getCriticalWorkOrders() {
        return criticalWorkOrders;
    }

    public void setCriticalWorkOrders(long criticalWorkOrders) {
        this.criticalWorkOrders = criticalWorkOrders;
    }

    public long getAvailableEngineers() {
        return availableEngineers;
    }

    public void setAvailableEngineers(long availableEngineers) {
        this.availableEngineers = availableEngineers;
    }

    public long getBusyEngineers() {
        return busyEngineers;
    }

    public void setBusyEngineers(long busyEngineers) {
        this.busyEngineers = busyEngineers;
    }

    public long getUnavailableEngineers() {
        return unavailableEngineers;
    }

    public void setUnavailableEngineers(long unavailableEngineers) {
        this.unavailableEngineers = unavailableEngineers;
    }

    public long getMaintenanceDueEquipment() {
        return maintenanceDueEquipment;
    }

    public void setMaintenanceDueEquipment(long maintenanceDueEquipment) {
        this.maintenanceDueEquipment = maintenanceDueEquipment;
    }
}