export interface Equipment {
  id: number;
  name: string;
  type: string;
  location: string;
  status: string;
  installationDate: string;
  nextMaintenanceDate: string | null;
}

export interface EquipmentRequest {
  name: string;
  type: string;
  location: string;
  status: string;
  installationDate: string;
  nextMaintenanceDate: string | null;
}