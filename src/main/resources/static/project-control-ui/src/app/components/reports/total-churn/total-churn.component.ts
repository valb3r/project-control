import {AfterContentInit, Component, Input} from '@angular/core';
import {EntityModelGitRepo, StatisticsSearchControllerService} from "../../../api";
import {ChartsConfig} from "../charts-config";
import {Id} from "../../../id";
import {mergeMap} from "rxjs/internal/operators";

@Component({
  selector: 'app-total-churn',
  templateUrl: './total-churn.component.html',
  styleUrls: ['./total-churn.component.scss']
})
export class TotalChurnComponent implements AfterContentInit {

  @Input() project: EntityModelGitRepo;

  isLoading = false;

  options = ChartsConfig.defaultBarChart()
  updatedOptions = undefined

  constructor(private statistics: StatisticsSearchControllerService) { }

  ngAfterContentInit(): void {
    this.isLoading = true;
    let repoId = +Id.read(this.project._links.self.href);
    this.statistics.getTotalWorkDateRangesL(repoId).pipe(
      mergeMap(it => this.statistics.getTotalWorkStatsLII(repoId, it.from, it.to))
    ).subscribe(res => {
      const dataAdded = [];
      res.forEach(it => {dataAdded.push([Date.parse(it.from), it.linesAdded]) })
      const dataRemoved = [];
      res.forEach(it => {dataRemoved.push([Date.parse(it.from), it.linesRemoved]) })

      this.isLoading = false;
      let update = this.options;
      update.series = [
        {type: 'bar', stack: true, data: dataAdded, name: "Added", color: '#00aa00'},
        {type: 'bar', stack: true, data: dataRemoved, name: "Removed", color: '#bb0000'}
      ];
      this.updatedOptions = update;
    });
  }
}
