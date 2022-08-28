import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { IConfirmPassword } from 'src/app/model/IConfirmPassword';
import { IGenericAuthResponse } from 'src/app/model/IGenericAuthResponse';
import { ILogin } from 'src/app/model/ILogin';
import { ISignUpAndForgotPassword } from 'src/app/model/ISignUpAndForgotPassword';
import { map } from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  apiGateWay: string = 'http://localhost:9000/'
  constructor(
    private http: HttpClient
  ) { }
  
  signUp(signUpPayload: ISignUpAndForgotPassword): Observable<IGenericAuthResponse> {
    return this.http.post<IGenericAuthResponse>(this.apiGateWay + "auth-service/api/v1/auth/sign_up", signUpPayload).pipe(
      map(response => response as IGenericAuthResponse)
    );
  }

  confirmPassword(confirmPasswordPayload: IConfirmPassword): Observable<IGenericAuthResponse> {
    return this.http.post(this.apiGateWay + "auth-service/api/v1/auth/confirm_password", confirmPasswordPayload).pipe(
      map(response => response as IGenericAuthResponse)
    );
  }

  login(loginPayload: ILogin): Observable<IGenericAuthResponse> {
    return this.http.post(this.apiGateWay + "auth-service/api/v1/auth/sign_in", loginPayload).pipe(
      map(response => response as IGenericAuthResponse)
    );
  }

  forgotPassword(forgotPasswordPayload: ISignUpAndForgotPassword): Observable<IGenericAuthResponse> {
    return this.http.post(this.apiGateWay + "auth-service/api/v1/auth/forgot_password", forgotPasswordPayload).pipe(
      map(response => response as IGenericAuthResponse)
    );
  }

  confirmForgotPassword(confirmForgotPasswordPayload: IConfirmPassword): Observable<IGenericAuthResponse> {
    return this.http.post(this.apiGateWay + "auth-service/api/v1/auth/confirm_forgot_password", confirmForgotPasswordPayload).pipe(
      map(response => response as IGenericAuthResponse)
    );
  }

  getNewCreds(): Observable<IGenericAuthResponse> {
    return this.http.get(this.apiGateWay + "auth-service/api/v1/auth/get_new_creds").pipe(
      map(response => response as IGenericAuthResponse)
    );
  }

  logout(): Observable<IGenericAuthResponse> {
    return this.http.post(this.apiGateWay + "auth-service/api/v1/auth/logout", {}).pipe(
      map(response => response as IGenericAuthResponse)
    );
  }

}
