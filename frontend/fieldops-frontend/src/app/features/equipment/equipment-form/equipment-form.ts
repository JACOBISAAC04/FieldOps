import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { EquipmentRequest } from '../../../core/models/equipment.model';
import { EquipmentService } from '../../../core/services/equipment.service';

@Component({
  selector: 'app-equipment-form',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './equipment-form.html',
  styleUrl: './equipment-form.css'
})
export class EquipmentForm implements OnInit {
  private equipmentService = inject(EquipmentService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  id = signal<number | null>(null);
  editing = signal(false);
  loading = signal(false);
  saving = signal(false);
  success = signal('');
  error = signal('');

  form: EquipmentRequest = {
    name: '',
    type: '',
    location: '',
    status: 'OPERATIONAL',
    installationDate: '',
    nextMaintenanceDate: null
  };

  ngOnInit(): void {
    const routeId = this.route.snapshot.paramMap.get('id');

    if (routeId) {
      this.id.set(Number(routeId));
      this.editing.set(true);
      this.loadEquipment(Number(routeId));
    }
  }

  loadEquipment(id: number): void {
    this.loading.set(true);

    this.equipmentService.getEquipmentById(id).subscribe({
      next: (equipment) => {
        this.form = {
          name: equipment.name,
          type: equipment.type,
          location: equipment.location,
          status: equipment.status,
          installationDate: equipment.installationDate,
          nextMaintenanceDate: equipment.nextMaintenanceDate
        };

        this.loading.set(false);
      },
      error: () => {
        this.error.set('Unable to load equipment.');
        this.loading.set(false);
      }
    });
  }

  submit(): void {
    this.saving.set(true);
    this.success.set('');
    this.error.set('');

    if (this.editing() && this.id()) {
      this.equipmentService.updateEquipment(this.id()!, this.form).subscribe({
        next: () => {
          this.saving.set(false);
          this.success.set('Equipment updated successfully.');
        },
        error: () => {
          this.saving.set(false);
          this.error.set('Unable to update equipment.');
        }
      });

      return;
    }

    this.equipmentService.createEquipment(this.form).subscribe({
      next: () => {
        this.saving.set(false);
        this.success.set('Equipment created successfully.');

        this.form = {
          name: '',
          type: '',
          location: '',
          status: 'OPERATIONAL',
          installationDate: '',
          nextMaintenanceDate: null
        };
      },
      error: () => {
        this.saving.set(false);
        this.error.set('Unable to create equipment.');
      }
    });
  }
}
