import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Equipment } from '../../../core/models/equipment.model';
import { EquipmentService } from '../../../core/services/equipment.service';
import { Engineer } from '../../../core/models/engineer.model';
import { EngineerService } from '../../../core/services/engineer.service';
import { WorkOrderService } from '../../../core/services/work-order.service';
import { WorkOrderRequest } from '../work-order.model';

@Component({
  selector: 'app-work-order-form',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './work-order-form.html',
  styleUrl: './work-order-form.scss'
})
export class WorkOrderForm implements OnInit {

  equipmentList = signal<Equipment[]>([]);
  engineers = signal<Engineer[]>([]);
  loadingEquipment = signal(true);
  loadingEngineers = signal(true);
  saving = signal(false);
  error = signal('');

  form = {
    equipmentId: null as number | null,
    engineerId: null as number | null,
    priority: 'MEDIUM',
    description: '',
    dueDate: ''
  };

  constructor(
    private workOrderService: WorkOrderService,
    private equipmentService: EquipmentService,
    private engineerService: EngineerService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadEquipment();
    this.loadEngineers();
  }

  private loadEquipment(): void {
    this.equipmentService.getAllEquipment().subscribe({
      next: (equipment) => {
        this.equipmentList.set(equipment);
        this.loadingEquipment.set(false);
      },
      error: () => {
        this.error.set('Unable to load equipment.');
        this.loadingEquipment.set(false);
      }
    });
  }

  private loadEngineers(): void {
    this.engineerService.getAllEngineers().subscribe({
      next: (engineers) => {
        this.engineers.set(engineers);
        this.loadingEngineers.set(false);
      },
      error: () => {
        this.error.set('Unable to load engineers.');
        this.loadingEngineers.set(false);
      }
    });
  }

  submit(): void {
    this.error.set('');

    if (this.form.equipmentId === null) {
      this.error.set('Equipment is required.');
      return;
    }

    if (!this.form.description.trim()) {
      this.error.set('Description is required.');
      return;
    }

    const request: WorkOrderRequest = {
      equipmentId: this.form.equipmentId,
      engineerId: this.form.engineerId,
      priority: this.form.priority,
      description: this.form.description.trim(),
      dueDate: this.form.dueDate || null
    };

    this.saving.set(true);

    this.workOrderService.createWorkOrder(request).subscribe({
      next: (workOrder) => {
        this.saving.set(false);
        this.router.navigate(['/work-orders', workOrder.id]);
      },
      error: () => {
        this.error.set('Unable to create work order.');
        this.saving.set(false);
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/work-orders']);
  }
}