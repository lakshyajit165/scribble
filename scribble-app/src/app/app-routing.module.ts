import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AddScribbleComponent } from './components/add-scribble/add-scribble.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { EditScribbleComponent } from './components/edit-scribble/edit-scribble.component';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password.component';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { SignupComponent } from './components/signup/signup.component';
import { AuthGuard } from './utils/auth.guard';
import { AuthRouteGuard } from './utils/authroute.guard';

const routes: Routes = [
  { path: '', redirectTo: 'home/dashboard', pathMatch: 'full' },
  {
    path: 'home',
    component: HomeComponent,
    canActivate: [AuthGuard],
    children: [
      {
        path: '', redirectTo: 'dashboard', pathMatch: 'full'
      },
      {
        path: 'dashboard', component: DashboardComponent, canActivateChild: [AuthGuard]
      },
      {
        path: 'scribbles/add', component: AddScribbleComponent, canActivateChild: [AuthGuard]
      },
      {
        path: 'scribbles/edit/:id', component: EditScribbleComponent, canActivateChild: [AuthGuard]
      }
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
  /***
   * Added hash routing due to the following error
   * https://stackoverflow.com/questions/56213079/404-error-on-page-refresh-with-angular-7-nginx-and-docker
   */
  imports: [RouterModule.forRoot(routes, { useHash: true })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
