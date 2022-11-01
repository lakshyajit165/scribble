import { Component, Inject, OnInit } from '@angular/core';
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
import { IGenericResponse } from 'src/app/model/IGenericResponse';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import * as _moment from 'moment';
const moment = _moment; 

export interface DialogData {
  fromDate: string;
  toDate: string;
}

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
    public dialog: MatDialog
  ) { 
    this.searchScribbleFormGroup = this._formBuilder.group({
      searchText: ''
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
  selectedFromDate: string = "";
  selectedToDate: string = "";

  // called during component load
  loadScribbles(searchText: string, fromDate: string, toDate: string, page: number, size: number): void {
    this._notesService.searchNotes(searchText, fromDate, toDate, page, size)
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
    this._router.navigate([`/home/scribbles/edit/${noteId}`]);
  }

  deleteNote(noteId: number): void {
    this._notesService.deleteNote(noteId).subscribe({
      next: (data: IGenericResponse) => {
        this.loadScribbles(
          this.searchScribbleFormGroup.get('searchText')?.value, 
          this.selectedFromDate ? moment(this.selectedFromDate).format("YYYY-MM-DD").toString() : "", 
          this.selectedToDate ? moment(this.selectedToDate).format("YYYY-MM-DD").toString() : "", 
          this.page,
          this.size
        )
        this._snackBarService.showSnackBar(data.message || 'Scribble deleted!', 3000, 'check_circle_outline');
      },
      error: err => {
        this._snackBarService.showSnackBar(err.error && err.error.message ? err.error.message : 'Error deleting scribble!', 3000, 'error_outline');
      }
    })
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
      this.selectedFromDate ? moment(this.selectedFromDate).format("YYYY-MM-DD").toString() : "", 
      this.selectedToDate ? moment(this.selectedToDate).format("YYYY-MM-DD").toString() : "", 
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
      this.selectedFromDate ? moment(this.selectedFromDate).format("YYYY-MM-DD").toString() : "", 
      this.selectedToDate ? moment(this.selectedToDate).format("YYYY-MM-DD").toString() : "", 
      this.page,
      this.size
    )
  }

  openDialog(): void {
    const dialogRef = this.dialog.open(DialogOverviewExampleDialog, {
      width: '400px',
      data: {fromDate: this.selectedFromDate, toDate: this.selectedToDate},
    });

    dialogRef.afterClosed().subscribe(result => {
      /**
       * Note: Angular material date picker picks just previous day's date
       * due to local timezone difference. Hence handled acc. to - 
       * https://stackoverflow.com/questions/66102300/angular-material-datepicker-shows-one-day-behind-in-angular-9-when-i-click-searc
       * 
      */
      if(result) {
        this.selectedFromDate = result.fromDate;
        const fromDatePicked = result.fromDate;
        this.selectedToDate = result.toDate;
        const toDatePicked = result.toDate;
        const formattedFromDate = fromDatePicked ? moment(fromDatePicked).format("YYYY-MM-DD").toString() : "";
        const formattedToDate = toDatePicked ? moment(toDatePicked).format("YYYY-MM-DD").toString() : "";
      

        this.loadScribbles(
          this.searchScribbleFormGroup.get('searchText')?.value,
          formattedFromDate,
          formattedToDate,
          this.page,
          this.size
        )
        }
      
    });
  }

  getNumberOfFiltersApplied(): number {
    if(this.selectedFromDate && this.selectedToDate)
      return 2;
    else if(this.selectedFromDate || this.selectedToDate){
      return 1;
    }else{ 
      return 0;
    }
  }

  getFormattedDate(datePicked: Date): string {
    if(/^[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])$/.test(datePicked.toString()))
      return datePicked.toString();
    return datePicked && 
        new Date(
          datePicked.getFullYear(), 
          datePicked.getMonth(), 
          datePicked.getDate(), 
          datePicked.getHours(), 
          datePicked.getMinutes() - datePicked.getTimezoneOffset())
          .toISOString().split('T')[0] ? 
                new Date(datePicked.getFullYear(), 
                        datePicked.getMonth(), 
                        datePicked.getDate(), 
                        datePicked.getHours(), 
                        datePicked.getMinutes() - datePicked.getTimezoneOffset())
                        .toISOString().split('T')[0] : '';
  }


}

@Component({
  selector: 'dialog-overview-example-dialog',
  templateUrl: 'dialog-overview-example-dialog.html',
})
export class DialogOverviewExampleDialog {
  constructor(
    public dialogRef: MatDialogRef<DialogOverviewExampleDialog>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData,
  ) {}

  onNoClick(): void {
    this.dialogRef.close();
  }
}
