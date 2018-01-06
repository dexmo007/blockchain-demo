import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";

import {AppComponent} from './app.component';
import {BlockComponent} from './block/block.component';
import {DigitsOnlyDirective} from './util/digits-only.directive';
import {HttpClientModule} from "@angular/common/http";
import {FormsModule} from "@angular/forms";
import {JwtModule} from "@auth0/angular-jwt";
import { LoginComponent } from './login/login.component';
import { HomeComponent } from './home/home.component';
import {RouterModule, Routes} from "@angular/router";
import {AuthGuard} from "./auth.guard";
import {AuthService} from "./auth.service";

export const routes: Routes = [
  { path: 'login', component: LoginComponent},
  { path: '', component: HomeComponent, canActivate: [AuthGuard]},
  { path: 'logout', redirectTo: 'login'}
];

@NgModule({
  declarations: [
    AppComponent,
    BlockComponent,
    DigitsOnlyDirective,
    LoginComponent,
    HomeComponent
  ],
  imports: [
    BrowserModule, NgbModule.forRoot(), HttpClientModule, FormsModule,
    JwtModule.forRoot({
      config: {
        tokenGetter: () => localStorage.getItem('access_token'),
        skipWhenExpired: true,
        whitelistedDomains: ['localhost:9000']
      }
    }),
    RouterModule.forRoot(routes)
  ],
  providers: [AuthGuard, AuthService],
  bootstrap: [AppComponent]
})
export class AppModule {
}
