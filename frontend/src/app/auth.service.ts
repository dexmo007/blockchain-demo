import {Injectable} from '@angular/core';
import {JwtHelperService} from '@auth0/angular-jwt';

@Injectable()
export class AuthService {

  constructor(private jwt: JwtHelperService) {
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem('access_token');
  }


}
