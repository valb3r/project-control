import {AfterContentInit, Component, Input, OnInit, ViewChild} from "@angular/core";

import {MatSelectionList, MatSelectionListChange} from "@angular/material/list";
import {BehaviorSubject, zip} from "rxjs";
import {
  EntityModelGitRepo,
  EntityModelUser,
  StatisticsSearchControllerService,
  UserSearchControllerService
} from "../../api";
import {ChartsConfig} from "./charts-config";
import {Id} from "../../id";

@Component({template: ''})
export abstract class ByUserComponent implements OnInit, AfterContentInit {
  @Input() abstract project: EntityModelGitRepo;
  @ViewChild("usersSelected") usersSelected: MatSelectionList;

  isLoading = false;
  users: EntityModelUser[]
  options = ChartsConfig.defaultBarChart()
  updatedOptions = undefined
  dateRange: string[] = []
  series = []
  chart = undefined
  userSelected = {} as EntityModelUser;

  afterLoaded = new BehaviorSubject(null)

  protected constructor(protected statistics: StatisticsSearchControllerService, protected userList: UserSearchControllerService) {
  }

  ngOnInit(): void {
    let repoId = +Id.read(this.project._links.self.href);
    this.userList.findByRepoIdL(repoId)
    const loaded = zip(
      this.statistics.getTotalWorkDateRangesL(repoId),
      this.userList.findByRepoIdL(repoId)
    );

    loaded.subscribe(res => {
      this.users = res[1]._embedded.users
      this.dateRange[0] = res[0].from
      this.dateRange[1] = res[0].to
    });

    zip(
      this.afterLoaded,
      loaded
    ).subscribe(_ => {
      this.doSelect();
    })
  }

  ngAfterContentInit(): void {
    this.afterLoaded.next(null);
  }

  doSelect() {
    this.usersSelected.selectAll();
    this.usersSelected.options.forEach(it => this.userSelectionChange(new MatSelectionListChange(this.usersSelected, null, [it])))
  }

  onChartInit(ec) {
    this.chart = ec;
  }

  userSelectionChange(change: MatSelectionListChange) {
    let repoId = +Id.read(this.project._links.self.href);
    this.isLoading = true;
    const user = change.options[0].value as EntityModelUser;
    if (change.source.selectedOptions.isSelected(change.options[0])) {
      this.loadData(repoId, user);
      this.userSelected = change.options[0].value
    } else {
      this.chart.clear();
      this.isLoading = false;
      let update = ChartsConfig.defaultBarChart();
      this.series = this.series.filter(it => it.href !== user._links.self.href)
      update.series = this.series;
      this.updatedOptions = update;
    }
  }

  protected abstract loadData(repoId: number, user: EntityModelUser): void;
}