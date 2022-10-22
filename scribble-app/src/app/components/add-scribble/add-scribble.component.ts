import { Component, OnInit } from '@angular/core';
import { CookieService } from 'ngx-cookie-service';
import { NotesService } from 'src/app/services/notes/notes.service';
import {FormBuilder, FormGroup, FormControl, AbstractControl, FormGroupDirective, NgForm, Validators} from '@angular/forms';
import {ErrorStateMatcher} from '@angular/material/core';
import { INote } from 'src/app/model/INote';
import { SnackbarService } from 'src/app/utils/snackbar.service';
import { IGenericAuthResponse } from 'src/app/model/IGenericAuthResponse';
import { Router } from '@angular/router';

/** Error when invalid control is dirty, touched, or submitted. */
export class CustomErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: FormControl, form: NgForm | FormGroupDirective | null) {
    return control && control.invalid && control.touched;
  }
}

@Component({
  selector: 'app-add-scribble',
  templateUrl: './add-scribble.component.html',
  styleUrls: ['./add-scribble.component.css']
})
export class AddScribbleComponent implements OnInit {

  constructor(
    private _formBuilder: FormBuilder,
    private _snackBarService: SnackbarService,
    private _notesService: NotesService,
    private _router: Router
  ) {
   
   }

  ngOnInit(): void {
  }

  _matcher = new CustomErrorStateMatcher();
  addScribbleLoading: boolean = false;

  addScribblePayload: INote = {
    title: '',
    description: '',
    label: '',
    dueDate: ''
  };

  addScribbleFormGroup = this._formBuilder.group({
    title: '',
    description: ['', [Validators.required]],
    label: '',
    dueDate: ''
  });

  addScribble(): void {
    this.addScribbleLoading = true;
    // get the values from both forms
    if(this.addScribbleFormGroup.valid){
      this.addScribblePayload.title = this.addScribbleFormGroup.get('title')?.value ?? '';
      this.addScribblePayload.description = this.addScribbleFormGroup.get('description')?.value ?? '';
      this.addScribblePayload.label = this.addScribbleFormGroup.get('label')?.value ?? '';
      this.addScribblePayload.dueDate = this.addScribbleFormGroup.get('dueDate')?.value ?? '';
      this._notesService.createNote(this.addScribblePayload).subscribe({
        next: (data: IGenericAuthResponse) => {
          this.addScribbleLoading = false;
          this._snackBarService.showSnackBar("Scribble added!", 3000, 'check_circle_outline');
          this._router.navigate(['/home']);
        },
        error: err => {
          this.addScribbleLoading = false;
          if(err.error && err.error.message) {
            this._snackBarService.showSnackBar(err.error.message, 3000, 'error_outline');
          }else{
            this._snackBarService.showSnackBar("An error occurred. Please try again!", 3000, 'error_outline');
          }
        } 
      })
    }else{
      this.addScribbleLoading = false;
      this._snackBarService.showSnackBar("Missing fields or Invalid data!", 3000, 'error_outline');
    }
  }
}
