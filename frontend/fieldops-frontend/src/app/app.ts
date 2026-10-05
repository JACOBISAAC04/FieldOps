import { Component, inject } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { filter } from 'rxjs';
import { AuthService } from './core/services/auth.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  private router = inject(Router);
  protected authService = inject(AuthService);

  isLoginPage = this.router.url === '/login';

  constructor() {
    this.router.events
      .pipe(
        filter(event => event instanceof NavigationEnd)
      )
      .subscribe(event => {
        this.isLoginPage =
          (event as NavigationEnd).urlAfterRedirects === '/login';
      });
  }

  get currentUser() {
    return this.authService.getUser();
  }

  get userInitials(): string {
    const name = this.currentUser?.name;

    if (!name) {
      return '';
    }

    return name
      .split(' ')
      .map(part => part[0])
      .join('')
      .substring(0, 2)
      .toUpperCase();
  }

  get userRole(): string {
    const role = this.currentUser?.role;

    if (role === 'ENGINEER' || role === 'FIELD_ENGINEER') {
      return 'Field Engineer';
    }

    if (role === 'ADMIN') {
      return 'Administrator';
    }

    if (role === 'OPERATIONS') {
      return 'Operations';
    }

    return role ?? '';
  }
  logout(): void {
  this.authService.logout();
  this.router.navigate(['/login']);
}
}