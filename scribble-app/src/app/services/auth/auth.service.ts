import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { IConfirmPassword } from 'src/app/model/IConfirmPassword';
import { IGenericResponse } from 'src/app/model/IGenericResponse';
import { ILogin } from 'src/app/model/ILogin';
import { ISignUpAndForgotPassword } from 'src/app/model/ISignUpAndForgotPassword';
import { map } from "rxjs/operators";
import { Router } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';


@Injectable({
  providedIn: 'root'
})
export class AuthService {

  apiGateWay: string = 'http://localhost:9000/';
  
  constructor(
    private _http: HttpClient,
    private _router: Router,
    private _cookieService: CookieService
  ) { 
  }

  public isUserLoggedIn(): boolean {
    return this._cookieService.get('user_profile') ? true : false;
  }
  
  
  signUp(signUpPayload: ISignUpAndForgotPassword): Observable<IGenericResponse> {
    return this._http.post<IGenericResponse>(this.apiGateWay + "auth-service/api/v1/auth/sign_up", signUpPayload).pipe(
      map(response => response as IGenericResponse)
    );
  }

  confirmPassword(confirmPasswordPayload: IConfirmPassword): Observable<IGenericResponse> {
    return this._http.post(this.apiGateWay + "auth-service/api/v1/auth/confirm_password", confirmPasswordPayload).pipe(
      map(response => response as IGenericResponse)
    );
  }

  login(loginPayload: ILogin): Observable<IGenericResponse> {
    return this._http.post(this.apiGateWay + "auth-service/api/v1/auth/sign_in", loginPayload).pipe(
      map(response => response as IGenericResponse)
    );
  }

  forgotPassword(forgotPasswordPayload: ISignUpAndForgotPassword): Observable<IGenericResponse> {
    return this._http.post(this.apiGateWay + "auth-service/api/v1/auth/forgot_password", forgotPasswordPayload).pipe(
      map(response => response as IGenericResponse)
    );
  }

  confirmForgotPassword(confirmForgotPasswordPayload: IConfirmPassword): Observable<IGenericResponse> {
    return this._http.post(this.apiGateWay + "auth-service/api/v1/auth/confirm_forgot_password", confirmForgotPasswordPayload).pipe(
      map(response => response as IGenericResponse)
    );
  }

  getNewCreds(): void {
   this._http.get(this.apiGateWay + "auth-service/api/v1/auth/get_new_creds").subscribe();
  }

  logout(): Observable<IGenericResponse> {
    return this._http.post(this.apiGateWay + "auth-service/api/v1/auth/logout", {}).pipe(
      map(response => response as IGenericResponse)
    );
  }


}
