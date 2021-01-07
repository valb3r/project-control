import {Component, Input, OnInit} from '@angular/core';
import {EntityModelGitRepo} from "../../api";

@Component({
  selector: 'app-project-rules',
  templateUrl: './project-rules.component.html',
  styleUrls: ['./project-rules.component.scss']
})
export class ProjectRulesComponent implements OnInit {

  @Input() project: EntityModelGitRepo;

  constructor() { }

  ngOnInit(): void {
  }

}
