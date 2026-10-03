import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Document } from '../models/document.model';

@Injectable({
  providedIn: 'root'
})
export class DocumentService {
  private http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/api/documents';

  uploadForEquipment(
    equipmentId: number,
    file: File,
    documentType: string
  ): Observable<Document> {
    const formData = new FormData();

    formData.append('file', file);
    formData.append('documentType', documentType);

    return this.http.post<Document>(
      `${this.apiUrl}/equipment/${equipmentId}`,
      formData
    );
  }

  uploadForWorkOrder(
    workOrderId: number,
    file: File,
    documentType: string
  ): Observable<Document> {
    const formData = new FormData();

    formData.append('file', file);
    formData.append('documentType', documentType);

    return this.http.post<Document>(
      `${this.apiUrl}/work-order/${workOrderId}`,
      formData
    );
  }

  getEquipmentDocuments(
    equipmentId: number
  ): Observable<Document[]> {
    return this.http.get<Document[]>(
      `${this.apiUrl}/equipment/${equipmentId}`
    );
  }

  getWorkOrderDocuments(
    workOrderId: number
  ): Observable<Document[]> {
    return this.http.get<Document[]>(
      `${this.apiUrl}/work-order/${workOrderId}`
    );
  }

  getDownloadUrl(documentId: number): string {
    return `${this.apiUrl}/${documentId}/download`;
  }

  deleteDocument(documentId: number): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/${documentId}`
    );
  }
}