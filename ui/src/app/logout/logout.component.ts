import {Component, OnInit} from '@angular/core';
import {LoginControllerService} from "../api";
import {Router} from "@angular/router";

@Component({
  selector: 'app-logout',
  templateUrl: './logout.component.html',
  styleUrls: ['./logout.component.scss']
})
export class LogoutComponent implements OnInit {

  public static ROUTE = 'logout';

  responseError: string;

  constructor(private auth: LoginControllerService, private router: Router) { }

  ngOnInit(): void {
    this.auth.logout().subscribe(
      success => {
        this.router.navigate(['']);
      },
      error => {
        this.responseError = error.status === 401 ? "Unauthorized" : error.message;
      });
  }
}
