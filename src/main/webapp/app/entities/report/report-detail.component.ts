import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IReport } from 'app/shared/model/report.model';

@Component({
  selector: 'jhi-report-detail',
  templateUrl: './report-detail.component.html'
})
export class ReportDetailComponent implements OnInit {
  report: IReport | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ report }) => (this.report = report));
  }

  previousState(): void {
    window.history.back();
  }
}
