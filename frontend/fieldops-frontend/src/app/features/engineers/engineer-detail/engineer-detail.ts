import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { EngineerService } from '../../../core/services/engineer.service';
import { Engineer } from '../../../core/models/engineer.model';

@Component({
  selector: 'app-engineer-detail',
  standalone: true,
  imports: [],
  templateUrl: './engineer-detail.html',
  styleUrl: './engineer-detail.scss'
})
export class EngineerDetail implements OnInit {

  engineer = signal<Engineer | null>(null);
  loading = signal(true);
  error = signal('');

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private engineerService: EngineerService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    if (!id) {
      this.error.set('Invalid engineer ID.');
      this.loading.set(false);
      return;
    }

    this.engineerService.getEngineerById(id).subscribe({
      next: (engineer) => {
        this.engineer.set(engineer);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Engineer not found.');
        this.loading.set(false);
      }
    });
  }

  back(): void {
    this.router.navigate(['/engineers']);
  }

  edit(): void {
    const id = this.engineer()?.id;

    if (id) {
      this.router.navigate(['/engineers', id, 'edit']);
    }
  }
}