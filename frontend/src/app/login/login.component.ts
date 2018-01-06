import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {HttpClient} from "@angular/common/http";

interface JwtToken {
  access_token: string;
  token_type: string;
  expires_in: number;
  scope: string;
  jti: string;
}

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  username: string;
  password: string;
  error: boolean = false;

  constructor(private router: Router, private http: HttpClient) {
  }

  ngOnInit() {
    setTimeout(() =>
      localStorage.removeItem('access_token'));
  }

  login() {
    this.error = false;
    this.http.post(`/oauth/token?username=${this.username}&password=${this.password}&grant_type=password`,
      null,
      {
      headers: {
        "Authorization": "Basic " + btoa("08154711:supersecret")
      }
    })
      .subscribe((token: JwtToken) => {
        localStorage.setItem('access_token', token.access_token);
        this.router.navigate(['/']);
      }, error => {
        console.log(error);
        //this.username = '';
        this.password = '';
        this.error = true;
      });
  }
}
