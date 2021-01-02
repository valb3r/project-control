import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {MainScreenComponent} from "./main-screen/main-screen.component";
import {ProjectsComponent} from "./projects/projects.component";
import {ReportsComponent} from "./reports/reports.component";
import {UserMappingsComponent} from "./user-mappings/user-mappings.component";
import {LoginComponent} from "./login/login.component";


const routes: Routes = [
  {path: '', component: LoginComponent, pathMatch: 'full'},
  {path: 'login', component: LoginComponent},
  {path: 'control', component: MainScreenComponent, children: [
      {path: 'projects', component: ProjectsComponent},
      {path: 'reports', component: ReportsComponent},
      {path: 'user-mappings', component: UserMappingsComponent},
    ]}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
