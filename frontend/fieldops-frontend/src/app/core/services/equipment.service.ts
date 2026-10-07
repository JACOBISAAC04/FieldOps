import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Equipment, EquipmentRequest } from '../models/equipment.model';
import { EquipmentRisk } from '../models/equipment-risk.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class EquipmentService {
  private http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl + '/api/equipment';

  getAllEquipment(filters?: {
    status?: string;
    location?: string;
    type?: string;
    name?: string;
  }): Observable<Equipment[]> {
    let params = new HttpParams();

    if (filters?.status) {
      params = params.set('status', filters.status);
    }

    if (filters?.location) {
      params = params.set('location', filters.location);
    }

    if (filters?.type) {
      params = params.set('type', filters.type);
    }

    if (filters?.name) {
      params = params.set('name', filters.name);
    }

    return this.http.get<Equipment[]>(this.apiUrl, { params });
  }

  getEquipmentById(id: number): Observable<Equipment> {
    return this.http.get<Equipment>(`${this.apiUrl}/${id}`);
  }
  getEquipmentRisk(id: number): Observable<EquipmentRisk> {
    return this.http.get<EquipmentRisk>(
      `${environment.apiUrl}/api/analytics/equipment/${id}`
    );
  }

  createEquipment(request: EquipmentRequest): Observable<Equipment> {
    return this.http.post<Equipment>(this.apiUrl, request);
  }

  updateEquipment(id: number, request: EquipmentRequest): Observable<Equipment> {
    return this.http.put<Equipment>(`${this.apiUrl}/${id}`, request);
  }

  deactivateEquipment(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getMaintenanceDueEquipment(): Observable<Equipment[]> {
    return this.http.get<Equipment[]>(`${this.apiUrl}/maintenance-due`);
  }
}
