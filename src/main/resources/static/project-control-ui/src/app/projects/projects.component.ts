import { Component, OnInit } from '@angular/core';
import {CollectionModelGitRepo, GitRepoEntityControllerService} from "../api";

@Component({
  selector: 'app-projects',
  templateUrl: './projects.component.html',
  styleUrls: ['./projects.component.scss']
})
export class ProjectsComponent implements OnInit {

  repos: CollectionModelGitRepo;

  constructor(private gitRepoes: GitRepoEntityControllerService) { }

  ngOnInit() {
    this.gitRepoes.getCollectionResourceGitrepoGet1().subscribe(resp => {
      this.repos = resp;
    })
  }

}
