import { TestBed } from '@angular/core/testing';
import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting
} from '@angular/common/http/testing';
import { errorInterceptor } from './error-interceptor';
import { HttpErrorResponse } from '@angular/common/http';

describe('errorInterceptor', () => {
  let http: HttpClient;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([errorInterceptor])),
        provideHttpClientTesting()
      ]
    });

    http = TestBed.inject(HttpClient);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('handles server unavailable errors', () => {
    http.get('/api/test').subscribe({
      next: () => {
        throw new Error('Request should have failed');
      },
      error: (error: HttpErrorResponse) => {
        expect(error.status).toBe(0);
        expect(error.error.message).toBe('Unable to connect to the server.');
      }
    });

    const request = httpTesting.expectOne('/api/test');

    request.error(new ProgressEvent('error'));
  });

  it('handles unauthorized errors', () => {
    http.get('/api/test').subscribe({
      next: () => {
        throw new Error('Request should have failed');
      },
      error: (error: HttpErrorResponse) => {
        expect(error.status).toBe(401);
        expect(error.error.message).toBe(
          'Your session has expired. Please log in again.'
        );
      }
    });

    const request = httpTesting.expectOne('/api/test');

    request.flush(
      { message: 'Authentication required' },
      { status: 401, statusText: 'Unauthorized' }
    );
  });

  it('handles forbidden errors', () => {
    http.get('/api/test').subscribe({
      next: () => {
        throw new Error('Request should have failed');
      },
      error: (error: HttpErrorResponse) => {
        expect(error.status).toBe(403);
        expect(error.error.message).toBe(
          'You do not have permission to perform this action.'
        );
      }
    });

    const request = httpTesting.expectOne('/api/test');

    request.flush(
      { message: 'Forbidden' },
      { status: 403, statusText: 'Forbidden' }
    );
  });

  it('preserves backend message for bad requests', () => {
    http.get('/api/test').subscribe({
      next: () => {
        throw new Error('Request should have failed');
      },
      error: (error: HttpErrorResponse) => {
        expect(error.status).toBe(400);
        expect(error.error.message).toBe('Invalid equipment data');
      }
    });

    const request = httpTesting.expectOne('/api/test');

    request.flush(
      { message: 'Invalid equipment data' },
      { status: 400, statusText: 'Bad Request' }
    );
  });

  it('handles not found errors', () => {
    http.get('/api/test').subscribe({
      next: () => {
        throw new Error('Request should have failed');
      },
      error: (error: HttpErrorResponse) => {
        expect(error.status).toBe(404);
        expect(error.error.message).toBe('Equipment not found');
      }
    });

    const request = httpTesting.expectOne('/api/test');

    request.flush(
      { message: 'Equipment not found' },
      { status: 404, statusText: 'Not Found' }
    );
  });

  it('handles conflict errors', () => {
    http.get('/api/test').subscribe({
      next: () => {
        throw new Error('Request should have failed');
      },
      error: (error: HttpErrorResponse) => {
        expect(error.status).toBe(409);
        expect(error.error.message).toBe('Equipment already exists');
      }
    });

    const request = httpTesting.expectOne('/api/test');

    request.flush(
      { message: 'Equipment already exists' },
      { status: 409, statusText: 'Conflict' }
    );
  });

  it('handles server errors', () => {
    http.get('/api/test').subscribe({
      next: () => {
        throw new Error('Request should have failed');
      },
      error: (error: HttpErrorResponse) => {
        expect(error.status).toBe(500);
        expect(error.error.message).toBe(
          'The server encountered an error. Please try again later.'
        );
      }
    });

    const request = httpTesting.expectOne('/api/test');

    request.flush(
      { message: 'Internal server error' },
      { status: 500, statusText: 'Internal Server Error' }
    );
  });
});