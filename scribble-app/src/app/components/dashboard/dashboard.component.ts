import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, FormControl, AbstractControl, FormGroupDirective, NgForm, Validators} from '@angular/forms';
import { INote } from 'src/app/model/INote';
import { of, Subscription, throwError } from 'rxjs';
import { switchMap, debounceTime, catchError, distinctUntilChanged } from 'rxjs/operators';
import { NotesService } from 'src/app/services/notes/notes.service';
import { Observable } from 'rxjs';
import { ISearchNotesResponse } from 'src/app/model/ISearchNotesResponse';
import { SnackbarService } from 'src/app/utils/snackbar.service';
import { INoteResponseObject } from 'src/app/model/INoteResponseObject';
import { Router } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  debounceSubcription: Subscription = new Subscription();
  constructor(
    private _formBuilder: FormBuilder,
    private _notesService: NotesService,
    private _snackBarService: SnackbarService,
    private _router: Router,
  ) { 
    this.searchScribbleFormGroup = this._formBuilder.group({
      searchText: '',
      fromDate: '',
      toDate: ''
    });
  }
  
  
  ngOnInit(): void {
    // load scribbles on component load
    this.loadScribbles('', '', '', this.page, this.size);
    // load scribbles based on user input
    this.loadScribblesByDebounce();
  }
  page: number = 0;
  size: number = 9;
  lastPage: boolean = true;
  totalElements: number = 0;
  totalPages: number = 0;
  searchScribbleFormGroup: FormGroup;
  loading: boolean = true;
  loadingMessage: string = "Loading Notes...";
  notes: INoteResponseObject[] = [];

  // called during component load
  loadScribbles(searchText: string, updatedOnOrBefore: string, updatedOnOrAfter: string, page: number, size: number): void {
    this._notesService.searchNotes(searchText, updatedOnOrBefore, updatedOnOrAfter, page, size)
    .subscribe({
      next: (data: ISearchNotesResponse) => {
        this.loading = false;
        // process data
        this.notes = data["content"];
        this.page = data["page"];
        this.size = data["size"];
        this.totalElements = data["totalElements"];
        this.totalPages = data["totalPages"];
        this.lastPage = data["last"];
      },
      error: err => {
        this.loading = false;
        this.notes = [];
        if(err.error && err.error.message) {
          this._snackBarService.showSnackBar("Error fetching notes!", 3000, 'error_outline');
        }else{
          this._snackBarService.showSnackBar("Error fetching notes!", 3000, 'error_outline');
        }
      } 
    });		
  }

  // called when user input search text changes
  loadScribblesByDebounce(): void {
    this.loading = true;
    this.debounceSubcription = this.searchScribbleFormGroup.valueChanges.pipe(
      debounceTime(500),
      distinctUntilChanged(),
      switchMap(fields => 
        this._notesService.searchNotes(
          fields['searchText'],
          fields['fromDate'] ? fields['fromDate'] : '',
          fields['toDate'] ? fields['toDate'] : '',
          this.page,
          this.size
        )
  )).subscribe({
    next: (data: ISearchNotesResponse) => {
      this.loading = false;
      // process data
      this.notes = data['content'];
      this.page = data["page"];
      this.size = data["size"];
      this.totalElements = data["totalElements"];
      this.totalPages = data["totalPages"];
      this.lastPage = data["last"];
    },
    error: err => {
      this.loading = false;
      this.notes = [];
      // unsubscribe b4 subscribing again.
      this.unSubscribeSearchDebounce();
      /**
       * 
In an Observable Execution, zero to infinite Next notifications may be delivered.
 If either an Error or Complete notification is delivered, then nothing else can be delivered afterwards.
 Hence the recursive call to subscribe to value changes again is there.
       */
      this.loadScribblesByDebounce();
      if(err.error && err.error.message) {
        this._snackBarService.showSnackBar(err.error.message, 3000, 'error_outline');
      }else{
        this._snackBarService.showSnackBar("An error occurred. Please try again!", 3000, 'error_outline');
      }
    }
  })
  }

  ngOnDestroy(): void {
    this.unSubscribeSearchDebounce();
  }

  unSubscribeSearchDebounce(): void {
    if(this.debounceSubcription){
      this.debounceSubcription.unsubscribe();
    }
  }

  editNote(noteId: number): void {
    console.log("edit note: " + noteId);
  }

  deleteNote(noteId: number): void {
    console.log("delete note: " + noteId);
  }

  
  test(noteId: number): void {
    console.log(noteId);
  }

  goToRoute(route: string): void {
    this._router.navigate([route]);
  }

  previousPage(): void {
    if(this.page === 0){
      this._snackBarService.showSnackBar("This is the first page!", 3000, 'error_outline');
      return;
    }
    this.page = this.page - 1;
    this.loadScribbles(
      this.searchScribbleFormGroup.get('searchText')?.value, 
      this.searchScribbleFormGroup.get('fromDate')?.value, 
      this.searchScribbleFormGroup.get('toDate')?.value, 
      this.page,
      this.size
    )
  }

  nextPage(): void {
    if(this.page === this.totalPages - 1 && this.lastPage){
      this._snackBarService.showSnackBar("This is the last page!", 3000, 'error_outline');
      return;
    }
    this.page = this.page + 1;
    this.loadScribbles(
      this.searchScribbleFormGroup.get('searchText')?.value, 
      this.searchScribbleFormGroup.get('fromDate')?.value, 
      this.searchScribbleFormGroup.get('toDate')?.value, 
      this.page,
      this.size
    )
  }
}
