import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../services/auth/auth.service';


@Injectable({ providedIn: 'root' })
export class AuthRouteGuard implements CanActivate {
    constructor(
        private _router: Router,
        private _authService: AuthService
    ) { }

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        if (this._authService.isUserLoggedIn()) {
            this._router.navigate(['home']);
            return false;
          }
          
          return true;
      
    }
}