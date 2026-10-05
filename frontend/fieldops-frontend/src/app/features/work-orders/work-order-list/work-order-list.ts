import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { WorkOrderService } from '../../../core/services/work-order.service';
import { EquipmentService } from '../../../core/services/equipment.service';
import { EngineerService } from '../../../core/services/engineer.service';
import { WorkOrder } from '../work-order.model';
import { Equipment } from '../../../core/models/equipment.model';
import { Engineer } from '../../../core/models/engineer.model';

@Component({
  selector: 'app-work-order-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './work-order-list.html',
  styleUrl: './work-order-list.css'
})
export class WorkOrderList implements OnInit {

  workOrders = signal<WorkOrder[]>([]);
  filteredWorkOrders = signal<WorkOrder[]>([]);
  equipment = signal<Equipment[]>([]);
  engineers = signal<Engineer[]>([]);
  loading = signal(true);
  error = signal('');

  statusFilter = signal('');
  priorityFilter = signal('');

  constructor(
    private workOrderService: WorkOrderService,
    private equipmentService: EquipmentService,
    private engineerService: EngineerService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading.set(true);
    this.error.set('');

    forkJoin({
      workOrders: this.workOrderService.getAllWorkOrders(),
      equipment: this.equipmentService.getAllEquipment(),
      engineers: this.engineerService.getAllEngineers()
    }).subscribe({
      next: (data) => {
        this.workOrders.set(data.workOrders);
        this.equipment.set(data.equipment);
        this.engineers.set(data.engineers);
        this.filteredWorkOrders.set(data.workOrders);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Failed to load work order data.');
        this.loading.set(false);
      }
    });
  }

  getEquipmentName(equipmentId: number): string {
    const item = this.equipment().find(equipment => equipment.id === equipmentId);
    return item ? item.name : `Equipment #${equipmentId}`;
  }

  getEngineerName(engineerId: number | null): string {
    if (engineerId === null) {
      return 'Unassigned';
    }

    const engineer = this.engineers().find(engineer => engineer.id === engineerId);
    return engineer ? engineer.name : `Engineer #${engineerId}`;
  }

  applyFilters(): void {
    const status = this.statusFilter();
    const priority = this.priorityFilter();

    const filtered = this.workOrders().filter(workOrder => {
      const statusMatch =
        !status ||
        workOrder.status.toLowerCase() === status.toLowerCase();

      const priorityMatch =
        !priority ||
        workOrder.priority.toLowerCase() === priority.toLowerCase();

      return statusMatch && priorityMatch;
    });

    this.filteredWorkOrders.set(filtered);
  }

  onStatusChange(status: string): void {
    this.statusFilter.set(status);
    this.applyFilters();
  }

  onPriorityChange(priority: string): void {
    this.priorityFilter.set(priority);
    this.applyFilters();
  }

  clearFilters(): void {
    this.statusFilter.set('');
    this.priorityFilter.set('');
    this.filteredWorkOrders.set(this.workOrders());
  }

  viewDetails(id: number): void {
    this.router.navigate(['/work-orders', id]);
  }

  createWorkOrder(): void {
    this.router.navigate(['/work-orders/new']);
  }
}
