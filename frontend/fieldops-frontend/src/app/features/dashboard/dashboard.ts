import { Component, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { DashboardService } from '../../core/services/dashboard.service';
import { DashboardSummary } from '../../core/models/dashboard.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class Dashboard implements OnInit {

  summary = signal<DashboardSummary | null>(null);
  loading = signal(true);
  error = signal('');

  constructor(
    private dashboardService: DashboardService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadDashboard();
  }

  loadDashboard(): void {
    this.loading.set(true);
    this.error.set('');

    this.dashboardService.getSummary().subscribe({
      next: (summary) => {
        this.summary.set(summary);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Unable to load dashboard data.');
        this.loading.set(false);
      }
    });
  }

  openEquipment(): void {
    this.router.navigate(['/equipment']);
  }

  openWorkOrders(): void {
    this.router.navigate(['/work-orders']);
  }

  openEngineers(): void {
    this.router.navigate(['/engineers']);
  }
}