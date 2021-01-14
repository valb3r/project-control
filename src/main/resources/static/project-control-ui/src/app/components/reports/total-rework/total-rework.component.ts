import {AfterContentInit, Component, Input} from '@angular/core';
import {EntityModelGitRepo, StatisticsSearchControllerService} from "../../../api";
import {ChartsConfig} from "../charts-config";
import {Id} from "../../../id";
import {flatMap} from "rxjs/internal/operators";

@Component({
  selector: 'app-total-rework',
  templateUrl: './total-rework.component.html',
  styleUrls: ['./total-rework.component.scss']
})
export class TotalReworkComponent implements AfterContentInit {

  @Input() project: EntityModelGitRepo;

  isLoading = false;

  options = ChartsConfig.defaultBarChart()
  updatedOptions = undefined

  constructor(private statistics: StatisticsSearchControllerService) { }

  ngAfterContentInit(): void {
    this.isLoading = true;
    let repoId = +Id.read(this.project._links.self.href);
    this.statistics.getTotalWorkDateRangesL(repoId).pipe(
      flatMap(it => this.statistics.getTotalRemovedLinesStatsLII(repoId, it.from, it.to))
    ).subscribe(res => {
      const data = [];
      res.forEach(it => {data.push([Date.parse(it.from), it.removedLinesOfOthers]) })

      this.isLoading = false;
      let update = this.options;
      update.series = [{type: 'bar', data: data}];
      this.updatedOptions = update;
    });
  }
}
