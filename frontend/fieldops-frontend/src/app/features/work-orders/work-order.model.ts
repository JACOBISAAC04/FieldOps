export interface WorkOrder {
  id: number;
  equipmentId: number;
  engineerId: number | null;
  priority: string;
  description: string;
  status: string;
  createdAt: string;
  dueDate: string | null;
  completedAt: string | null;
}

export interface WorkOrderRequest {
  equipmentId: number;
  engineerId?: number | null;
  priority: string;
  description: string;
  dueDate?: string | null;
}