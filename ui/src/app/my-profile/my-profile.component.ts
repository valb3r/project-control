import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormControl, Validators} from "@angular/forms";
import {FieldErrorStateMatcher} from "../app.component";
import {AuthUserControllerService} from "../api";

@Component({
  selector: 'app-my-profile',
  templateUrl: './my-profile.component.html',
  styleUrls: ['./my-profile.component.scss']
})
export class MyProfileComponent implements OnInit {

  public static ROUTE = 'my-profile';

  id: number;
  responseError: String;
  updated: boolean;

  userNameControl = new FormControl('', [
    Validators.required,
    Validators.minLength(3)
  ]);

  passwordControl = new FormControl('', [
    Validators.required
  ]);

  registerForm = this.fb.group({
    username: this.userNameControl,
    password: this.passwordControl
  });


  fieldMatcher = new FieldErrorStateMatcher();

  constructor(private fb: FormBuilder, private api: AuthUserControllerService) {}

  ngOnInit() {
    this.api.me().subscribe(res => {
      this.id = res.id;
      this.userNameControl.setValue(res.login);
    });
  }

  onSaveClick(): void {
    if (!this.registerForm.valid) {
      return
    }

    this.updated = false;

    this.api.updateLogin({newLogin: this.userNameControl.value, password: this.passwordControl.value})
      .subscribe(
        success => {
          this.updated = true;
        },
        error => {
          this.responseError = error.status === 401 ? "Unauthorized" : error.message;
        });
  }
}
