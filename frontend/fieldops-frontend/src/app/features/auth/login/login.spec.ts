import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { Login } from './login';
import { AuthService } from '../../../core/services/auth.service';

describe('Login', () => {
  let component: Login;
  let fixture: ComponentFixture<Login>;
  let authService: {
    login: ReturnType<typeof vi.fn>;
  };
  let router: {
    navigate: ReturnType<typeof vi.fn>;
  };

  beforeEach(async () => {
    authService = {
      login: vi.fn()
    };

    router = {
      navigate: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [Login],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: Router, useValue: router }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Login);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should show validation error when credentials are empty', () => {
    component.onSubmit();

    expect(component.errorMessage).toBe(
      'Please enter your email and password.'
    );
    expect(authService.login).not.toHaveBeenCalled();
  });

  it('should login and navigate to dashboard', () => {
    authService.login.mockReturnValue(
      of({
        token: 'test-token',
        userId: 1,
        name: 'Jacob Isaac',
        email: 'jacob@fieldops.local',
        role: 'FIELD_ENGINEER'
      })
    );

    component.email = 'jacob@fieldops.local';
    component.password = 'dev-password';

    component.onSubmit();

    expect(authService.login).toHaveBeenCalledWith({
      email: 'jacob@fieldops.local',
      password: 'dev-password'
    });

    expect(component.isLoading).toBe(false);
    expect(router.navigate).toHaveBeenCalledWith(['/dashboard']);
  });

  it('should display backend error when login fails', () => {
    authService.login.mockReturnValue(
      throwError(() => ({
        error: {
          message: 'Invalid email or password.'
        }
      }))
    );

    component.email = 'jacob@fieldops.local';
    component.password = 'wrong-password';

    component.onSubmit();

    expect(component.errorMessage).toBe('Invalid email or password.');
    expect(component.isLoading).toBe(false);
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should toggle password visibility state', () => {
    expect(component.showPassword).toBe(false);

    component.togglePassword();

    expect(component.showPassword).toBe(true);

    component.togglePassword();

    expect(component.showPassword).toBe(false);
  });
});