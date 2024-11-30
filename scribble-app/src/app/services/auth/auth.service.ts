import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { IConfirmPassword } from 'src/app/model/IConfirmPassword';
import { IGenericResponse } from 'src/app/model/IGenericResponse';
import { ILogin } from 'src/app/model/ILogin';
import { ISignUpAndForgotPassword } from 'src/app/model/ISignUpAndForgotPassword';
import { catchError, map } from 'rxjs/operators';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  apiGateWay: string = 'http://localhost:9000/';
  private _isLoggedIn$ = new BehaviorSubject<boolean>(false);

  constructor(private _http: HttpClient, private _router: Router) {}

  public updateisLoggedInStatus(status: boolean): void {
    this._isLoggedIn$.next(status);
  }

  get isLoggedIn$(): Observable<boolean> {
    return this._isLoggedIn$.asObservable();
  }

  checkAuthStatus(): Observable<boolean> {
    return this._http
      .get<IGenericResponse>(
        this.apiGateWay + 'auth-service/api/v1/auth/is_loggedin'
      )
      .pipe(
        map((response) => {
          const isLoggedIn = response && response.message === 'true';
          this.updateisLoggedInStatus(isLoggedIn);
          return isLoggedIn;
        }),
        catchError(() => {
          this.updateisLoggedInStatus(false);
          return of(false);
        })
      );
  }

  signUp(
    signUpPayload: ISignUpAndForgotPassword
  ): Observable<IGenericResponse> {
    return this._http
      .post<IGenericResponse>(
        this.apiGateWay + 'auth-service/api/v1/auth/sign_up',
        signUpPayload
      )
      .pipe(map((response) => response as IGenericResponse));
  }

  confirmPassword(
    confirmPasswordPayload: IConfirmPassword
  ): Observable<IGenericResponse> {
    return this._http
      .post(
        this.apiGateWay + 'auth-service/api/v1/auth/confirm_password',
        confirmPasswordPayload
      )
      .pipe(map((response) => response as IGenericResponse));
  }

  login(loginPayload: ILogin): Observable<IGenericResponse> {
    return this._http
      .post(this.apiGateWay + 'auth-service/api/v1/auth/sign_in', loginPayload)
      .pipe(map((response) => response as IGenericResponse));
  }

  forgotPassword(
    forgotPasswordPayload: ISignUpAndForgotPassword
  ): Observable<IGenericResponse> {
    return this._http
      .post(
        this.apiGateWay + 'auth-service/api/v1/auth/forgot_password',
        forgotPasswordPayload
      )
      .pipe(map((response) => response as IGenericResponse));
  }

  confirmForgotPassword(
    confirmForgotPasswordPayload: IConfirmPassword
  ): Observable<IGenericResponse> {
    return this._http
      .post(
        this.apiGateWay + 'auth-service/api/v1/auth/confirm_forgot_password',
        confirmForgotPasswordPayload
      )
      .pipe(map((response) => response as IGenericResponse));
  }

  refreshTokens(): Observable<void> {
    return this._http
      .get(this.apiGateWay + 'auth-service/api/v1/auth/get_new_creds', {
        withCredentials: true,
      })
      .pipe(
        map(() => {
          this.updateisLoggedInStatus(true); // Update login status on successful refresh
        }),
        catchError((err) => {
          this.updateisLoggedInStatus(false); // Set login status to false if refresh fails
          return throwError(() => err);
        })
      );
  }

  logout(): Observable<IGenericResponse> {
    return this._http
      .post(this.apiGateWay + 'auth-service/api/v1/auth/logout', {})
      .pipe(map((response) => response as IGenericResponse));
  }
}
