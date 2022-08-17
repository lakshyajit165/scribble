import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, FormControl, AbstractControl, FormGroupDirective, NgForm, Validators} from '@angular/forms';
import {ErrorStateMatcher} from '@angular/material/core';
import { MatStepper } from '@angular/material/stepper';

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
    private _formBuilder: FormBuilder
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
  
  verifyEmail(stepper: MatStepper): void {
    // logic to verify email => if success then go to next step
    console.log("inside function");
    stepper.next();
  }

  signUpUser(): void {
    // get the values from both forms
    if(this.emailFormGroup.valid && this.confirmPasswordFormGroup.valid){
      console.log(this.emailFormGroup.get('email')?.value);
      console.log(this.confirmPasswordFormGroup.get('verificationCode')?.value, 
      this.confirmPasswordFormGroup.get('password')?.value);
    }
    
    
  }

}
