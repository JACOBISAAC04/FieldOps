import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Equipment } from '../../../core/models/equipment.model';
import { EquipmentService } from '../../../core/services/equipment.service';
import { EquipmentRisk } from '../../../core/models/equipment-risk.model';
import { WorkOrder } from '../../work-orders/work-order.model';
import { WorkOrderService } from '../../../core/services/work-order.service';

@Component({
  selector: 'app-equipment-detail',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './equipment-detail.html',
  styleUrl: './equipment-detail.scss'
})
export class EquipmentDetail implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private equipmentService = inject(EquipmentService);
  private workOrderService = inject(WorkOrderService);

  equipment = signal<Equipment | null>(null);
  risk = signal<EquipmentRisk | null>(null);
  loading = signal(true);
  error = signal('');
  riskLoading = signal(true);
  riskError = signal('');
  maintenanceHistory = signal<WorkOrder[]>([]);
  historyLoading = signal(true);
  historyError = signal('');

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    if (!id) {
      this.error.set('Invalid equipment ID.');
      this.loading.set(false);
      return;
    }

    this.equipmentService.getEquipmentById(id).subscribe({
      next: (data) => {
        this.equipment.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Unable to load equipment.');
        this.loading.set(false);
      }
    });

    this.equipmentService.getEquipmentRisk(id).subscribe({
      next: (data) => {
        this.risk.set(data);
        this.riskLoading.set(false);
      },
      error: () => {
        this.riskError.set('Unable to load equipment risk.');
        this.riskLoading.set(false);
      }
    });

    this.workOrderService.getMaintenanceHistory(id).subscribe({
      next: (data) => {
        this.maintenanceHistory.set(data);
        this.historyLoading.set(false);
      },
      error: () => {
        this.historyError.set('Unable to load maintenance history.');
        this.historyLoading.set(false);
      }
    });
  }

  deactivate(): void {
    const equipmentId = this.equipment()?.id;

    if (!equipmentId) {
      return;
    }

    const confirmed = confirm(
      `Are you sure you want to deactivate ${this.equipment()?.name}?`
    );

    if (!confirmed) {
      return;
    }

    this.loading.set(true);
    this.error.set('');

    this.equipmentService.deactivateEquipment(equipmentId).subscribe({
      next: () => {
        this.router.navigate(['/equipment']);
      },
      error: () => {
        this.error.set('Unable to deactivate equipment.');
        this.loading.set(false);
      }
    });
  }
}