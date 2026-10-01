import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { EngineerService } from '../../../core/services/engineer.service';
import { Engineer } from '../../../core/models/engineer.model';

@Component({
  selector: 'app-engineer-list',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './engineer-list.html',
  styleUrl: './engineer-list.scss'
})
export class EngineerList implements OnInit {

  engineers = signal<Engineer[]>([]);
  filteredEngineers = signal<Engineer[]>([]);
  loading = signal(true);
  error = signal('');

  searchTerm = '';
  availabilityFilter = '';
  specializationFilter = '';

  constructor(
    private engineerService: EngineerService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadEngineers();
  }

  loadEngineers(): void {
    this.loading.set(true);
    this.error.set('');

    this.engineerService.getAllEngineers().subscribe({
      next: (engineers) => {
        this.engineers.set(engineers);
        this.filteredEngineers.set(engineers);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Unable to load engineers.');
        this.loading.set(false);
      }
    });
  }

  applyFilters(): void {
    const search = this.searchTerm.toLowerCase().trim();
    const availability = this.availabilityFilter.toLowerCase();
    const specialization = this.specializationFilter.toLowerCase();

    const filtered = this.engineers().filter(engineer => {
      const searchMatch =
        !search ||
        engineer.name.toLowerCase().includes(search) ||
        engineer.email.toLowerCase().includes(search) ||
        engineer.location.toLowerCase().includes(search);

      const availabilityMatch =
        !availability ||
        engineer.availability.toLowerCase() === availability;

      const specializationMatch =
        !specialization ||
        engineer.specialization.toLowerCase() === specialization;

      return searchMatch && availabilityMatch && specializationMatch;
    });

    this.filteredEngineers.set(filtered);
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.availabilityFilter = '';
    this.specializationFilter = '';
    this.filteredEngineers.set(this.engineers());
  }

  createEngineer(): void {
    this.router.navigate(['/engineers/new']);
  }

  viewEngineer(id: number): void {
    this.router.navigate(['/engineers', id]);
  }

  getSpecializations(): string[] {
    return [...new Set(this.engineers().map(engineer => engineer.specialization))];
  }

  getAvailabilityOptions(): string[] {
    return [...new Set(this.engineers().map(engineer => engineer.availability))];
  }
}