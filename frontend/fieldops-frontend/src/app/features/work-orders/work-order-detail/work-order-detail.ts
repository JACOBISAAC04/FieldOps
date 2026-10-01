import { Component, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { WorkOrderService } from '../../../core/services/work-order.service';
import { EngineerService } from '../../../core/services/engineer.service';
import { WorkOrder } from '../work-order.model';
import { Engineer } from '../../../core/models/engineer.model';

@Component({
  selector: 'app-work-order-detail',
  standalone: true,
  imports: [FormsModule, DatePipe],
  templateUrl: './work-order-detail.html',
  styleUrl: './work-order-detail.scss'
})
export class WorkOrderDetail implements OnInit {

  workOrder = signal<WorkOrder | null>(null);
  engineers = signal<Engineer[]>([]);
  loading = signal(true);
  saving = signal(false);
  error = signal('');
  success = signal('');

  selectedStatus = '';
  engineerId: number | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private workOrderService: WorkOrderService,
    private engineerService: EngineerService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.loadEngineers();

    this.workOrderService.getWorkOrderById(id).subscribe({
      next: (workOrder) => {
        this.setWorkOrder(workOrder);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Work order not found.');
        this.loading.set(false);
      }
    });
  }

  private loadEngineers(): void {
    this.engineerService.getAllEngineers().subscribe({
      next: (engineers) => {
        this.engineers.set(engineers);
      },
      error: () => {
        this.error.set('Unable to load engineers.');
      }
    });
  }

  private setWorkOrder(workOrder: WorkOrder): void {
    this.workOrder.set(workOrder);
    this.selectedStatus = workOrder.status;
    this.engineerId = workOrder.engineerId;
  }

  getAvailableStatuses(): string[] {
    const status = this.workOrder()?.status;

    if (status === 'OPEN') {
      return ['ASSIGNED', 'CANCELLED'];
    }

    if (status === 'ASSIGNED') {
      return ['IN_PROGRESS', 'CANCELLED'];
    }

    if (status === 'IN_PROGRESS') {
      return ['COMPLETED', 'CANCELLED'];
    }

    return [];
  }

  canAssignEngineer(): boolean {
    const status = this.workOrder()?.status;
    return status === 'OPEN' || status === 'ASSIGNED';
  }

  canUpdateStatus(): boolean {
    return this.getAvailableStatuses().length > 0;
  }

  assignEngineer(): void {
    const current = this.workOrder();

    if (!current || this.engineerId === null) {
      this.error.set('Select an engineer to assign.');
      return;
    }

    this.runUpdate(
      this.workOrderService.assignEngineer(current.id, this.engineerId),
      'Engineer assigned.',
      'Unable to assign engineer.'
    );
  }

  changeStatus(): void {
    const current = this.workOrder();

    if (!current || !this.selectedStatus) {
      return;
    }

    this.runUpdate(
      this.workOrderService.updateStatus(current.id, this.selectedStatus),
      'Status updated.',
      'Unable to update status.'
    );
  }

  private runUpdate(
    request: ReturnType<WorkOrderService['assignEngineer']>,
    successMessage: string,
    errorMessage: string
  ): void {
    this.saving.set(true);
    this.error.set('');
    this.success.set('');

    request.subscribe({
      next: (updated) => {
        this.setWorkOrder(updated);
        this.success.set(successMessage);
        this.saving.set(false);
      },
      error: () => {
        this.error.set(errorMessage);
        this.saving.set(false);
      }
    });
  }

  back(): void {
    this.router.navigate(['/work-orders']);
  }
}