import {AfterViewInit, Component, ViewChild} from '@angular/core';
import {MatPaginator} from "@angular/material/paginator";
import {catchError, map, startWith, switchMap} from "rxjs/operators";
import {EMPTY} from "rxjs";
import {MatDialog} from "@angular/material/dialog";
import {AddProjectDialogComponent} from "../dialogs/add-project-dialog/add-project-dialog.component";
import {EntityModelGitRepo, GitRepo, GitRepoEntityControllerService, GitRepoSearchControllerService} from "../api";
import {Id} from "../id";
import AnalysisStateEnum = EntityModelGitRepo.AnalysisStateEnum;

@Component({
  selector: 'app-projects',
  templateUrl: './projects.component.html',
  styleUrls: ['./projects.component.scss']
})
export class ProjectsComponent implements AfterViewInit {

  public static ROUTE = 'projects';

  @ViewChild(MatPaginator) paginator: MatPaginator;
  repos: EntityModelGitRepo[];

  displayedColumns: string[] = ['actions', 'name', 'status', 'branch', 'lastAnalyzedCommit', 'url', 'needsAuthentication', 'errorMessage'];
  resultsLength = 0;
  isLoadingResults = true;

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

  runAnalysis(repo: EntityModelGitRepo) {
    this.gitRepoes.patchItemResourceGitrepoPatch(Id.read(repo._links.self.href), {analysisState: AnalysisStateEnum.Started} as GitRepo)
      .subscribe(_ => {this.paginator.page.emit()}, error => {console.log("Error", error)});
  }

  newRepo() {
    this.dialog.open(AddProjectDialogComponent, {width: "70%"}).afterClosed().subscribe(res => {
      if (res) {
        this.paginator.page.emit();
      }
    });
  }

  deleteRepo(repo: EntityModelGitRepo) {
    this.gitRepoes.deleteItemResourceGitrepoDelete(Id.read(repo._links.self.href)).subscribe(_ => {this.paginator.page.emit()});
  }

  cleanAnalyzedDataOfRepo(repo: EntityModelGitRepo) {
    this.gitRepoes.patchItemResourceGitrepoPatch(Id.read(repo._links.self.href), {analysisState: AnalysisStateEnum.Cleanup} as GitRepo)
      .subscribe(_ => {this.paginator.page.emit()}, error => {console.log("Error", error)});
  }
}
