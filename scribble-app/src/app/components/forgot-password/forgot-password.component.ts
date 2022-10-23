import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroupDirective, NgForm, Validators } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatStepper } from '@angular/material/stepper';
import { Router } from '@angular/router';
import { IConfirmPassword } from 'src/app/model/IConfirmPassword';
import { IGenericResponse } from 'src/app/model/IGenericResponse';
import { ISignUpAndForgotPassword } from 'src/app/model/ISignUpAndForgotPassword';
import { AuthService } from 'src/app/services/auth/auth.service';
import { SnackbarService } from 'src/app/utils/snackbar.service';

/** Error when invalid control is dirty, touched, or submitted. */
export class CustomErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: FormControl, form: NgForm | FormGroupDirective | null) {
    return control && control.invalid && control.touched;
  }
} 

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent implements OnInit {

  constructor(
    private _formBuilder: FormBuilder,
    private _authService: AuthService,
    private _snackBarService: SnackbarService,
    private _router: Router
  ) { }

  ngOnInit(): void {
  }

  _matcher = new CustomErrorStateMatcher();

  emailFormGroup = this._formBuilder.group({
    email: ['', [Validators.required, Validators.email]],
  });

  confirmForgotPasswordFormGroup = this._formBuilder.group({
    verificationCode: ['', Validators.required],
    password: ['', Validators.required]
  });

  signUpAndForgotPasswordPayload: ISignUpAndForgotPassword = {
    email: ''
  };
  confirmForgotPasswordPayload: IConfirmPassword = {
    email: '',
    password: '',
    verification_code: ''
  };
  signUpAndForgotPasswordLoading: boolean = false;
  confirmForgotPasswordLoading: boolean = false;

  forgotPassword(stepper: MatStepper): void {
    // make loading to true
    this.signUpAndForgotPasswordLoading = true;
    // get the values from both forms
    if(this.emailFormGroup.valid){
      this.signUpAndForgotPasswordPayload.email = this.emailFormGroup.get('email')?.value ?? '';
      this._authService.forgotPassword(this.signUpAndForgotPasswordPayload).subscribe({
        next: (data: IGenericResponse) => {
          this.signUpAndForgotPasswordLoading = false;
          this._snackBarService.showSnackBar(data.message, 3000, 'check_circle_outline');
          stepper.next();
        },
        error: err => {
          this.signUpAndForgotPasswordLoading = false;
          if(err.error && err.error.message) {
            this._snackBarService.showSnackBar(err.error.message, 3000, 'error_outline');
          }else{
            this._snackBarService.showSnackBar("An error occurred. Please try again!", 3000, 'error_outline');
          }
        } 
      })
    }else{
      this.signUpAndForgotPasswordLoading = false;
      this._snackBarService.showSnackBar("Invalid data!", 3000, 'error_outline');
    }
    
    
  }

  confirmForgotPassword(): void {
    this.confirmForgotPasswordLoading = true;
    if(this.confirmForgotPasswordFormGroup.valid) {
      this.confirmForgotPasswordPayload.email = this.emailFormGroup.get('email')?.value ?? '';
      this.confirmForgotPasswordPayload.password = this.confirmForgotPasswordFormGroup.get('password')?.value ?? '';
      this.confirmForgotPasswordPayload.verification_code = this.confirmForgotPasswordFormGroup.get('verificationCode')?.value ?? '';
      this._authService.confirmForgotPassword(this.confirmForgotPasswordPayload).subscribe({
        next: (data: IGenericResponse) => {
          this.signUpAndForgotPasswordLoading = false;
          this._snackBarService.showSnackBar(data.message || 'Password reset successful!', 3000, 'check_circle_outline');
          this._router.navigate(['/login']);
        },
        error: err => {
          this.confirmForgotPasswordLoading = false;
          if(err.error && err.error.message) {
            this._snackBarService.showSnackBar(err.error.message, 3000, 'error_outline');
          }else{
            this._snackBarService.showSnackBar("An error occurred. Please try again!", 3000, 'error_outline');
          }
        } 
      })
    }else{
      this.confirmForgotPasswordLoading = false;
      this._snackBarService.showSnackBar("Invalid data!", 3000, 'error_outline');
    }
  }

}


