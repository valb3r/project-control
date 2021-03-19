import {Component, Input} from '@angular/core';
import {
  EntityModelGitRepo,
  EntityModelUser,
  StatisticsSearchControllerService,
  UserSearchControllerService
} from "../../../api";
import {Id} from "../../../id";
import {ChartsConfig} from "../charts-config";
import {ByUserComponent} from "../by-user.component";
import {Observable} from "rxjs";

@Component({
  selector: 'app-by-user-rework-from-others',
  templateUrl: './by-user-rework-from-others.component.html',
  styleUrls: ['./by-user-rework-from-others.component.scss']
})
export class ByUserReworkFromOthersComponent extends ByUserComponent {

  @Input() project: EntityModelGitRepo;

  constructor(statistics: StatisticsSearchControllerService, userList: UserSearchControllerService) {
    super(statistics, userList);
  }

  protected loadData(repoId: number, user: EntityModelUser): Observable<any>  {
    const resp = this.statistics.getRemovedLinesStatsLLII(repoId, +Id.read(user._links.self.href), this.dateRange[0], this.dateRange[1]);
    resp.subscribe(
        res => {
          this.series.push({
            href: user._links.self.href,
            type: 'bar',
            stack: true,
            data: res.map(it => [Date.parse(it.from), it.removedLinesOfOthers]),
            name: user.name,
            itemStyle: { normal: { color: ChartsConfig.seriesColor(user.name) } }
          });
          let update = ChartsConfig.defaultBarChart();
          update.series = this.series;
          this.updatedOptions = update;
        }
      );
    return resp;
  }
}
