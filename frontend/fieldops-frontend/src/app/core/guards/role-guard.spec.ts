import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { roleGuard } from './role-guard';

describe('roleGuard', () => {
  let authService: {
    hasAnyRole: ReturnType<typeof vi.fn>;
  };

  let router: {
    createUrlTree: ReturnType<typeof vi.fn>;
  };

  beforeEach(() => {
    authService = {
      hasAnyRole: vi.fn()
    };

    router = {
      createUrlTree: vi.fn()
    };

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: Router, useValue: router }
      ]
    });
  });

  it('should allow users with an allowed role', () => {
    authService.hasAnyRole.mockReturnValue(true);

    const result = TestBed.runInInjectionContext(() =>
      roleGuard(['ADMIN', 'OPERATIONS'])(
        {} as ActivatedRouteSnapshot,
        {} as RouterStateSnapshot
      )
    );

    expect(result).toBe(true);
    expect(router.createUrlTree).not.toHaveBeenCalled();
  });

  it('should redirect users without an allowed role', () => {
    const dashboardUrlTree = { path: '/dashboard' };

    authService.hasAnyRole.mockReturnValue(false);
    router.createUrlTree.mockReturnValue(dashboardUrlTree);

    const result = TestBed.runInInjectionContext(() =>
      roleGuard(['ADMIN'])(
        {} as ActivatedRouteSnapshot,
        {} as RouterStateSnapshot
      )
    );

    expect(result).toBe(dashboardUrlTree);
    expect(router.createUrlTree).toHaveBeenCalledWith(['/dashboard']);
  });
});