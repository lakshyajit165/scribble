import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthService } from './auth.service';


@Injectable({ providedIn: 'root' })
export class AuthRouteGuard implements CanActivate {
    constructor(
        private _router: Router,
        private _authService: AuthService
    ) { }

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const user = this._authService.loggedInStatus;
        if (user) {
            this._router.navigate(['/home']);
            return false;
        } 
        return true;
    }
}