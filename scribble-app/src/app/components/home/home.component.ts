import { Component } from '@angular/core';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Observable } from 'rxjs';
import { map, shareReplay } from 'rxjs/operators';
import { NotesService } from 'src/app/services/notes/notes.service';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth/auth.service';
import { IGenericResponse } from 'src/app/model/IGenericResponse';
import { SnackbarService } from 'src/app/utils/snackbar.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent {
  isHandset$: Observable<boolean> = this.breakpointObserver
    .observe(Breakpoints.Handset)
    .pipe(
      map((result) => result.matches),
      shareReplay()
    );

  constructor(
    private breakpointObserver: BreakpointObserver,
    private _router: Router,
    private _authService: AuthService,
    private _snackBarService: SnackbarService
  ) {}

  goToComponnent(route: string): void {
    this._router.navigate([route]);
  }

  logout(): void {
    this._authService.logout().subscribe({
      next: (data: IGenericResponse) => {
        // this._cookieService.delete("user_profile", "/");
        this._authService.updateisLoggedInStatus(false);
        this._snackBarService.showSnackBar(
          'User logged out!',
          3000,
          'check_circle_outline'
        );
        this._router.navigate(['/login']);
      },
      error: (err) => {
        this._authService.updateisLoggedInStatus(true);
        this._snackBarService.showSnackBar(
          'Error logging out user',
          3000,
          'error_outline'
        );
      },
    });
  }
}
