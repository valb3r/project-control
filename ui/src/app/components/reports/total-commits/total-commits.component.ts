import {AfterContentInit, Component, Input} from '@angular/core';
import {EntityModelGitRepo, StatisticsSearchControllerService} from "../../../api";
import {Id} from "../../../id";
import {ChartsConfig} from "../charts-config";
import {mergeMap} from "rxjs/operators";

@Component({
  selector: 'app-total-commits',
  templateUrl: './total-commits.component.html',
  styleUrls: ['./total-commits.component.scss']
})
export class TotalCommitsComponent implements AfterContentInit {

  @Input() project: EntityModelGitRepo;

  isLoading = false;
  chartData = [];

  options = ChartsConfig.defaultBarChart()
  updatedOptions = undefined

  constructor(private statistics: StatisticsSearchControllerService) { }

  ngAfterContentInit(): void {
    this.isLoading = true;
    let repoId = +Id.read(this.project._links.self.href);
    this.statistics.getTotalWorkDateRangesL(repoId).pipe(
      mergeMap(it => this.statistics.getTotalWorkStatsLII(repoId, it.from, it.to))
    ).subscribe(res => {
      this.chartData = [];
      res.forEach(it => {this.chartData.push([Date.parse(it.from), it.totalCommits]) })

      this.isLoading = false;
      let update = this.options;
      update.series = [{type: 'bar', data: this.chartData}];
      this.updatedOptions = update;
    });
  }
}
