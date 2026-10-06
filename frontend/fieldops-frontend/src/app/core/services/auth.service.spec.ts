import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting
} from '@angular/common/http/testing';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);

    localStorage.clear();
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should login and store authentication data', () => {
    const response = {
      token: 'test-jwt-token',
      userId: 1,
      name: 'Jacob Isaac',
      email: 'jacob@fieldops.local',
      role: 'FIELD_ENGINEER'
    };

    service.login({
      email: 'jacob@fieldops.local',
      password: 'dev-password'
    }).subscribe(result => {
      expect(result).toEqual(response);
      expect(service.getToken()).toBe('test-jwt-token');
      expect(service.getUser()?.email).toBe('jacob@fieldops.local');
      expect(service.hasRole('FIELD_ENGINEER')).toBe(true);
      expect(service.isLoggedIn()).toBe(true);
    });

    const request = httpMock.expectOne(
      'http://localhost:8080/api/auth/login'
    );

    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({
      email: 'jacob@fieldops.local',
      password: 'dev-password'
    });

    request.flush(response);
  });

  it('should logout and clear authentication data', () => {
    localStorage.setItem('fieldops_token', 'test-token');
    localStorage.setItem(
      'fieldops_user',
      JSON.stringify({
        userId: 1,
        name: 'Jacob Isaac',
        email: 'jacob@fieldops.local',
        role: 'FIELD_ENGINEER'
      })
    );

    expect(service.isLoggedIn()).toBe(true);

    service.logout();

    expect(service.getToken()).toBeNull();
    expect(service.getUser()).toBeNull();
    expect(service.isLoggedIn()).toBe(false);
  });

  it('should check multiple roles correctly', () => {
    localStorage.setItem(
      'fieldops_user',
      JSON.stringify({
        userId: 1,
        name: 'Jacob Isaac',
        email: 'jacob@fieldops.local',
        role: 'FIELD_ENGINEER'
      })
    );

    expect(service.hasAnyRole(['ADMIN', 'FIELD_ENGINEER'])).toBe(true);
    expect(service.hasAnyRole(['ADMIN', 'OPERATIONS'])).toBe(false);
  });
});