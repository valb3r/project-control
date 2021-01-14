import {AfterContentInit, AfterViewInit, Component, Input, OnInit, ViewChild} from '@angular/core';
import {
  EntityModelGitRepo,
  EntityModelUser,
  StatisticsSearchControllerService,
  UserSearchControllerService
} from "../../../api";
import {ChartsConfig} from "../charts-config";
import {Id} from "../../../id";
import {MatSelectionList, MatSelectionListChange} from "@angular/material/list";
import {BehaviorSubject, zip} from "rxjs";

@Component({
  selector: 'app-by-user-commits',
  templateUrl: './by-user-commits.component.html',
  styleUrls: ['./by-user-commits.component.scss']
})
export class ByUserCommitsComponent implements OnInit {

  @Input() project: EntityModelGitRepo;
  @ViewChild("usersSelected") usersSelected: MatSelectionList;

  isLoading = false;
  users: EntityModelUser[]
  options = ChartsConfig.defaultBarChart()
  updatedOptions = undefined
  dateRange: string[] = []
  series = []
  chart = undefined

  afterLoaded = new BehaviorSubject(null)

  constructor(private statistics: StatisticsSearchControllerService, private userList: UserSearchControllerService) { }

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
      this.usersSelected.selectAll();
      this.usersSelected.options.forEach(it => this.userSelectionChange(new MatSelectionListChange(this.usersSelected, null, [it])))
    })
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
    } else {
      this.chart.clear();
      this.isLoading = false;
      let update = ChartsConfig.defaultBarChart();
      this.series = this.series.filter(it => it.href !== user._links.self.href)
      update.series = this.series;
      this.updatedOptions = update;
    }
  }

  private loadData(repoId: number, user: EntityModelUser) {
    this.statistics.getWeeklyWorkStatsLLII(repoId, +Id.read(user._links.self.href), this.dateRange[0], this.dateRange[1])
      .subscribe(
        res => {
          this.isLoading = false;
          this.series.push({
            href: user._links.self.href,
            type: 'bar',
            stack: true,
            data: res.map(it => [Date.parse(it.from), it.totalCommits]),
            name: user.name
          });
          let update = ChartsConfig.defaultBarChart();
          update.series = this.series;
          this.updatedOptions = update;
        }
      )
  }
}
