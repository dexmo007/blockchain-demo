import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {AngularFontAwesomeModule} from 'angular-font-awesome';

import {AppComponent} from './app.component';
import {BlockComponent} from './block/block.component';
import {DigitsOnlyDirective} from './util/digits-only.directive';
import {HttpClientModule} from "@angular/common/http";
import {FormsModule} from "@angular/forms";
import {JwtModule} from "@auth0/angular-jwt";
import {LoginComponent} from './login/login.component';
import {HomeComponent} from './home/home.component';
import {RouterModule, Routes} from "@angular/router";
import {AuthGuard} from "./auth.guard";
import {AuthService, LoginModalComponent, retrieveToken} from "./auth.service";
import {BlockchainComponent} from './blockchain/blockchain.component';

export const routes: Routes = [
  {path: 'login', component: LoginComponent},
  {path: '', component: HomeComponent, canActivate: [AuthGuard]},
  {path: 'logout', redirectTo: 'login'}
];

@NgModule({
  declarations: [
    AppComponent,
    BlockComponent,
    DigitsOnlyDirective,
    LoginComponent,
    HomeComponent,
    BlockchainComponent,
    LoginModalComponent
  ],
  imports: [
    BrowserModule, NgbModule.forRoot(), HttpClientModule, FormsModule,
    JwtModule.forRoot({
      config: {
        tokenGetter: retrieveToken,
        skipWhenExpired: true,
        whitelistedDomains: ['localhost:9000']
      }
    }),
    RouterModule.forRoot(routes)
  ],
  providers: [AuthService, AuthGuard],
  bootstrap: [AppComponent],
  entryComponents: [LoginModalComponent]
})
export class AppModule {
}
