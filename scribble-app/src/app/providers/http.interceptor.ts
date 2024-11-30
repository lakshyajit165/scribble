import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpInterceptor,
  HttpHandler,
  HttpRequest,
  HttpErrorResponse,
} from '@angular/common/http';
import { catchError, Observable, switchMap, throwError, take } from 'rxjs';
import { AuthService } from '../services/auth/auth.service';

@Injectable()
export class HttpRequestInterceptor implements HttpInterceptor {
  constructor(private _authService: AuthService) {}

  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    // Clone the request and add `withCredentials` to include cookies
    const req = request.clone({
      withCredentials: true,
    });

    return this._authService.isLoggedIn$.pipe(
      take(1), // Ensure we take only the latest value from the BehaviorSubject
      switchMap((isLoggedIn) => {
        if (!isLoggedIn) {
          // If user is not logged in, just forward the request
          return next.handle(req);
        } else {
          // If user is logged in, proceed with the request and handle potential 401 errors
          return next.handle(req).pipe(
            catchError((err: HttpErrorResponse) => {
              console.error('HTTP Error:', err);
              if (err.status === 401) {
                // If a 401 error occurs, attempt to refresh tokens
                return this._authService
                  .refreshTokens() // Call a method in AuthService to refresh tokens
                  .pipe(
                    switchMap(() => {
                      // Retry the original request after refreshing tokens
                      return next.handle(
                        request.clone({
                          withCredentials: true,
                        })
                      );
                    }),
                    catchError((refreshError) => {
                      // Handle token refresh failure (e.g., logout user)
                      console.error('Token refresh failed:', refreshError);
                      this._authService.updateisLoggedInStatus(false); // Update BehaviorSubject
                      return throwError(() => refreshError);
                    })
                  );
              } else {
                // For other errors, just throw the error
                return throwError(() => err);
              }
            })
          );
        }
      })
    );
  }
}
