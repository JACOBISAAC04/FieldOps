import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    catchError(error => {
      if (error instanceof HttpErrorResponse) {
        let message = 'Something went wrong. Please try again.';

        if (error.status === 0) {
          message = 'Unable to connect to the server.';
        } else if (error.status === 400) {
          message = error.error?.message || 'Invalid request.';
        } else if (error.status === 401) {
          message = 'Your session has expired. Please log in again.';
        } else if (error.status === 403) {
          message = 'You do not have permission to perform this action.';
        } else if (error.status === 404) {
          message = error.error?.message || 'The requested resource was not found.';
        } else if (error.status === 409) {
          message = error.error?.message || 'This operation conflicts with existing data.';
        } else if (error.status >= 500) {
          message = 'The server encountered an error. Please try again later.';
        }

        return throwError(() => new HttpErrorResponse({
          error: {
            ...(error.error || {}),
            message
          },
          headers: error.headers,
          status: error.status,
          statusText: error.statusText,
          url: error.url || undefined
        }));
      }

      return throwError(() => error);
    })
  );
};