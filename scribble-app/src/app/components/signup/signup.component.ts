import { HttpResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, FormControl, AbstractControl, FormGroupDirective, NgForm, Validators} from '@angular/forms';
import {ErrorStateMatcher} from '@angular/material/core';
import { MatStepper } from '@angular/material/stepper';
import { Router } from '@angular/router';
import { IConfirmPassword } from 'src/app/model/IConfirmPassword';
import { IGenericResponse } from 'src/app/model/IGenericResponse';
import { ILogin } from 'src/app/model/ILogin';
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
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent implements OnInit {

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

  confirmPasswordFormGroup = this._formBuilder.group({
    verificationCode: ['', Validators.required],
    password: ['', Validators.required]
  });

  signUpAndForgotPasswordPayload: ISignUpAndForgotPassword = {
    email: ''
  };
  confirmPasswordPayload: IConfirmPassword = {
    email: '',
    password: '',
    verification_code: ''
  };
  userLoginPayload: ILogin = {
    email: '',
    password: ''
  };
  signUpLoading: boolean = false;
  confirmPasswordLoading: boolean = false;

  signUpUser(stepper: MatStepper): void {
    // make loading to true
    this.signUpLoading = true;
    // get the values from both forms
    if(this.emailFormGroup.valid){
      this.signUpAndForgotPasswordPayload.email = this.emailFormGroup.get('email')?.value ?? '';
      this._authService.signUp(this.signUpAndForgotPasswordPayload).subscribe({
        next: (data: IGenericResponse) => {
          this.signUpLoading = false;
          this._snackBarService.showSnackBar(data.message, 3000, 'check_circle_outline');
          stepper.next();
        },
        error: err => {
          this.signUpLoading = false;
          if(err.error && err.error.message) {
            this._snackBarService.showSnackBar(err.error.message, 3000, 'error_outline');
          }else{
            this._snackBarService.showSnackBar("An error occurred. Please try again!", 3000, 'error_outline');
          }
        } 
      })
    }else{
      this.signUpLoading = false;
      this._snackBarService.showSnackBar("Invalid data!", 3000, 'error_outline');
    }
    
    
  }

  confirmPassword(): void {
    this.confirmPasswordLoading = true;
    if(this.confirmPasswordFormGroup.valid) {
      this.confirmPasswordPayload.email = this.emailFormGroup.get('email')?.value ?? '';
      this.confirmPasswordPayload.password = this.confirmPasswordFormGroup.get('password')?.value ?? '';
      this.confirmPasswordPayload.verification_code = this.confirmPasswordFormGroup.get('verificationCode')?.value ?? '';
      this._authService.confirmPassword(this.confirmPasswordPayload).subscribe({
        next: (data: IGenericResponse) => {
          this.confirmPasswordLoading = false;
          this._snackBarService.showSnackBar(data.message || 'SignUp successful, logging in!', 3000, 'check_circle_outline');
          /**
           * 1. call the login api
           * 2. Login success => route to dashboard and show appropriate msg
           * 3. Login fail => route to login page and show appropriate msg
           */
          this.login(this.confirmPasswordPayload.email, this.confirmPasswordPayload.password);
        },
        error: err => {
          this.confirmPasswordLoading = false;
          if(err.error && err.error.message) {
            this._snackBarService.showSnackBar(err.error.message, 3000, 'error_outline');
          }else{
            this._snackBarService.showSnackBar("An error occurred. Please try again!", 3000, 'error_outline');
          }
        } 
      })
    }else{
      this.confirmPasswordLoading = false;
      this._snackBarService.showSnackBar("Invalid data!", 3000, 'error_outline');
    }
  }

  login(email: string, password: string): void {
    this.confirmPasswordLoading = true;
    if(email && password){
      this.userLoginPayload.email = email;
      this.userLoginPayload.password = password;
      this._authService.login(this.userLoginPayload).subscribe({
        next: (data: IGenericResponse) => {
          this.confirmPasswordLoading = false;
          this._router.navigate(['/home']);
        },
        error: err => {
          this.confirmPasswordLoading = false;
          if(err.error && err.error.message) {
            this._snackBarService.showSnackBar(err.error.message, 3000, 'error_outline');
          }else{
            this._snackBarService.showSnackBar("An error occurred. Please try again!", 3000, 'error_outline');
          }
          this._router.navigate(['/login']);
        } 
      })
    }else{
      this.confirmPasswordLoading = false;
      this._snackBarService.showSnackBar("Invalid data!", 3000, 'error_outline');
      this._router.navigate(['/login']);
    }
  }

}
