import {Component, Injectable, Input} from '@angular/core';
import {JwtHelperService} from '@auth0/angular-jwt';
import {NgbActiveModal, NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {HttpClient} from "@angular/common/http";
import 'rxjs/add/operator/map'
import {Observable} from "rxjs/Observable";

export const tokenName = 'access_token';

export interface JwtToken {
  access_token: string;
  token_type: string;
  expires_in: number;
  scope: string;
  jti: string;
}

export interface TokenData {
  aud: string[];
  user_name: string;
  scope: string[];
  exp: number;
  authorities: string[];
  jti: string;
  client_id: string;
}

@Injectable()
export class AuthService {

  private tokenData: TokenData = null;
  private loginModalActive = false;

  constructor(private jwt: JwtHelperService, private modalService: NgbModal, private http: HttpClient) {
    // check if token is still in localStorage, then we authenticate silently
    const fromStorage = localStorage.getItem(tokenName);
    if (fromStorage != null) {
      this.tokenData = AuthService.decode(fromStorage);
      if (this.isExpired() || true) {
        this.loginModalActive = true;
        this.logout();
        // redirect is sub-optimal
        this.showLoginModal();
      }
    }
    console.log('expired: ' + this.isExpired());
  }

  showLoginModal() {
    const modalRef = this.modalService.open(LoginModalComponent, {backdrop: "static"});
    modalRef.componentInstance.auth = this;
    modalRef.result.then(
      () => this.loginModalActive = false,
      () => this.loginModalActive = false);
  }

  canActivate(): boolean {
    return this.loginModalActive || this.isAuthenticated();
  }

  isAuthenticated(): boolean {
    if (this.tokenData != null) {
      return true;
    }
    // check if token is still in localStorage, then we authenticate silently
    const fromStorage = localStorage.getItem(tokenName);
    if (fromStorage != null) {
      this.tokenData = AuthService.decode(fromStorage);
      return true;
    }
    return false;
  }

  logout() {
    localStorage.removeItem(tokenName);
    this.tokenData = null;
  }

  login(username: string, password: string): Observable<TokenData> {
    return this.http.post(`/oauth/token?username=${username}&password=${password}&grant_type=password`,
      null,
      {
        headers: {
          "Authorization": "Basic " + btoa("08154711:supersecret")
        }
      }).map((jwtToken: JwtToken) => {
      const token = jwtToken.access_token;
      localStorage.setItem(tokenName, token);
      this.tokenData = AuthService.decode(token);
      return this.tokenData;
    });
    // .subscribe((token: JwtToken) => {
    //   this.authService.login(token);
    //   this.router.navigate(['/']);
    // }, error => {
    //   console.log(error);
    //   this.password = '';
    //   this.error = true;
    // });
  }

  static decode(token: string): TokenData {
    const parts = token.split('.');
    return JSON.parse(atob(parts[1]));
  }

  getUsername(): string {
    if (this.tokenData == null) {
      return null;
    }
    return this.tokenData.user_name;
  }

  isExpired() {
    if (this.tokenData == null) {
      return true;
    }
    console.log(new Date(this.tokenData.exp * 1000));
    // exp is the epoch second it expires
    return Date.now() / 1000 > this.tokenData.exp;
  }


}

@Component({
  selector: 'login-modal',
  template: `
    <div class="modal-header">
      <h4 class="modal-title">Please confirm your credentials</h4>
    </div>
    <div class="modal-body">
      <form (submit)="login()">
        <div class="form-group">
          <label for="username">Username</label>
          <input type="text" class="form-control" id="username" name="username"
                 placeholder="Enter username" [(ngModel)]="username">
        </div>
        <div class="form-group">
          <label for="password">Password</label>
          <input type="password" class="form-control" id="password" name="password" placeholder="Password"
                 [(ngModel)]="password">
        </div>
        <div *ngIf="error" class="alert alert-danger">Invalid credentials!</div>
      </form>
    </div>
    <div class="modal-footer">
      <button type="button" class="btn btn-outline-dark" (click)="login()">Login</button>
    </div>
  `
})
export class LoginModalComponent {
  @Input() auth: AuthService;
  username: string;
  password: string;
  error: boolean = false;

  constructor(public activeModal: NgbActiveModal) {
  }

  login() {
    this.error = false;
    this.auth.login(this.username, this.password)
      .subscribe(success => {
        this.password = '';
        this.activeModal.close();
      }, error => {
        console.log(error);
        this.password = '';
        this.error = true;
      })
  }
}
