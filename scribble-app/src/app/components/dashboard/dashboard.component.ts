import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, FormControl, AbstractControl, FormGroupDirective, NgForm, Validators} from '@angular/forms';
import { IScribble } from 'src/app/model/IScribble';
import { switchMap, debounceTime } from 'rxjs/operators';
import { NotesService } from 'src/app/services/notes/notes.service';
import { Observable } from 'rxjs';
import { ISearchNotesResponse } from 'src/app/model/ISearchNotesResponse';
import { SnackbarService } from 'src/app/utils/snackbar.service';
import { IScribbleResponseObject } from 'src/app/model/IScribbleResponseObject';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  constructor(
    private _formBuilder: FormBuilder,
    private _notesService: NotesService,
    private _snackBarService: SnackbarService
  ) { 
    this.searchScribbleFormGroup = this._formBuilder.group({
      searchText: '',
      updatedOnOrAfter: '',
      updatedOnOrBefore: ''
    });
  }

  ngOnInit(): void {
    this.loadScribbles();
    // called during search filter(s) value change
    this.searchScribbleFormGroup.valueChanges.pipe(
        debounceTime(500),
        switchMap(fields => this._notesService.searchNotes(
          fields['searchText'],
          fields['updatedOnOrAfter'],
          fields['updatedOnOrBefore'],
          this.page,
          this.size
        )
    )).subscribe({
      next: (data: ISearchNotesResponse) => {
        this.searchScribbleLoading = false;
        // process data
        this.scribbles = data['content'];
        console.log("DEBOUNCED CALL: ", this.scribbles);
      },
      error: err => {
        this.searchScribbleLoading = false;
        if(err.error && err.error.message) {
          this._snackBarService.showSnackBar(err.error.message, 3000, 'error_outline');
        }else{
          this._snackBarService.showSnackBar("An error occurred. Please try again!", 3000, 'error_outline');
        }
      } 
    });		
  }
  page: number = 0;
  size: number = 9;
  searchScribbleFormGroup: FormGroup;
  searchScribbleLoading: boolean = true;
  scribbles: IScribbleResponseObject[] = [];

  // called during component load
  loadScribbles(): void {
    this._notesService.searchNotes('', '', '', this.page, this.size)
    .subscribe({
      next: (data: ISearchNotesResponse) => {
        this.searchScribbleLoading = false;
        // process data
        this.scribbles = data['content'];
        console.log("DEFAULT CALL: ", this.scribbles);
      },
      error: err => {
        this.searchScribbleLoading = false;
        if(err.error && err.error.message) {
          this._snackBarService.showSnackBar(err.error.message, 3000, 'error_outline');
        }else{
          this._snackBarService.showSnackBar("An error occurred. Please try again!", 3000, 'error_outline');
        }
      } 
    });		
  }
  // searchScribbleResponse: Observable<ISearchNotesResponse>;

  // this.searchScribbleFormGroup.valueChanges.pipe(
  //   debounceTime(500),
  //   switchMap(fields => this.apiService.search(
  //     fields['term'],
  //     fields['filter'],
  //     fields['sort'])
  //   )
  // );	
  

  
}
