import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {MatPaginator} from "@angular/material/paginator";
import {Alias, GitRepo, GitRepoEntityControllerService, User} from "../api";
import {catchError, map, startWith, switchMap} from "rxjs/operators";
import {EMPTY} from "rxjs";
import {animate, state, style, transition, trigger} from "@angular/animations";

@Component({
  selector: 'app-user-mappings',
  templateUrl: './user-mappings.component.html',
  styleUrls: ['./user-mappings.component.scss'],
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({height: '0px', minHeight: '0'})),
      state('expanded', style({height: '*'})),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ])
  ]
})
export class UserMappingsComponent implements AfterViewInit {

  @ViewChild(MatPaginator) paginator: MatPaginator;
  repos: RepoWithAliases[];

  displayedColumns: string[] = ['actions', 'name', 'branch', 'url'];
  resultsLength = 0;
  isLoadingResults = true;
  expandedElement: RepoWithAliases | null;

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

        return data._embedded.gitRepoes.map(it => {return {repo: it} as RepoWithAliases})
      }),
      catchError(() => {
        this.isLoadingResults = false;
        return EMPTY;
      })
    ).subscribe(data => this.repos = data);
  }

  repo(item: RepoWithAliases): RepoWithAliases {
    return item;
  }

  expandOrCollapseRow(item: RepoWithAliases) {
    if (this.expandedElement) {
      this.expandedElement = null;
      return;
    }
    this.expandedElement = item;
  }
}

class RepoWithAliases {
  repo: GitRepo;
  expanded = false;
  aliases: Alias[];
  users: User[];
  unmappedAliases: Alias[];
}
