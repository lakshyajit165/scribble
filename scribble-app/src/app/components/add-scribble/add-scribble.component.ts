import { Component, OnInit } from '@angular/core';
import { CookieService } from 'ngx-cookie-service';
import { NotesService } from 'src/app/services/notes/notes.service';

@Component({
  selector: 'app-add-scribble',
  templateUrl: './add-scribble.component.html',
  styleUrls: ['./add-scribble.component.css']
})
export class AddScribbleComponent implements OnInit {

  constructor(
    private _cookieService: CookieService,
    private _notesService: NotesService
  ) {
    console.log(this._cookieService.get('user_profile'));
    this._notesService.getNoteById().subscribe({
      next: response => {
        console.log(response);
      },
      error: err => {
        console.log("inside add component " + err);
      }
    })
   }

  ngOnInit(): void {
  }

}
