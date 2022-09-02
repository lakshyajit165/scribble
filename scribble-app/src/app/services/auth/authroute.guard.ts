import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';
import { AuthService } from './auth.service';


@Injectable({ providedIn: 'root' })
export class AuthRouteGuard implements CanActivate {
    constructor(
        private _router: Router,
        private _authService: AuthService,
        private _cookieService: CookieService
    ) { }

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const status = this._authService.loggedInStatus;
        if (status) {
            this._router.navigate(['home']);
            return false;
          }
          
          return true;
      
    }
}