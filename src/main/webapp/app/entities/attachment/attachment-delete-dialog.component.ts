import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IAttachment } from 'app/shared/model/attachment.model';
import { AttachmentService } from './attachment.service';

@Component({
  templateUrl: './attachment-delete-dialog.component.html'
})
export class AttachmentDeleteDialogComponent {
  attachment?: IAttachment;

  constructor(
    protected attachmentService: AttachmentService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.attachmentService.delete(id).subscribe(() => {
      this.eventManager.broadcast('attachmentListModification');
      this.activeModal.close();
    });
  }
}
