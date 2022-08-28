import { Injectable } from '@angular/core';
import { MatSnackBar,  MatSnackBarHorizontalPosition,
  MatSnackBarVerticalPosition,
 } from '@angular/material/snack-bar';
import { SnackbarComponent } from '../components/snackbar/snackbar.component';

@Injectable({
  providedIn: 'root'
})
export class SnackbarService {

  horizontalPosition: MatSnackBarHorizontalPosition = 'center';
  verticalPosition: MatSnackBarVerticalPosition = 'bottom';

  constructor(
    private _snackBar: MatSnackBar
  ) { }
  
  showSnackBar(message: string, duration: number, iconName: string): void {
    this._snackBar.openFromComponent(SnackbarComponent, {
      data: {
        message: message,
        icon: iconName
      },
      duration: duration,
      horizontalPosition: this.horizontalPosition,
      verticalPosition: this.verticalPosition
    })

  }

  
}
