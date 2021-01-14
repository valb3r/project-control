import {Component, Input} from '@angular/core';
import {
  EntityModelGitRepo,
  EntityModelUser,
  StatisticsSearchControllerService,
  UserSearchControllerService
} from "../../../api";
import {ChartsConfig} from "../charts-config";
import {Id} from "../../../id";
import {ByUserComponent} from "../by-user.component";

@Component({
  selector: 'app-by-user-commits',
  templateUrl: './by-user-commits.component.html',
  styleUrls: ['./by-user-commits.component.scss']
})
export class ByUserCommitsComponent extends ByUserComponent {

  @Input() project: EntityModelGitRepo;

  constructor(statistics: StatisticsSearchControllerService, userList: UserSearchControllerService) {
    super(statistics, userList); }
  protected loadData(repoId: number, user: EntityModelUser) {
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
