import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {MainScreenComponent} from "./main-screen/main-screen.component";
import {ProjectsComponent} from "./projects/projects.component";
import {ReportsComponent} from "./reports/reports.component";
import {UserMappingsComponent} from "./user-mappings/user-mappings.component";
import {LoginComponent} from "./login/login.component";
import {RulesComponent} from "./rules/rules.component";
import {ChangePasswordComponent} from "./change-password/change-password.component";
import {MyProfileComponent} from "./my-profile/my-profile.component";
import {LogoutComponent} from "./logout/logout.component";


const routes: Routes = [
  {path: '', component: LoginComponent, pathMatch: 'full'},
  {path: LoginComponent.ROUTE, component: LoginComponent},
  {path: MainScreenComponent.ROUTE, component: MainScreenComponent, children: [
      {path: ProjectsComponent.ROUTE, component: ProjectsComponent},
      {path: RulesComponent.ROUTE, component: RulesComponent},
      {path: ReportsComponent.ROUTE, component: ReportsComponent},
      {path: UserMappingsComponent.ROUTE, component: UserMappingsComponent},
      {path: ChangePasswordComponent.ROUTE, component: ChangePasswordComponent},
      {path: MyProfileComponent.ROUTE, component: MyProfileComponent},
      {path: LogoutComponent.ROUTE, component: LogoutComponent},
    ]}
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { useHash: true })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
