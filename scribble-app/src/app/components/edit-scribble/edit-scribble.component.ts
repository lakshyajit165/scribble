import { Component, OnInit } from '@angular/core';
import { FormControl, NgForm, FormGroupDirective, FormBuilder, Validators } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { IGenericNotesResponse } from '../../model/IGenericNotesResponse';
import { ActivatedRoute, Router } from '@angular/router';
import { INote } from 'src/app/model/INote';
import { NotesService } from 'src/app/services/notes/notes.service';
import { SnackbarService } from 'src/app/utils/snackbar.service';
import { INoteResponseObject } from 'src/app/model/INoteResponseObject';
import { IGenericResponse } from 'src/app/model/IGenericResponse';

/** Error when invalid control is dirty, touched, or submitted. */
export class CustomErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: FormControl, form: NgForm | FormGroupDirective | null) {
    return control && control.invalid && control.touched;
  }
}

@Component({
  selector: 'app-edit-scribble',
  templateUrl: './edit-scribble.component.html',
  styleUrls: ['./edit-scribble.component.css']
})
export class EditScribbleComponent implements OnInit {

  constructor(
    private _formBuilder: FormBuilder,
    private _snackBarService: SnackbarService,
    private _notesService: NotesService,
    private _router: Router,
    private _activatedRoute: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.getScribbleLoading = true;
    this._activatedRoute.params.subscribe(value => {
      this.noteId = value["id"];
      // validate the id here
      if(this.scribbleIdRegex.test(value["id"])){
        // make api call to get the scribble if id is valid
        this._notesService.getNoteById(value["id"]).subscribe({
          next: (data: IGenericNotesResponse<INoteResponseObject>) => {
            this.getScribbleLoading = false;
            this.noteId = data["data"]["id"];
            this.editScribbleFormGroup.controls["title"].setValue(data["data"]["title"]);
            this.editScribbleFormGroup.controls["description"].setValue(data["data"]["description"]);
            this.editScribbleFormGroup.controls["label"].setValue(data["data"]["label"]);
            this.editScribbleFormGroup.controls["dueDate"].setValue(data["data"]["dueDate"]);
          },
          error: err => {
            this.getScribbleLoading = false;
            this.getScribbleError = true;
            this.getScribbleErrorMessage = err.error && err.error.message ? err.error.message : "Failed to fetch scribble with this id!";
          }
        })
      }else{
        this.getScribbleLoading = false;
        this.getScribbleError = true;
        this.getScribbleErrorMessage = "Failed to validate scribble id!";
      }
      
    });
  }

  _matcher = new CustomErrorStateMatcher();
  scribbleIdRegex = /^\d+$/;
  editScribbleLoading: boolean = false;
  getScribbleLoading: boolean = false;
  getScribbleError: boolean = false;
  getScribbleErrorMessage: string = "";
  noteId: number = -1;
 
  editScribblePayload: INote = {
    title: '',
    description: '',
    label: '',
    dueDate: ''
  };

  editScribbleFormGroup = this._formBuilder.group({
    title: '',
    description: ['', [Validators.required]],
    label: '',
    dueDate: ''
  });

  editScribble(): void {
    this.editScribbleLoading = true;
    // get the values from both forms
    if(this.editScribbleFormGroup.valid){
      this.editScribblePayload.title = this.editScribbleFormGroup.get('title')?.value ?? '';
      this.editScribblePayload.description = this.editScribbleFormGroup.get('description')?.value ?? '';
      this.editScribblePayload.label = this.editScribbleFormGroup.get('label')?.value ?? '';
      this.editScribblePayload.dueDate = this.editScribbleFormGroup.get('dueDate')?.value ?? '';
      this._notesService.updateNote(this.editScribblePayload, this.noteId).subscribe({
        next: (data: IGenericNotesResponse<Object>) => {
          this.editScribbleLoading = false;
          this._snackBarService.showSnackBar("Scribble edited!", 3000, 'check_circle_outline');
          this._router.navigate(['/home']);
        },
        error: err => {
          this.editScribbleLoading = false;
          if(err.error && err.error.message) {
            this._snackBarService.showSnackBar(err.error.message, 3000, 'error_outline');
          }else{
            this._snackBarService.showSnackBar("An error occurred. Please try again!", 3000, 'error_outline');
          }
        } 
      })
    }else{
      this.editScribbleLoading = false;
      this._snackBarService.showSnackBar("Missing fields or Invalid data!", 3000, 'error_outline');
    }
  }

}
