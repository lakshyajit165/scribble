import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';
import { Observable, map } from 'rxjs';
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

  getNoteById(): Observable<IGenericNotesResponse<Object>> {
    return this._http.get<IGenericNotesResponse<Object>>(this.apiGateWay + "note-service/api/v1/notes/get_note/2").pipe(
      map((response: IGenericNotesResponse<Object>) => response as IGenericNotesResponse<Object>)
    );
  }
}
