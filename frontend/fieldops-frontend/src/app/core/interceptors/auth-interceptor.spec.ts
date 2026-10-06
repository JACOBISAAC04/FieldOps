import { TestBed } from '@angular/core/testing';
import {
  HttpClient,
  provideHttpClient,
  withInterceptors
} from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting
} from '@angular/common/http/testing';
import { AuthService } from '../services/auth.service';
import { authInterceptor } from './auth-interceptor';

describe('authInterceptor', () => {
  let httpClient: HttpClient;
  let httpMock: HttpTestingController;
  let authService: {
    getToken: ReturnType<typeof vi.fn>;
  };

  beforeEach(() => {
    authService = {
      getToken: vi.fn()
    };

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authService },
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting()
      ]
    });

    httpClient = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should add the JWT authorization header when a token exists', () => {
    authService.getToken.mockReturnValue('test-jwt-token');

    httpClient.get('/api/equipment').subscribe();

    const request = httpMock.expectOne('/api/equipment');

    expect(request.request.headers.get('Authorization')).toBe(
      'Bearer test-jwt-token'
    );

    request.flush([]);
  });

  it('should not add the authorization header when no token exists', () => {
    authService.getToken.mockReturnValue(null);

    httpClient.get('/api/equipment').subscribe();

    const request = httpMock.expectOne('/api/equipment');

    expect(request.request.headers.has('Authorization')).toBe(false);

    request.flush([]);
  });
});