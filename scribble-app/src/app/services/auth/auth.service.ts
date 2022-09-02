import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { IConfirmPassword } from 'src/app/model/IConfirmPassword';
import { IGenericAuthResponse } from 'src/app/model/IGenericAuthResponse';
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
  isUserLoggedIn: BehaviorSubject<boolean>;
  
  constructor(
    private _http: HttpClient,
    private _router: Router,
    private _cookieService: CookieService
  ) { 
    this.isUserLoggedIn = new BehaviorSubject<boolean>(false);
  }

  public get loggedInStatus(): boolean {
    return this.isUserLoggedIn.value;
  }

  public setLoggedInStatus(status: boolean): void {
    this.isUserLoggedIn.next(status);
  }
  
  
  signUp(signUpPayload: ISignUpAndForgotPassword): Observable<IGenericAuthResponse> {
    return this._http.post<IGenericAuthResponse>(this.apiGateWay + "auth-service/api/v1/auth/sign_up", signUpPayload).pipe(
      map(response => response as IGenericAuthResponse)
    );
  }

  confirmPassword(confirmPasswordPayload: IConfirmPassword): Observable<IGenericAuthResponse> {
    return this._http.post(this.apiGateWay + "auth-service/api/v1/auth/confirm_password", confirmPasswordPayload).pipe(
      map(response => response as IGenericAuthResponse)
    );
  }

  login(loginPayload: ILogin): Observable<IGenericAuthResponse> {
    return this._http.post(this.apiGateWay + "auth-service/api/v1/auth/sign_in", loginPayload, ).pipe(
      map(response => {
        const authReponse = response as IGenericAuthResponse;
        const expirationTime: Date = new Date();
        expirationTime.setHours(expirationTime.getHours() + 1);
        if(authReponse.message === "User signed in."){
          this.isUserLoggedIn.next(true);
          // this._cookieService.set('userLoggedIn', 'true', { expires: expirationTime});
          // console.log(this._cookieService.get('userLoggedIn'));
          return authReponse;
        }else{
          this.isUserLoggedIn.next(false);
          return authReponse;
        }
      })
    );
  }

  forgotPassword(forgotPasswordPayload: ISignUpAndForgotPassword): Observable<IGenericAuthResponse> {
    return this._http.post(this.apiGateWay + "auth-service/api/v1/auth/forgot_password", forgotPasswordPayload).pipe(
      map(response => response as IGenericAuthResponse)
    );
  }

  confirmForgotPassword(confirmForgotPasswordPayload: IConfirmPassword): Observable<IGenericAuthResponse> {
    return this._http.post(this.apiGateWay + "auth-service/api/v1/auth/confirm_forgot_password", confirmForgotPasswordPayload).pipe(
      map(response => response as IGenericAuthResponse)
    );
  }

  getNewCreds(): Observable<IGenericAuthResponse> {
    return this._http.get(this.apiGateWay + "auth-service/api/v1/auth/get_new_creds").pipe(
      map(response => response as IGenericAuthResponse)
    );
  }

  logout(): void {
    this._http.post(this.apiGateWay + "auth-service/api/v1/auth/logout", {}).subscribe();
    this.isUserLoggedIn.next(false);
    // this._cookieService.delete('userLoggedIn');
    this._router.navigate(['/login']);
  }


}
