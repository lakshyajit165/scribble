import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AddScribbleComponent } from './components/add-scribble/add-scribble.component';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password.component';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { SignupComponent } from './components/signup/signup.component';
import { AuthGuard } from './utils/auth.guard';
import { AuthRouteGuard } from './utils/authroute.guard';

const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  {
    path: 'home',
    component: HomeComponent,
    canActivate: [AuthGuard],
    children: [
      {
        path: 'scribbles/add', component: AddScribbleComponent, canActivateChild: [AuthGuard]
      },
    ]
  },
  {
    path: 'login',
    component: LoginComponent,
    canActivate: [AuthRouteGuard]
  },
  {
    path: 'signup',
    component: SignupComponent,
    canActivate: [AuthRouteGuard]
  },
  {
    path: 'forgot-password',
    component: ForgotPasswordComponent,
    canActivate: [AuthRouteGuard]
  }


];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
