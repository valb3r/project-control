import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {EntityModelGitRepo, GitRepoEntityControllerService} from "../api";
import {MatDialog} from "@angular/material/dialog";
import {catchError, map, startWith, switchMap} from "rxjs/operators";
import {EMPTY} from "rxjs";
import {MatPaginator} from "@angular/material/paginator";

@Component({
  selector: 'app-rules',
  templateUrl: './rules.component.html',
  styleUrls: ['./rules.component.scss']
})
export class RulesComponent implements AfterViewInit {

  public static ROUTE = 'rules';

  @ViewChild(MatPaginator) paginator: MatPaginator;
  repos: EntityModelGitRepo[];

  displayedColumns: string[] = ['actions', 'name', 'url'];
  resultsLength = 0;
  isLoadingResults = true;
  expandedElement: EntityModelGitRepo | null;

  constructor(private gitRepoes: GitRepoEntityControllerService, public dialog: MatDialog) { }

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

  repo(item: EntityModelGitRepo): EntityModelGitRepo {
    return item;
  }

  expandOrCollapseRow(item: EntityModelGitRepo) {
    if (this.expandedElement === item) {
      this.expandedElement = null;
      return;
    }

    this.expandedElement = item;
  }
}
