export interface Document {
  id: number;
  fileName: string;
  contentType: string;
  fileSize: number;
  documentType: string;
  equipmentId: number | null;
  workOrderId: number | null;
  uploadedAt: string;
}