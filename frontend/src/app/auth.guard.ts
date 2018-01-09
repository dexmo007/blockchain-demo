import {Injectable} from '@angular/core';
import {CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import {AuthService} from "./auth.service";
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";

@Injectable()
export class AuthGuard implements CanActivate {

  constructor(private router: Router, private auth: AuthService) { }

  /**
   * Checks if the user is authenticated, if not redirects to login page
   * @param {ActivatedRouteSnapshot} next
   * @param {RouterStateSnapshot} state
   * @return {Observable<boolean> | Promise<boolean> | boolean}
   */
  canActivate(next: ActivatedRouteSnapshot,
              state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
    if (this.auth.canActivate()) {
      return true;
    }
    this.router.navigate(['/login']);
    return false;
  }
}
