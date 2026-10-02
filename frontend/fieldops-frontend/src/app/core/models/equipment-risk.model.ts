export interface EquipmentRisk {
  equipmentId: number;
  riskLevel: string;
  maintenanceDue: boolean;
  reasons: string[];
}