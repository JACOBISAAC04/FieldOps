export interface DashboardSummary {
  totalEquipment: number;
  operationalEquipment: number;
  maintenanceRequiredEquipment: number;
  deactivatedEquipment: number;

  totalWorkOrders: number;
  openWorkOrders: number;
  assignedWorkOrders: number;
  inProgressWorkOrders: number;
  overdueWorkOrders: number;
  highPriorityWorkOrders: number;
  criticalWorkOrders: number;

  availableEngineers: number;
  busyEngineers: number;
  unavailableEngineers: number;

  maintenanceDueEquipment: number;
}