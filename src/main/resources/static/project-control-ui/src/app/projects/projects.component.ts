import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {GitRepo, GitRepoEntityControllerService} from "../api";
import {MatPaginator} from "@angular/material/paginator";
import {catchError, map, startWith, switchMap} from "rxjs/operators";
import {EMPTY} from "rxjs";

@Component({
  selector: 'app-projects',
  templateUrl: './projects.component.html',
  styleUrls: ['./projects.component.scss']
})
export class ProjectsComponent implements AfterViewInit {

  @ViewChild(MatPaginator) paginator: MatPaginator;
  repos: GitRepo[];

  displayedColumns: string[] = ['actions', 'name', 'status', 'branch', 'lastAnalyzedCommit', 'url', 'needsAuthentication', 'errorMessage'];
  resultsLength = 0;
  isLoadingResults = true;

  constructor(private gitRepoes: GitRepoEntityControllerService) { }

  ngAfterViewInit() {
    this.paginator.page.pipe(
        startWith({}),
        switchMap(() => {
          this.isLoadingResults = true;
          return this.gitRepoes.getCollectionResourceGitrepoGet1(this.paginator.pageIndex, this.paginator.pageSize)
        }),
        map(data => {
          this.isLoadingResults = false;
          this.resultsLength = data._embedded.gitRepoes.length;

          return data._embedded.gitRepoes;
        }),
        catchError(() => {
          this.isLoadingResults = false;
          return EMPTY;
        })
      ).subscribe(data => this.repos = data);
  }

  repo(item: GitRepo): GitRepo {
    return item;
  }
}
