import {Component, Input, OnInit} from '@angular/core';
import {parseAsync} from "json2csv";

@Component({
  selector: 'csv-export',
  templateUrl: './csv-export.component.html',
  styleUrls: ['./csv-export.component.scss']
})
export class CsvExportComponent implements OnInit {

  @Input() data: any;
  @Input() enabled: boolean;

  constructor() { }

  ngOnInit(): void {
  }

  exportToCsv() {
    if (!this.data) {
      console.warn("No data to export");
      return;
    }
    parseAsync(this.data).then(res => console.log(res));
  }
}
