import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Equipment } from '../../../core/models/equipment.model';
import { EquipmentService } from '../../../core/services/equipment.service';

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

  equipment = signal<Equipment | null>(null);
  loading = signal(true);
  error = signal('');

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