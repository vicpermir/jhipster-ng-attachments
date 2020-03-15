import { Component, Input } from '@angular/core';
import { IAttachment } from '../model/attachment.model';
import { JhiDataUtils } from 'ng-jhipster';
import * as hash from 'hash.js';

@Component({
  selector: 'jhi-attachment-upload',
  template: `
    <div class="form-group">
      <div [hidden]="!attachments?.length">
        <label for="field_attachments">Attachments</label>
        <dl *ngFor="let attachment of attachments; let i = index" class="row" [ngClass]="{ 'bg-light text-muted': attachment.processing }">
          <dt class="col-md-8 col-sm-10">
            <fa-icon [icon]="'paperclip'"></fa-icon>
            <a *ngIf="attachment.id" target="_blank" href="api/attachments/{{ attachment.id }}/download">
              {{ attachment.originalFilename }}
            </a>
            <span *ngIf="!attachment.id">{{ attachment.originalFilename }}</span>
            <span class="badge badge-pill badge-light" [innerHtml]="attachment.sizeInBytes | fileSize"></span>
          </dt>
          <dt class="col-md-4 col-sm-2">
            <button
              type="button"
              class="btn btn-sm btn-danger pull-right"
              *ngIf="!attachment.processing"
              (click)="attachments.splice(i, 1)"
            >
              <fa-icon [icon]="'times'"></fa-icon>
            </button>
            <span *ngIf="attachment.processing">
              <fa-icon icon="spinner" spin="true"></fa-icon>
              Reading file &hellip;
            </span>
          </dt>
        </dl>
      </div>
      <input type="file" name="attachments" id="attachment_file" (change)="addAttachment($event)" [disabled]="loadingFiles > 0" multiple />
    </div>
  `,
  providers: [JhiDataUtils]
})
export class JhiAttachmentUploadComponent {
  @Input()
  attachments: IAttachment[] = [];
  loadingFiles: number;

  constructor(private dataUtils: JhiDataUtils) {
    this.loadingFiles = 0;
  }

  addAttachment(e: any): void {
    this.loadingFiles = 0;
    if (e && e.target.files) {
      this.loadingFiles = e.target.files.length;
      for (let i = 0; i < this.loadingFiles; i++) {
        const file = e.target.files[i];
        const fileName = file.name;
        const attachment: IAttachment = {
          originalFilename: fileName,
          contentType: file.type,
          sizeInBytes: file.size,
          extension: this.getExtension(fileName),
          processing: true
        };
        this.attachments.push(attachment);
        this.dataUtils.toBase64(file, (base64Data: any) => {
          attachment.file = base64Data;
          attachment.sha256 = hash
            .sha256()
            .update(base64Data)
            .digest('hex');
          attachment.processing = false;
          this.loadingFiles--;
        });
      }
    }
    e.target.value = '';
  }

  getExtension(fileName: string): string {
    return fileName.substring(fileName.lastIndexOf('.'));
  }
}
