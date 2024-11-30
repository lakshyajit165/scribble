import { APP_INITIALIZER, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HomeComponent } from './components/home/home.component';
import { LayoutModule } from '@angular/cdk/layout';
import { LoginComponent } from './components/login/login.component';
import { SignupComponent } from './components/signup/signup.component';
import { MaterialModule } from './material/material.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { SnackbarComponent } from './components/snackbar/snackbar.component';
import { TruncatePipe } from './utils/truncate.pipe';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password.component';
import { AuthGuard } from './utils/auth.guard';
import { AuthRouteGuard } from './utils/authroute.guard';
import { HttpRequestInterceptor } from './providers/http.interceptor';
import { AddScribbleComponent } from './components/add-scribble/add-scribble.component';
import {
  DashboardComponent,
  DialogOverviewExampleDialog,
} from './components/dashboard/dashboard.component';
import { EditScribbleComponent } from './components/edit-scribble/edit-scribble.component';
import { AuthService } from './services/auth/auth.service';

export function initializeAuth(authService: AuthService): () => void {
  return () => authService.checkAuthStatus();
}

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    LoginComponent,
    SignupComponent,
    SnackbarComponent,
    TruncatePipe,
    ForgotPasswordComponent,
    AddScribbleComponent,
    DashboardComponent,
    EditScribbleComponent,
    DialogOverviewExampleDialog,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    LayoutModule,
    MaterialModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
  ],
  providers: [
    {
      provide: APP_INITIALIZER,
      useFactory: initializeAuth,
      deps: [AuthService],
      multi: true,
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpRequestInterceptor,
      multi: true,
    },
    AuthGuard,
    AuthRouteGuard,
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
