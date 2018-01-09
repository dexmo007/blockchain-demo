import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {HttpClient} from "@angular/common/http";
import {AuthService, JwtToken} from "../auth.service";


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  username: string;
  password: string;
  error: boolean = false;

  constructor(private router: Router, private http: HttpClient, private authService: AuthService) {
  }

  ngOnInit() {
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/']);
    }
  }

  login() {
    this.error = false;
    this.authService.login(this.username, this.password)
      .subscribe(tokenData => {
          this.router.navigate(['/']);
        },
        error => {
          this.password = '';
          this.error = true;
        }
      );
  }
}
