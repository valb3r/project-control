import {AfterContentInit, Component, Input} from '@angular/core';
import {EntityModelGitRepo, StatisticsSearchControllerService} from "../../api";
import {Id} from "../../id";
import {flatMap} from "rxjs/internal/operators";

@Component({
  selector: 'app-project-activity',
  templateUrl: './project-activity.component.html',
  styleUrls: ['./project-activity.component.scss']
})
export class ProjectActivityComponent implements AfterContentInit {

  @Input() project: EntityModelGitRepo;

  isLoading = false;

  options = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    title: {
      left: 'center'
    },
    toolbox: {
      feature: {
        dataZoom: {
          yAxisIndex: 'none'
        },
        restore: {},
        saveAsImage: {}
      }
    },
    xAxis: {
      type: 'time',
      boundaryGap: false,
      data: [],
      axisTick: {
        alignWithLabel: true,
      },
    },
    yAxis: {
      type: 'value',
      boundaryGap: [0, '100%']
    },
    dataZoom: [{
      type: 'inside',
      start: 0,
      end: 100
    }, {
      start: 0,
      end: 10,
      handleSize: '80%',
    }],
    series: []
  };

  updatedOptions = undefined

  constructor(private statistics: StatisticsSearchControllerService) { }

  ngAfterContentInit(): void {
    this.isLoading = true;
    let repoId = +Id.read(this.project._links.self.href);
    this.statistics.getTotalWorkDateRangesL(repoId).pipe(
      flatMap(it => this.statistics.getTotalWorkStatsLII(repoId, it.from, it.to))
    ).subscribe(res => {
      const data = [];
      res.forEach(it => {data.push([Date.parse(it.from), it.totalCommits]) })

      this.isLoading = false;
      let update = this.options;
      update.series = [{type: 'bar', data: data}];
      this.updatedOptions = update;
    });
  }
}
