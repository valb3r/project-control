import { Component, OnInit } from '@angular/core';
import {
  AbstractControlOptions,
  FormBuilder,
  FormControl,
  FormGroup,
  FormGroupDirective,
  NgForm,
  Validators
} from "@angular/forms";
import {ErrorStateMatcher} from "@angular/material/core";
import {FieldErrorStateMatcher} from "../app.component";
import {ActivatedRoute, Router} from "@angular/router";
import {AuthUserControllerService} from "../api";
import {MyProfileComponent} from "../my-profile/my-profile.component";

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.scss']
})
export class ChangePasswordComponent implements OnInit {

  public static ROUTE = 'change-password';

  responseError: string;

  oldPasswordControl = new FormControl('', [
    Validators.required
  ]);

  passwordControl = new FormControl('', [
    Validators.required,
    Validators.minLength(3)
  ]);

  passwordMatchControl = new PasswordsMatchControl(false);

  passwordUpdateForm = this.fb.group({
    oldpassword: this.oldPasswordControl,
    passwords: this.passwordControl,
    matchPasswords: this.passwordMatchControl
  }, {validator: ChangePasswordComponent.checkPasswords} as AbstractControlOptions);

  fieldMatcher = new FieldErrorStateMatcher();
  parentOrFieldMatcher = new ParentOrFieldErrorStateMatcher();

  constructor(private api: AuthUserControllerService, private router: Router, private route: ActivatedRoute, private fb: FormBuilder) { }

  ngOnInit() {
  }

  handleSaveClick() {
    if (!this.passwordUpdateForm.valid) {
      return
    }

    this.api.updatePassword({oldPassword: this.oldPasswordControl.value, newPassword: this.passwordControl.value})
      .subscribe(
        success => {
          this.router.navigate([`../${MyProfileComponent.ROUTE}`], {relativeTo: this.route});
        },
        error => {
          this.responseError = error.status === 401 ? "Unauthorized" : error.message;
        });
  }

  private static checkPasswords(group: FormGroup) { // here we have the 'passwords' group
    let matchControl = <PasswordsMatchControl>group.controls.matchPasswords;
    let pass = group.controls.passwords.value;
    let confirmPass = matchControl.value;

    return (matchControl.Hidden || pass === confirmPass) ? null : {notSame: true}
  }
}

export class ParentOrFieldErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    const invalidCtrl = !!(control && control.invalid && control.parent.dirty);
    const invalidParent = !!(control && control.parent && control.parent.invalid && control.parent.dirty);

    return (invalidCtrl || invalidParent);
  }
}

class PasswordsMatchControl extends FormControl {

  constructor(private hidden: boolean) {
    super('', [])
  }

  get Hidden(): boolean {
    return this.hidden;
  }

  visible(): boolean {
    return !this.hidden;
  }

  set Hidden(hidden: boolean) {
    this.hidden = hidden;
  }
}

