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
import {Observable, zip} from "rxjs";
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
    this.usersSelected.selectedOptions.select(this.usersSelected.options.first);
    this.userSelectionChange(new MatSelectionListChange(this.usersSelected, null, [this.usersSelected.options.first]));
  }

  protected loadData(repoId: number, user: EntityModelUser): Observable<any>  {
    const resp = zip(
      this.statistics.getWeeklyWorkStatsLLII(repoId, +Id.read(user._links.self.href), this.dateRange[0], this.dateRange[1]),
      this.statistics.getWeeklyOwnershipStatsLLII(repoId, +Id.read(user._links.self.href), this.dateRange[0], this.dateRange[1]),
      this.statistics.getRemovedLinesStatsLLII(repoId, +Id.read(user._links.self.href), this.dateRange[0], this.dateRange[1])
    );
    resp.subscribe(res => {
      this.series = [];
      const xData = [];
      res.forEach(it => it.forEach(data => {
        if (!xData.includes(data.from)) {
          xData.push(data.from);
        }
      }));
      xData.sort();
      const workMap = new Map(res[0].map(it => [it.from, it]));
      const ownershipMap = new Map(res[1].map(it => [it.from, it]));
      const removedMap = new Map(res[2].map(it => [it.from, it]));

      this.series.push({
        href: user._links.self.href,
        type: 'bar',
        stack: 'churn',
        yAxisIndex: 0,
        data: xData.map(it => workMap.get(it)?.linesRemoved || 0),
        name: 'Lines removed',
        itemStyle: { normal: { color: '#dd0000' } }
      });
      this.series.push({
        href: user._links.self.href,
        type: 'bar',
        stack: 'churn',
        yAxisIndex: 0,
        data: xData.map(it => workMap.get(it)?.linesAdded || 0),
        name: 'Lines added',
        itemStyle: { normal: { color: '#00cc00' } }
      });
      this.series.push({
        href: user._links.self.href,
        type: 'bar',
        stack: 'lines-removed',
        yAxisIndex: 3,
        data: xData.map(it => removedMap.get(it)?.removedOwnLines || 0),
        name: 'Own lines removed',
        itemStyle: { normal: { color: '#eeee00' } }
      });
      this.series.push({
        href: user._links.self.href,
        type: 'bar',
        stack: 'lines-removed',
        yAxisIndex: 3,
        data: xData.map(it => removedMap.get(it)?.removedLinesOfOthers || 0),
        name: 'Lines removed of others',
        itemStyle: { normal: { color: '#aa0000' } }
      });
      this.series.push({
        href: user._links.self.href,
        type: 'bar',
        stack: 'lines-removed',
        yAxisIndex: 3,
        data: xData.map(it => removedMap.get(it)?.removedByOthersLines || 0),
        name: 'Lines removed by others',
        itemStyle: { normal: { color: '#ff0000' } }
      });
      this.series.push({
        href: user._links.self.href,
        type: 'bar',
        stack: 'commits',
        yAxisIndex: 1,
        data: xData.map(it => workMap.get(it)?.totalCommits || 0),
        name: 'Commit count',
        itemStyle: { normal: { color: '#00aaee' } }
      });
      this.series.push({
        href: user._links.self.href,
        type: 'bar',
        stack: 'lines-owned',
        yAxisIndex: 2,
        data: xData.map(it => ownershipMap.get(it)?.linesOwned || 0),
        name: 'Lines owned',
        itemStyle: { normal: { color: '#0000ee' } }
      });

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
    return resp;
  }

  private static clone<T>(objectToCopy: T): T {
    return JSON.parse(JSON.stringify(objectToCopy));
  }

  private static xAxis() {
    return {
      type: 'category',
      data: [],
      axisTick: {
        alignWithLabel: true,
      }
    };
  }
}
