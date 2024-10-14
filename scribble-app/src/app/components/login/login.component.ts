import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormControl,
  AbstractControl,
  FormGroupDirective,
  NgForm,
  Validators,
} from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import {
  MatSnackBar,
  MatSnackBarHorizontalPosition,
  MatSnackBarVerticalPosition,
} from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { IGenericResponse } from 'src/app/model/IGenericResponse';
import { AuthService } from 'src/app/services/auth/auth.service';
import { SnackbarService } from 'src/app/utils/snackbar.service';
import { ILogin } from '../../model/ILogin';

/** Error when invalid control is dirty, touched, or submitted. */
export class CustomErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: FormControl, form: NgForm | FormGroupDirective | null) {
    return control && control.invalid && control.touched;
  }
}

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent implements OnInit {
  constructor(
    private _router: Router,
    private _formBuilder: FormBuilder,
    private _authService: AuthService,
    private _snackBarService: SnackbarService
  ) {}

  ngOnInit(): void {}

  hide: boolean = true;
  _matcher = new CustomErrorStateMatcher();

  horizontalPosition: MatSnackBarHorizontalPosition = 'center';
  verticalPosition: MatSnackBarVerticalPosition = 'bottom';
  userLoginPayload: ILogin = {
    email: '',
    password: '',
  };
  loginFormGroup = this._formBuilder.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]],
  });
  loginUserLoading: boolean = false;

  login(): void {
    this.loginUserLoading = true;
    // get the values from both forms
    if (this.loginFormGroup.valid) {
      this.userLoginPayload.email =
        this.loginFormGroup.get('email')?.value ?? '';
      this.userLoginPayload.password =
        this.loginFormGroup.get('password')?.value ?? '';
      this._authService.login(this.userLoginPayload).subscribe({
        next: (data: IGenericResponse) => {
          this.loginUserLoading = false;
          this._router.navigate(['/home']);
        },
        error: (err) => {
          this.loginUserLoading = false;
          if (err.error && err.error.message) {
            this._snackBarService.showSnackBar(
              err.error.message,
              3000,
              'error_outline'
            );
          } else {
            this._snackBarService.showSnackBar(
              'An error occurred. Please try again!',
              3000,
              'error_outline'
            );
          }
        },
      });
    } else {
      this.loginUserLoading = false;
      this._snackBarService.showSnackBar(
        'Invalid data!',
        3000,
        'error_outline'
      );
    }
  }
}
