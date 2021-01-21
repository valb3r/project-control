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
  selector: 'app-by-user-churn',
  templateUrl: './by-user-churn.component.html',
  styleUrls: ['./by-user-churn.component.scss']
})
export class ByUserChurnComponent extends ByUserComponent {

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
            data: res.map(it => [Date.parse(it.from), it.linesAdded + it.linesRemoved]),
            name: user.name,
            itemStyle: { normal: { color: ChartsConfig.seriesColor(user.name) } }
          });
          let update = ChartsConfig.defaultBarChart();
          update.series = this.series;
          this.updatedOptions = update;
        }
      )
  }
}
