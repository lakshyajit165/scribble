import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, map } from 'rxjs';
import { INote } from 'src/app/model/INote';
import { IGenericNotesResponse } from 'src/app/model/IGenericNotesResponse';
import { ISearchNotesResponse } from 'src/app/model/ISearchNotesResponse';
import { INoteResponseObject } from 'src/app/model/INoteResponseObject';
import { IGenericResponse } from 'src/app/model/IGenericResponse';

@Injectable({
  providedIn: 'root',
})
export class NotesService {
  constructor(private _http: HttpClient, private _router: Router) {}

  apiGateWay: string = 'http://localhost:9000/';

  searchNotes(
    searchText: string,
    fromDate: string,
    toDate: string,
    page: number,
    size: number
  ): Observable<ISearchNotesResponse> {
    return this._http
      .get<ISearchNotesResponse>(
        this.apiGateWay +
          `note-service/api/v1/notes/search?searchText=${searchText}&updatedOnOrAfter=${fromDate}&updatedOnOrBefore=${toDate}&page=${page}&size=${size}`
      )
      .pipe(
        map(
          (response: ISearchNotesResponse) => response as ISearchNotesResponse
        )
      );
  }

  getNoteById(
    noteId: string
  ): Observable<IGenericNotesResponse<INoteResponseObject>> {
    return this._http
      .get<IGenericNotesResponse<INoteResponseObject>>(
        this.apiGateWay + `note-service/api/v1/notes/get_note/${noteId}`
      )
      .pipe(
        map(
          (response: IGenericNotesResponse<INoteResponseObject>) =>
            response as IGenericNotesResponse<INoteResponseObject>
        )
      );
  }

  createNote(
    addScribblePayload: INote
  ): Observable<IGenericNotesResponse<INoteResponseObject>> {
    return this._http
      .post<IGenericNotesResponse<INoteResponseObject>>(
        this.apiGateWay + 'note-service/api/v1/notes/create_note',
        addScribblePayload
      )
      .pipe(
        map(
          (response: IGenericNotesResponse<INoteResponseObject>) =>
            response as IGenericNotesResponse<INoteResponseObject>
        )
      );
  }

  updateNote(
    addScribblePayload: INote,
    noteId: number
  ): Observable<IGenericNotesResponse<INoteResponseObject>> {
    return this._http
      .patch<IGenericNotesResponse<INoteResponseObject>>(
        this.apiGateWay + `note-service/api/v1/notes/update_note/${noteId}`,
        addScribblePayload
      )
      .pipe(
        map(
          (response: IGenericNotesResponse<INoteResponseObject>) =>
            response as IGenericNotesResponse<INoteResponseObject>
        )
      );
  }

  deleteNote(noteId: number): Observable<IGenericResponse> {
    return this._http
      .delete<IGenericResponse>(
        this.apiGateWay + `note-service/api/v1/notes/delete_note/${noteId}`
      )
      .pipe(map((response: IGenericResponse) => response as IGenericResponse));
  }
}
