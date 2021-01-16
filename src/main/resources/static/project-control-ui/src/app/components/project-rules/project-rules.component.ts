import {Component, Input, NgZone, OnInit, ViewChild} from '@angular/core';
import {
  EntityModelFileExclusionRule,
  EntityModelFileInclusionRule,
  EntityModelGitRepo,
  FileExclusionRuleEntityControllerService,
  FileInclusionRuleEntityControllerService
} from "../../api";
import {zip} from "rxjs";
import {CdkTextareaAutosize} from "@angular/cdk/text-field";
import {take} from "rxjs/operators";
import {Form, NgForm} from "@angular/forms";

@Component({
  selector: 'app-project-rules',
  templateUrl: './project-rules.component.html',
  styleUrls: ['./project-rules.component.scss']
})
export class ProjectRulesComponent implements OnInit {

  @Input() project: EntityModelGitRepo;
  @ViewChild('autosizeInclusion') autosizeInclusion: CdkTextareaAutosize;
  @ViewChild('autosizeExclusion') autosizeExclusion: CdkTextareaAutosize;
  @ViewChild('detailsForm') detailsForm: NgForm

  types = RuleType
  isLoadingResults = true;
  inclusionRules: EntityModelFileInclusionRule[];
  exclusionRules: EntityModelFileExclusionRule[];
  newInclusionRule: EntityModelFileInclusionRule;
  newExclusionRule: EntityModelFileExclusionRule;
  expandedInclusionRule: EntityModelFileInclusionRule;
  expandedExclusionRule: EntityModelFileExclusionRule;

  constructor(
    private ngZone: NgZone,
    private fileInclusionRules: FileInclusionRuleEntityControllerService,
    private fileExclusionRules: FileExclusionRuleEntityControllerService
  ) { }

  ngOnInit(): void {
    this.loadRules();
  }

  addInclusionRule() {
    this.newInclusionRule = {
      name: "",
      rule: ""
    } as EntityModelFileInclusionRule;
  }

  addExclusionRule() {
    this.newExclusionRule = {
      name: "",
      rule: ""
    } as EntityModelFileExclusionRule
  }

  updateInclusionRule(rule: EntityModelFileInclusionRule) {
  }

  updateExclusionRule(rule: EntityModelFileExclusionRule) {
  }

  removeInclusionRule(rule: EntityModelFileInclusionRule) {
  }

  removeExclusionRule(rule: EntityModelFileExclusionRule) {
  }

  toggleExpandInclusionRule(rule: EntityModelFileInclusionRule) {
  }

  toggleExpandExclusionRule(rule: EntityModelFileExclusionRule) {
  }

  saveRule(rule: EntityModelFileExclusionRule|EntityModelFileExclusionRule, type: RuleType) {
    if (this.detailsForm.invalid) {
      return
    }
    console.log("Submit");
  }

  private loadRules() {
    this.isLoadingResults = true;
    zip(
      this.fileInclusionRules.getCollectionResourceFileinclusionruleGet1(),
      this.fileExclusionRules.getCollectionResourceFileexclusionruleGet1()
    ).subscribe(res => {
      this.isLoadingResults = false;
      this.inclusionRules = res[0]._embedded.fileInclusionRules;
      this.exclusionRules = res[1]._embedded.fileExclusionRules;
    });
  }

  triggerResizeInclusion() {
    this.ngZone.onStable.pipe(take(1))
      .subscribe(() => this.autosizeInclusion.resizeToFitContent(true));
  }

  triggerResizeExclusion() {
    this.ngZone.onStable.pipe(take(1))
      .subscribe(() => this.autosizeExclusion.resizeToFitContent(true));
  }
}

export enum RuleType {
  INCLUSION = 'inclusion',
  EXCLUSION = 'exclusion'
}
