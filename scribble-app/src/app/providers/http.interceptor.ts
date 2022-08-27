import { Injectable } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HTTP_INTERCEPTORS } from '@angular/common/http';
import { Observable } from 'rxjs';

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
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    req = req.clone({
      withCredentials: true,
    });
    return next.handle(req);
  }
}
export const httpInterceptorProviders = [
  { provide: HTTP_INTERCEPTORS, useClass: HttpRequestInterceptor, multi: true },
];