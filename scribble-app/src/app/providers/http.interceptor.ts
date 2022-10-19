import { Injectable } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HTTP_INTERCEPTORS, HttpClient, HttpErrorResponse } from '@angular/common/http';
import { catchError, Observable, switchMap, throwError } from 'rxjs';
import { AuthService } from '../services/auth/auth.service';
import { CookieService } from 'ngx-cookie-service';
/**
 * @source: https://www.bezkoder.com/angular-13-jwt-auth-httponly-cookie/
 * HttpInterceptor has intercept() method to inspect and transform, 
 * HTTP requests before they are sent to server.
 * HttpRequestInterceptor implements HttpInterceptor. 
 * We’re gonna add withCredentials: true to make browser include 
 * Cookie on the Request header (HttpOnly Cookie).
 * */
@Injectable()
export class HttpRequestInterceptor implements HttpInterceptor {

  constructor(
    private _authService: AuthService,
    private _http: HttpClient,
    private _cookieService: CookieService
  ) {}
  refresh = false;

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const req = request.clone({
      withCredentials: true,
    });
    // if no such cookie is present it means first time visit => no need to trigger refresh token flow.
    if(!this._cookieService.get('user_profile')){
      return next.handle(req);
    }else{
      return next.handle(req).pipe(catchError((err: HttpErrorResponse) => {
        if (err.status === 401 && !this.refresh) {
          this.refresh = true;
          return this._http.get('http://localhost:9000/auth-service/api/v1/auth/get_new_creds', {withCredentials: true}).pipe(
            switchMap((res: any) => {
      
              return next.handle(request.clone({
                withCredentials: true
              }));
            })
          );
          
        }else{
          this.refresh = false;
          return throwError(() => err);
        }
        
      }));
    }
   
  }
}
// export const httpInterceptorProviders = [
//   { provide: HTTP_INTERCEPTORS, useClass: HttpRequestInterceptor, multi: true },
// ];