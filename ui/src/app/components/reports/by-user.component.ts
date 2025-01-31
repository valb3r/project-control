import {AfterContentInit, Component, Input, OnInit, ViewChild} from "@angular/core";

import {MatSelectionList, MatSelectionListChange} from "@angular/material/list";
import {BehaviorSubject, Observable, of, zip} from "rxjs";
import {
  EntityModelGitRepo,
  EntityModelUser,
  StatisticsSearchControllerService,
  UserSearchControllerService
} from "../../api";
import {ChartsConfig} from "./charts-config";
import {Id} from "../../id";
import {delay} from "rxjs/operators";

@Component({template: ''})
export abstract class ByUserComponent implements OnInit, AfterContentInit {
  @Input() abstract project: EntityModelGitRepo;
  @ViewChild("usersSelected") usersSelected: MatSelectionList;

  numLoading = 0;
  users: EntityModelUser[];
  options = ChartsConfig.defaultBarChart();
  updatedOptions = undefined;
  dateRange: string[] = [];
  series = [];
  chart = undefined;
  userSelected = {} as EntityModelUser;

  afterLoaded = new BehaviorSubject(null);

  protected constructor(protected statistics: StatisticsSearchControllerService, protected userList: UserSearchControllerService) {
  }

  ngOnInit(): void {
    const repoId = +Id.read(this.project._links.self.href);
    const loaded = zip(
      this.statistics.getTotalWorkDateRangesL(repoId),
      this.userList.findByRepoIdL(repoId)
    );

    loaded.subscribe(res => {
      this.users = res[1]._embedded.users;
      this.dateRange[0] = res[0].from;
      this.dateRange[1] = res[0].to;
    });

    const delayedLoaded = loaded.pipe(delay(100)); // FIXME - need to catch after `usersSelected` options ready
    zip(
      this.afterLoaded,
      delayedLoaded
    ).subscribe(_ => {
      this.doSelect();
    });
  }

  ngAfterContentInit(): void {
    this.afterLoaded.next(null);
  }

  doSelect() {
    this.usersSelected.selectAll();
    this.usersSelected.options.forEach(it => this.userSelectionChange(new MatSelectionListChange(this.usersSelected, null, [it])));
  }

  onChartInit(ec) {
    this.chart = ec;
    this.numLoading = 0;
  }

  userSelectionChange(change: MatSelectionListChange): Observable<any> {
    if (!change.options || 0 === change.options.length || !change.options[0]) {
      return;
    }

    const repoId = +Id.read(this.project._links.self.href);
    const user = change.options[0].value as EntityModelUser;
    if (change.source.selectedOptions.isSelected(change.options[0])) {
      this.numLoading++;
      this.userSelected = change.options[0].value;
      const resp = this.loadData(repoId, user);
      resp.subscribe(_ => this.numLoading--);
      return resp;
    } else {
      this.numLoading++;
      this.chart.clear();
      const update = ChartsConfig.defaultBarChart();
      this.series = this.series.filter(it => it.href !== user._links.self.href);
      update.series = this.series;
      this.updatedOptions = update;
      this.numLoading--;
    }

    return of(null);
  }

  protected abstract loadData(repoId: number, user: EntityModelUser): Observable<any>;
}
