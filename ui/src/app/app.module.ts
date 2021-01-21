import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MainScreenComponent} from './main-screen/main-screen.component';
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatIconModule} from "@angular/material/icon";
import {MatSidenavModule} from "@angular/material/sidenav";
import {MatListModule} from "@angular/material/list";
import {MatButtonModule} from "@angular/material/button";
import {ProjectsComponent} from './projects/projects.component';
import {ReportsComponent} from './reports/reports.component';
import {UserMappingsComponent} from './user-mappings/user-mappings.component';
import {HttpClientModule} from "@angular/common/http";
import {ApiModule, Configuration, ConfigurationParameters} from "./api";
import {LoginComponent} from './login/login.component';
import {MatCardModule} from "@angular/material/card";
import {MatFormFieldModule} from "@angular/material/form-field";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatInputModule} from "@angular/material/input";
import {FlexLayoutModule} from "@angular/flex-layout";
import {MatPaginatorIntl, MatPaginatorModule} from '@angular/material/paginator';
import {MatPaginatorIntlWithoutRange} from './components/paginator/paginator.component';
import {MatTableModule} from "@angular/material/table";
import {AddProjectDialogComponent} from './dialogs/add-project-dialog/add-project-dialog.component';
import {MatDialogModule} from "@angular/material/dialog";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatProgressBarModule} from "@angular/material/progress-bar";
import {UserToAliasComponent} from './components/alias-list/user-to-alias.component';
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import {MatOptionModule} from "@angular/material/core";
import {MatAutocompleteModule} from "@angular/material/autocomplete";
import {RulesComponent} from './rules/rules.component';
import {ProjectRulesComponent} from './components/project-rules/project-rules.component';
import {ProjectActivityComponent} from './components/project-activity/project-activity.component';
import {NgxEchartsModule} from "ngx-echarts";
import {TotalCommitsComponent} from './components/reports/total-commits/total-commits.component';
import {TotalChurnComponent} from './components/reports/total-churn/total-churn.component';
import {TotalLinesCountComponent} from './components/reports/total-lines-count/total-lines-count.component';
import {ByUserCommitsComponent} from './components/reports/by-user-commits/by-user-commits.component';
import {ByUserChurnComponent} from './components/reports/by-user-churn/by-user-churn.component';
import {TotalReworkComponent} from './components/reports/total-rework/total-rework.component';
import {ByUserOwnershipComponent} from './components/reports/by-user-ownership/by-user-ownership.component';
import {MatExpansionModule} from "@angular/material/expansion";
import {ByUserReworkByOthersComponent} from './components/reports/by-user-rework-by-others/by-user-rework-by-others.component';
import {ByUserReworkFromOthersComponent} from './components/reports/by-user-rework-from-others/by-user-rework-from-others.component';
import {ByUserReworkOwnComponent} from './components/reports/by-user-rework-own/by-user-rework-own.component';
import {ByUserWorkDetailsComponent} from './components/reports/by-user-work-details/by-user-work-details.component';
import {environment} from "../environments/environment";

export function apiConfigFactory(): Configuration {
  const params: ConfigurationParameters = {
    basePath: environment.apiUrl,
    withCredentials: true
  };

  return new Configuration(params);
}

@NgModule({
  declarations: [
    AppComponent,
    MainScreenComponent,
    ProjectsComponent,
    ReportsComponent,
    UserMappingsComponent,
    LoginComponent,
    AddProjectDialogComponent,
    UserToAliasComponent,
    RulesComponent,
    ProjectRulesComponent,
    ProjectActivityComponent,
    TotalCommitsComponent,
    TotalChurnComponent,
    TotalLinesCountComponent,
    ByUserCommitsComponent,
    ByUserChurnComponent,
    TotalReworkComponent,
    ByUserOwnershipComponent,
    ByUserReworkByOthersComponent,
    ByUserReworkFromOthersComponent,
    ByUserReworkOwnComponent,
    ByUserWorkDetailsComponent
  ],
    imports: [
        ApiModule.forRoot(apiConfigFactory),
        NgxEchartsModule.forRoot({echarts: () => import('echarts')}),
        BrowserModule,
        HttpClientModule,
        AppRoutingModule,
        BrowserAnimationsModule,
        MatToolbarModule,
        MatIconModule,
        MatSidenavModule,
        MatListModule,
        MatButtonModule,
        MatCardModule,
        MatTableModule,
        MatCheckboxModule,
        MatDialogModule,
        MatProgressBarModule,
        MatPaginatorModule,
        MatFormFieldModule,
        MatAutocompleteModule,
        MatExpansionModule,
        ReactiveFormsModule,
        FlexLayoutModule,
        MatInputModule,
        FormsModule,
        MatProgressSpinnerModule,
        MatOptionModule
    ],
  providers:[{provide: MatPaginatorIntl, useClass: MatPaginatorIntlWithoutRange}],
  bootstrap: [AppComponent]
})
export class AppModule { }
