import {Component, Input, OnInit} from '@angular/core';
import {EntityModelGitRepo} from "../../api";

@Component({
  selector: 'app-project-activity',
  templateUrl: './project-activity.component.html',
  styleUrls: ['./project-activity.component.scss']
})
export class ProjectActivityComponent implements OnInit {

  @Input() project: EntityModelGitRepo;

  constructor() { }

  ngOnInit(): void {
  }
}
