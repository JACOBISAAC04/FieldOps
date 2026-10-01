import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { EngineerService } from '../../../core/services/engineer.service';
import { AvailableUser } from '../../../core/models/engineer.model';

@Component({
  selector: 'app-engineer-form',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './engineer-form.html',
  styleUrl: './engineer-form.scss'
})
export class EngineerForm implements OnInit {

  engineerId: number | null = null;
  editing = false;

  loading = signal(false);
  loadingUsers = signal(false);
  saving = signal(false);
  error = signal('');
  success = signal('');

  availableUsers = signal<AvailableUser[]>([]);

  userId: number | null = null;
  specialization = '';
  location = '';
  availability = 'AVAILABLE';

  readonly availabilityOptions = [
    'AVAILABLE',
    'BUSY',
    'UNAVAILABLE'
  ];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private engineerService: EngineerService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');

    if (id) {
      this.engineerId = Number(id);
      this.editing = true;
      this.loadEngineer(this.engineerId);
    } else {
      this.loadAvailableUsers();
    }
  }

  private loadAvailableUsers(): void {
    this.loadingUsers.set(true);

    this.engineerService.getAvailableEngineers().subscribe({
      next: (users) => {
        this.availableUsers.set(users);
        this.loadingUsers.set(false);
      },
      error: () => {
        this.error.set('Unable to load available users.');
        this.loadingUsers.set(false);
      }
    });
  }

  private loadEngineer(id: number): void {
    this.loading.set(true);
    this.error.set('');

    this.engineerService.getEngineerById(id).subscribe({
      next: (engineer) => {
        this.userId = engineer.userId;
        this.specialization = engineer.specialization;
        this.location = engineer.location;
        this.availability = engineer.availability;
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Unable to load engineer.');
        this.loading.set(false);
      }
    });
  }

  save(): void {
    this.error.set('');
    this.success.set('');

    if (!this.userId) {
      this.error.set('Please select a user.');
      return;
    }

    if (!this.specialization.trim()) {
      this.error.set('Specialization is required.');
      return;
    }

    if (!this.location.trim()) {
      this.error.set('Location is required.');
      return;
    }

    const request = {
      userId: this.userId,
      specialization: this.specialization.trim(),
      location: this.location.trim(),
      availability: this.availability
    };

    this.saving.set(true);

    if (this.editing && this.engineerId) {
      this.engineerService.updateEngineer(this.engineerId, request).subscribe({
        next: () => {
          this.saving.set(false);
          this.success.set('Engineer updated successfully.');

          setTimeout(() => {
            this.router.navigate(['/engineers', this.engineerId]);
          }, 500);
        },
        error: () => {
          this.error.set('Unable to update engineer.');
          this.saving.set(false);
        }
      });

      return;
    }

    this.engineerService.createEngineer(request).subscribe({
      next: (engineer) => {
        this.saving.set(false);
        this.router.navigate(['/engineers', engineer.id]);
      },
      error: () => {
        this.error.set('Unable to create engineer.');
        this.saving.set(false);
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/engineers']);
  }
}