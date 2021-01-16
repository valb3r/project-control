import {AfterViewInit, Component, Input, NgZone, OnInit, ViewChild} from '@angular/core';
import {
  EntityModelFileExclusionRule,
  EntityModelFileInclusionRule,
  EntityModelGitRepo, FileExclusionRule,
  FileExclusionRuleEntityControllerService, FileExclusionRuleSearchControllerService, FileInclusionRule,
  FileInclusionRuleEntityControllerService, FileInclusionRuleSearchControllerService, GitRepo
} from "../../api";
import {zip} from "rxjs";
import {CdkTextareaAutosize} from "@angular/cdk/text-field";
import {take} from "rxjs/operators";
import {Form, NgForm} from "@angular/forms";
import {Id} from "../../id";

@Component({
  selector: 'app-project-rules',
  templateUrl: './project-rules.component.html',
  styleUrls: ['./project-rules.component.scss']
})
export class ProjectRulesComponent implements AfterViewInit {

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
    private fileExclusionRules: FileExclusionRuleEntityControllerService,
    private fileInclusionRulesSearch: FileInclusionRuleSearchControllerService,
    private fileExclusionRulesSearch: FileExclusionRuleSearchControllerService
  ) { }

  ngAfterViewInit(): void {
    this.loadRules();
  }

  addInclusionRule() {
    if (this.newInclusionRule) {
      this.newInclusionRule = undefined;
      return;
    }

    this.newInclusionRule = {
      name: 'Includes',
      rule: inclusionTemplate.replace(namePlaceholder, '$1Includes$3'),
      repo: this.project._links.self.href as any
    } as EntityModelFileInclusionRule;
  }

  addExclusionRule() {
    if (this.newExclusionRule) {
      this.newExclusionRule = undefined;
      return;
    }

    this.newExclusionRule = {
      name: 'Excludes',
      rule: exclusionTemplate.replace(namePlaceholder, '$1Excludes$3'),
      repo: this.project._links.self.href as any
    } as EntityModelFileExclusionRule
  }

  ruleNameChange(rule: EntityModelFileInclusionRule|EntityModelFileExclusionRule, type: RuleType) {
    if (type != RuleType.NEW_EXCLUSION && type != RuleType.NEW_INCLUSION) {
      return;
    }

    rule.rule = rule.rule.replace(namePlaceholder, `$1${rule.name}$3`);
  }

  removeInclusionRule(rule: EntityModelFileInclusionRule) {
    this.fileInclusionRules.deleteItemResourceFileinclusionruleDelete(Id.read(rule._links.self.href))
      .subscribe(_ => this.loadRules());
  }

  removeExclusionRule(rule: EntityModelFileExclusionRule) {
    this.fileExclusionRules.deleteItemResourceFileexclusionruleDelete(Id.read(rule._links.self.href))
      .subscribe(_ => this.loadRules());
  }

  toggleExpandInclusionRule(rule: EntityModelFileInclusionRule) {
    if (this.expandedInclusionRule === rule) {
      this.expandedInclusionRule = undefined;
      return;
    }

    this.expandedInclusionRule = rule;
  }

  toggleExpandExclusionRule(rule: EntityModelFileExclusionRule) {
    if (this.expandedExclusionRule === rule) {
      this.expandedExclusionRule = undefined;
      return;
    }

    this.expandedExclusionRule = rule;
  }

  saveRule(rule: EntityModelFileInclusionRule|EntityModelFileExclusionRule, type: RuleType) {
    if (this.detailsForm.invalid) {
      return
    }

    switch (type) {
      case RuleType.EXCLUSION:
        this.fileExclusionRules.patchItemResourceFileexclusionrulePatch(Id.read(rule._links.self.href), rule as FileExclusionRule)
          .subscribe(_ => this.loadRules());
        break
      case RuleType.INCLUSION:
        this.fileInclusionRules.patchItemResourceFileinclusionrulePatch(Id.read(rule._links.self.href), rule as FileExclusionRule)
          .subscribe(_ => this.loadRules());
        break
      case RuleType.NEW_EXCLUSION:
        this.fileExclusionRules.postCollectionResourceFileexclusionrulePost(rule as FileExclusionRule)
          .subscribe(_ => this.loadRules());
        break
      case RuleType.NEW_INCLUSION:
        this.fileInclusionRules.postCollectionResourceFileinclusionrulePost(rule as FileInclusionRule)
          .subscribe(_ => this.loadRules());
        break
      default: throw Error("Unimplemented: " + type);
    }
  }

  private loadRules() {
    this.isLoadingResults = true;
    zip(
      this.fileInclusionRulesSearch.findByRepoIdL2(+Id.read(this.project._links.self.href)),
      this.fileExclusionRulesSearch.findByRepoIdL1(+Id.read(this.project._links.self.href))
    ).subscribe(res => {
      this.isLoadingResults = false;
      this.inclusionRules = res[0]._embedded.fileInclusionRules.map(it => {it.repo = this.project._links.self.href as any; return it});
      this.exclusionRules = res[1]._embedded.fileExclusionRules.map(it => {it.repo = this.project._links.self.href as any; return it});
      this.expandedInclusionRule = undefined;
      this.expandedExclusionRule = undefined;
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
  EXCLUSION = 'exclusion',
  NEW_INCLUSION = 'new inclusion',
  NEW_EXCLUSION = 'new exclusion'
}

const namePlaceholder = /(\s*rule\s+["'])(.+)(["'].*)/
const inclusionTemplate = 'package com.project_control.rules\n' +
  '\n' +
  'import  com.valb3r.projectcontrol.domain.rules.RuleContext\n' +
  '\n' +
  'dialect  "mvel"\n' +
  '\n' +
  '\n' +
  'rule "Inclusion rule"\n' +
  '    no-loop\n' +
  '    when\n' +
  '       $c: RuleContext(path matches \'.+\\\\.java\')\n' +
  '    then\n' +
  '       $c.include = true;\n' +
  '       update($c)\n' +
  'end'

const exclusionTemplate = 'package com.project_control.rules\n' +
  '\n' +
  'import  com.valb3r.projectcontrol.domain.rules.RuleContext\n' +
  '\n' +
  'dialect  "mvel"\n' +
  '\n' +
  '\n' +
  'rule "Exclusion rule"\n' +
  '    no-loop\n' +
  '    when\n' +
  '       $c: RuleContext(path matches \'.+/resources/.+\')\n' +
  '    then\n' +
  '       $c.exclude = true;\n' +
  '       update($c)\n' +
  'end';
