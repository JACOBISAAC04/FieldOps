import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Equipment } from '../../../core/models/equipment.model';
import { EquipmentService } from '../../../core/services/equipment.service';

@Component({
  selector: 'app-equipment-list',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './equipment-list.html',
  styleUrl: './equipment-list.css'
})
export class EquipmentList implements OnInit {
  private equipmentService = inject(EquipmentService);

  equipment = signal<Equipment[]>([]);
  loading = signal(false);
  error = signal('');
  maintenanceDue = signal(false);

  nameFilter = '';
  statusFilter = '';
  typeFilter = '';
  locationFilter = '';

  ngOnInit(): void {
    this.loadEquipment();
  }

  loadEquipment(): void {
    this.loading.set(true);
    this.error.set('');
    this.maintenanceDue.set(false);

    this.equipmentService.getAllEquipment({
      name: this.nameFilter,
      status: this.statusFilter,
      type: this.typeFilter,
      location: this.locationFilter
    }).subscribe({
      next: (data) => {
        this.equipment.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Unable to load equipment.');
        this.loading.set(false);
      }
    });
  }

  loadMaintenanceDue(): void {
    this.loading.set(true);
    this.error.set('');
    this.maintenanceDue.set(true);

    this.equipmentService.getMaintenanceDueEquipment().subscribe({
      next: (data) => {
        this.equipment.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Unable to load maintenance due equipment.');
        this.loading.set(false);
      }
    });
  }

  clearFilters(): void {
    this.nameFilter = '';
    this.statusFilter = '';
    this.typeFilter = '';
    this.locationFilter = '';
    this.loadEquipment();
  }
}
