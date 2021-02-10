import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {FormBuilder, FormControl, Validators} from '@angular/forms';
import {FieldErrorStateMatcher} from "../app.component";
import {LoginControllerService, LoginDto} from "../api";

@Component({
  selector: 'login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  public static ROUTE = 'login';

  responseError: string;
  hide = true;

  userNameControl = new FormControl('', [
    Validators.required
  ]);

  passwordControl = new FormControl('', [
    Validators.required
  ]);

  loginForm = this.fb.group({
    username: this.userNameControl,
    passwords: this.passwordControl,
  });

  fieldMatcher = new FieldErrorStateMatcher();

  constructor(private auth: LoginControllerService, private router: Router, private fb: FormBuilder) { }

  ngOnInit() {
  }

  handleLoginClick() {
    if (!this.loginForm.valid) {
      return
    }

    this.auth.login({username: this.userNameControl.value, password: this.passwordControl.value} as LoginDto)
      .subscribe(
        success => {
          this.router.navigate(['/control/projects']);
        },
        error => {
          this.responseError = error.status === 401 ? "Unauthorized" : error.message;
        });
  }
}
