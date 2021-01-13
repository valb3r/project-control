import {AfterViewInit, Component, Input, OnInit} from '@angular/core';
import {EntityModelGitRepo, StatisticsSearchControllerService, UserSearchControllerService} from "../../api";

@Component({
  selector: 'app-project-activity',
  templateUrl: './project-activity.component.html',
  styleUrls: ['./project-activity.component.scss']
})
export class ProjectActivityComponent implements AfterViewInit {

  @Input() project: EntityModelGitRepo;

  isLoading = false;
  series: Series[] = []

  options = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross',
        label: {
          backgroundColor: '#6a7985'
        }
      }
    },
    legend: {
      data: ['X-1', 'X-2', 'X-3', 'X-4', 'X-5']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: [
      {
        type: 'category',
        boundaryGap: false,
        data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
      }
    ],
    yAxis: [
      {
        type: 'value'
      }
    ],
    series: this.series
  };

  constructor(private statistics: StatisticsSearchControllerService) { }

  ngAfterViewInit(): void {

  }
}

class Series {
  name: string
  type: string
  stack: string
  areaStyle: { normal: {} }
  data: number[]
}
