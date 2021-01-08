import {Component, Input, OnInit} from '@angular/core';
import {
  EntityModelGitRepo,
  FileExclusionRuleEntityControllerService,
  FileInclusionRuleEntityControllerService
} from "../../api";
import {zip} from "rxjs";

@Component({
  selector: 'app-project-rules',
  templateUrl: './project-rules.component.html',
  styleUrls: ['./project-rules.component.scss']
})
export class ProjectRulesComponent implements OnInit {

  @Input() project: EntityModelGitRepo;

  isLoadingResults = true;
  rules: Rule[];

  constructor(
    private fileInclusionRules: FileInclusionRuleEntityControllerService,
    private fileExclusionRules: FileExclusionRuleEntityControllerService
  ) { }

  ngOnInit(): void {
    this.loadRules();
  }

  private loadRules() {
    zip(
    ).subscribe();
  }
}

class Rule {
  name: string;
  url: string;
  type: RuleType;
}

enum RuleType {
  FILE_INCLUDE = "FILE_INCLUDE",
  FILE_EXCLUDE = "FILE_EXCLUDE"
}
