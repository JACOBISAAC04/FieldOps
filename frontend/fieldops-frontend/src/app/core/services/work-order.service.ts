import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { WorkOrder, WorkOrderRequest } from '../../features/work-orders/work-order.model';
import { environment } from '../../../environments/environment';
@Injectable({
  providedIn: 'root'
})
export class WorkOrderService {

  private readonly apiUrl = environment.apiUrl + '/work-orders';

  constructor(private http: HttpClient) {}

  getAllWorkOrders(): Observable<WorkOrder[]> {
    return this.http.get<WorkOrder[]>(this.apiUrl);
  }

  getWorkOrderById(id: number): Observable<WorkOrder> {
    return this.http.get<WorkOrder>(`${this.apiUrl}/${id}`);
  }

  createWorkOrder(request: WorkOrderRequest): Observable<WorkOrder> {
    return this.http.post<WorkOrder>(this.apiUrl, request);
  }

  updateWorkOrder(id: number, request: WorkOrderRequest): Observable<WorkOrder> {
    return this.http.put<WorkOrder>(`${this.apiUrl}/${id}`, request);
  }

  assignEngineer(id: number, engineerId: number): Observable<WorkOrder> {
    return this.http.put<WorkOrder>(
      `${this.apiUrl}/${id}/assign/${engineerId}`,
      {}
    );
  }

  updateStatus(id: number, status: string): Observable<WorkOrder> {
    return this.http.put<WorkOrder>(
      `${this.apiUrl}/${id}/status`,
      null,
      { params: { status } }
    );
  }

  getByStatus(status: string): Observable<WorkOrder[]> {
    return this.http.get<WorkOrder[]>(
      `${this.apiUrl}/status/${status}`
    );
  }

  getByPriority(priority: string): Observable<WorkOrder[]> {
    return this.http.get<WorkOrder[]>(
      `${this.apiUrl}/priority/${priority}`
    );
  }

  getByEquipment(equipmentId: number): Observable<WorkOrder[]> {
    return this.http.get<WorkOrder[]>(
      `${this.apiUrl}/equipment/${equipmentId}`
    );
  }

  getByEngineer(engineerId: number): Observable<WorkOrder[]> {
    return this.http.get<WorkOrder[]>(
      `${this.apiUrl}/engineer/${engineerId}`
    );
  }
    getMaintenanceHistory(equipmentId: number): Observable<WorkOrder[]> {
    return this.http.get<WorkOrder[]>(
      `${this.apiUrl}/equipment/${equipmentId}/history`
    );
  }
}