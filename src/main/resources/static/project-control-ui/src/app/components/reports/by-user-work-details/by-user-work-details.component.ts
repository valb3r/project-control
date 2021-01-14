import {Component, Input, OnInit} from '@angular/core';
import {
  EntityModelGitRepo,
  EntityModelUser,
  StatisticsSearchControllerService,
  UserSearchControllerService
} from "../../../api";
import {Id} from "../../../id";
import {ChartsConfig} from "../charts-config";
import {ByUserComponent} from "../by-user.component";
import {zip} from "rxjs";
import {MatSelectionListChange} from "@angular/material/list";

@Component({
  selector: 'app-by-user-work-details',
  templateUrl: './by-user-work-details.component.html',
  styleUrls: ['./by-user-work-details.component.scss']
})
export class ByUserWorkDetailsComponent extends ByUserComponent {

  @Input() project: EntityModelGitRepo;

  constructor(statistics: StatisticsSearchControllerService, userList: UserSearchControllerService) {
    super(statistics, userList);
  }

  doSelect() {
    this.usersSelected.selectedOptions.select(this.usersSelected.options.first)
    this.userSelectionChange(new MatSelectionListChange(this.usersSelected, null, [this.usersSelected.options.first]));
  }

  protected loadData(repoId: number, user: EntityModelUser) {
    zip(
      this.statistics.getWeeklyWorkStatsLLII(repoId, +Id.read(user._links.self.href), this.dateRange[0], this.dateRange[1]),
      this.statistics.getWeeklyOwnershipStatsLLII(repoId, +Id.read(user._links.self.href), this.dateRange[0], this.dateRange[1]),
      this.statistics.getRemovedLinesStatsLLII(repoId, +Id.read(user._links.self.href), this.dateRange[0], this.dateRange[1])
    ).subscribe(res => {
      this.isLoading = false;
      this.series = [];
      const xData = [];
      this.series.push({
        href: user._links.self.href,
        type: 'bar',
        stack: 'churn',
        yAxisIndex: 0,
        data: res[0].map(it => it.linesAdded),
        name: 'Lines added'
      });
      this.series.push({
        href: user._links.self.href,
        type: 'bar',
        stack: 'churn',
        yAxisIndex: 0,
        data: res[0].map(it => it.linesRemoved),
        name: 'Lines removed'
      });
      this.series.push({
        href: user._links.self.href,
        type: 'bar',
        stack: 'commits',
        yAxisIndex: 1,
        data: res[0].map(it => it.totalCommits),
        name: 'Commit count'
      });
      this.series.push({
        href: user._links.self.href,
        type: 'bar',
        stack: 'lines-owned',
        yAxisIndex: 2,
        data: res[1].map(it => it.linesOwned),
        name: 'Lines owned'
      });
      this.series.push({
        href: user._links.self.href,
        type: 'bar',
        stack: 'lines-removed',
        yAxisIndex: 3,
        data: res[2].map(it => it.removedOwnLines),
        name: 'Own lines removed'
      });
      this.series.push({
        href: user._links.self.href,
        type: 'bar',
        stack: 'lines-removed',
        yAxisIndex: 3,
        data: res[2].map(it => it.removedLinesOfOthers),
        name: 'Lines removed of others'
      });
      this.series.push({
        href: user._links.self.href,
        type: 'bar',
        stack: 'lines-removed',
        yAxisIndex: 3,
        data: res[2].map(it => it.removedByOthersLines),
        name: 'Lines removed by others'
      });

      res.forEach(it => it.forEach(data => {
        if (!xData.includes(data.from)) {
          xData.push(data.from);
        }
      }));

      let update = ChartsConfig.defaultBarChart();
      update.series = this.series;
      update.xAxis = ByUserWorkDetailsComponent.xAxis();
      update.xAxis.data = xData;
      update.yAxis = [
        ByUserWorkDetailsComponent.clone(update.yAxis),
        ByUserWorkDetailsComponent.clone(update.yAxis),
        ByUserWorkDetailsComponent.clone(update.yAxis),
        ByUserWorkDetailsComponent.clone(update.yAxis)
      ];
      this.updatedOptions = update;
    });
  }

  private static clone<T>(objectToCopy: T): T {
    return JSON.parse(JSON.stringify(objectToCopy))
  }

  private static xAxis() {
    return {
      type: 'category',
      data: [],
      axisTick: {
        alignWithLabel: true,
      }
    }
  }
}
