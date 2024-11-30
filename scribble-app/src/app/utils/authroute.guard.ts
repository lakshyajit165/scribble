import { Injectable } from '@angular/core';
import {
  Router,
  CanActivate,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
} from '@angular/router';
import { AuthService } from '../services/auth/auth.service';
import { map, Observable, take } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthRouteGuard implements CanActivate {
  constructor(private _router: Router, private _authService: AuthService) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> {
    // Use take(1) to get the latest value from BehaviorSubject and automatically unsubscribe
    return this._authService.isLoggedIn$.pipe(
      take(1), // Ensure only one subscription to avoid memory leaks
      map((isLoggedIn) => {
        if (isLoggedIn) {
          // If user is logged in, redirect to home page
          this._router.navigate(['home']);
          console.log('User is logged in, redirecting to home');
          return false; // Prevent access to the current route
        }
        console.log('User is not logged in, allowing access to route');
        return true; // Allow access to the route
      })
    );
  }
}
