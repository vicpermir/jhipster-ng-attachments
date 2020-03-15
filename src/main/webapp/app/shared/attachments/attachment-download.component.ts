import { Component, Input } from '@angular/core';
import { IAttachment } from '../model/attachment.model';
import { JhiDataUtils } from 'ng-jhipster';

@Component({
  selector: 'jhi-attachment-download',
  template: `
    <div *ngIf="attachments && attachments.length > 0">
      <dt>Attachments</dt>
      <dl *ngFor="let attachment of attachments" class="row">
        <dt class="col-sm-6">
          <fa-icon [icon]="'paperclip'"></fa-icon>
          <a *ngIf="attachment.id" target="_blank" title="Download attachment" href="api/attachments/{{ attachment.id }}/download">
            {{ attachment.originalFilename }}
          </a>
          <span class="badge badge-pill badge-light" [innerHtml]="attachment.sizeInBytes | fileSize"></span>
        </dt>
      </dl>
    </div>
  `,
  providers: [JhiDataUtils]
})
export class JhiAttachmentDownloadComponent {
  @Input()
  attachments: IAttachment[] = [];
}
