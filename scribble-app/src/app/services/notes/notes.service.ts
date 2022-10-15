import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';
import { Observable, map } from 'rxjs';
import { IAddScribble } from 'src/app/model/IAddScribble';
import { IGenericNotesResponse } from 'src/app/model/IGenericNotesResponse';

@Injectable({
  providedIn: 'root'
})
export class NotesService {

  constructor(
    private _http: HttpClient,
    private _router: Router,
    private _cookieService: CookieService
  ) { }

  apiGateWay: string = 'http://localhost:9000/';

  getNoteById(noteId: string): Observable<IGenericNotesResponse<Object>> {
    return this._http.get<IGenericNotesResponse<Object>>(this.apiGateWay + `note-service/api/v1/notes/get_note/${noteId}`).pipe(
      map((response: IGenericNotesResponse<Object>) => response as IGenericNotesResponse<Object>)
    );
  }

  createNote(addScribblePayload: IAddScribble): Observable<IGenericNotesResponse<Object>> {
    return this._http.post<IGenericNotesResponse<Object>>(this.apiGateWay + "note-service/api/v1/notes/create_note", addScribblePayload).pipe(
      map((response: IGenericNotesResponse<Object>) => response as IGenericNotesResponse<Object>)
    );
  }

  updateNote(addScribblePayload: IAddScribble, noteId: string): Observable<IGenericNotesResponse<Object>> {
    return this._http.patch<IGenericNotesResponse<Object>>(this.apiGateWay + `note-service/api/v1/notes/update_note/${noteId}`, addScribblePayload).pipe(
      map((response: IGenericNotesResponse<Object>) => response as IGenericNotesResponse<Object>)
    );
  }

  deleteNote(noteId: string): Observable<IGenericNotesResponse<Object>> {
    return this._http.delete<IGenericNotesResponse<Object>>(this.apiGateWay + `note-service/api/v1/notes/delete_note/${noteId}`).pipe(
      map((response: IGenericNotesResponse<Object>) => response as IGenericNotesResponse<Object>)
    );
  }
}
