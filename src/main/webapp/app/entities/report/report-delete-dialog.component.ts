import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IReport } from 'app/shared/model/report.model';
import { ReportService } from './report.service';

@Component({
  templateUrl: './report-delete-dialog.component.html'
})
export class ReportDeleteDialogComponent {
  report?: IReport;

  constructor(protected reportService: ReportService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.reportService.delete(id).subscribe(() => {
      this.eventManager.broadcast('reportListModification');
      this.activeModal.close();
    });
  }
}
