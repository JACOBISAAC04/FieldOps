export interface DashboardSummary {
  totalEquipment: number;
  operationalEquipment: number;
  maintenanceRequiredEquipment: number;
  deactivatedEquipment: number;
  totalWorkOrders: number;
  openWorkOrders: number;
  highPriorityWorkOrders: number;
  availableEngineers: number;
  busyEngineers: number;
  unavailableEngineers: number;
  maintenanceDueEquipment: number;
}