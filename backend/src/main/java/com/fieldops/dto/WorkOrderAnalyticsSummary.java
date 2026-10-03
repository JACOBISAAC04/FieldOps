package com.fieldops.dto;

public class WorkOrderAnalyticsSummary {

    private int openWorkOrders;
    private int overdueWorkOrders;
    private int highPriorityWorkOrders;
    private int completedWorkOrders;

    public WorkOrderAnalyticsSummary() {
    }

    public WorkOrderAnalyticsSummary(
            int openWorkOrders,
            int overdueWorkOrders,
            int highPriorityWorkOrders,
            int completedWorkOrders) {
        this.openWorkOrders = openWorkOrders;
        this.overdueWorkOrders = overdueWorkOrders;
        this.highPriorityWorkOrders = highPriorityWorkOrders;
        this.completedWorkOrders = completedWorkOrders;
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