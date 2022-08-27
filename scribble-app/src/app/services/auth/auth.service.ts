import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { IConfirmPassword } from 'src/app/model/IConfirmPassword';
import { ILogin } from 'src/app/model/ILogin';
import { ISignUpAndForgotPassword } from 'src/app/model/ISignUpAndForgotPassword';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  apiGateWay: string = 'http://localhost:9000/'
  constructor(
    private http: HttpClient
  ) { }
  
  signUp(signUpPayload: ISignUpAndForgotPassword): Observable<object> {
    return this.http.post(this.apiGateWay + "auth-service/api/v1/auth/sign_up", signUpPayload);
  }

  confirmPassword(confirmPasswordPayload: IConfirmPassword): Observable<object> {
    return this.http.post(this.apiGateWay + "auth-service/api/v1/auth/confirm_password", confirmPasswordPayload);
  }

  login(loginPayload: ILogin): Observable<object> {
    return this.http.post(this.apiGateWay + "auth-service/api/v1/auth/sign_in", loginPayload);
  }

  forgotPassword(forgotPasswordPayload: ISignUpAndForgotPassword): Observable<object> {
    return this.http.post(this.apiGateWay + "auth-service/api/v1/auth/forgot_password", forgotPasswordPayload);
  }

  confirmForgotPassword(confirmForgotPasswordPayload: IConfirmPassword): Observable<object> {
    return this.http.post(this.apiGateWay + "auth-service/api/v1/auth/confirm_forgot_password", confirmForgotPasswordPayload);
  }

  getNewCreds(): Observable<object> {
    return this.http.get(this.apiGateWay + "auth-service/api/v1/auth/get_new_creds");
  }

  logout(): Observable<object> {
    return this.http.post(this.apiGateWay + "auth-service/api/v1/auth/logout", {});
  }

}
