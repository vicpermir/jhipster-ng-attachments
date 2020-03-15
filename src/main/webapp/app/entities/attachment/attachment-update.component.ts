import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { IAttachment, Attachment } from 'app/shared/model/attachment.model';
import { AttachmentService } from './attachment.service';

@Component({
  selector: 'jhi-attachment-update',
  templateUrl: './attachment-update.component.html'
})
export class AttachmentUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    filename: [null, [Validators.required]],
    originalFilename: [null, [Validators.required]],
    extension: [null, [Validators.required]],
    sizeInBytes: [null, [Validators.required]],
    sha256: [null, [Validators.required]],
    contentType: [null, [Validators.required]],
    uploadDate: [null, [Validators.required]]
  });

  constructor(protected attachmentService: AttachmentService, protected activatedRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ attachment }) => {
      if (!attachment.id) {
        const today = moment().startOf('day');
        attachment.uploadDate = today;
      }

      this.updateForm(attachment);
    });
  }

  updateForm(attachment: IAttachment): void {
    this.editForm.patchValue({
      id: attachment.id,
      filename: attachment.filename,
      originalFilename: attachment.originalFilename,
      extension: attachment.extension,
      sizeInBytes: attachment.sizeInBytes,
      sha256: attachment.sha256,
      contentType: attachment.contentType,
      uploadDate: attachment.uploadDate ? attachment.uploadDate.format(DATE_TIME_FORMAT) : null
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const attachment = this.createFromForm();
    if (attachment.id !== undefined) {
      this.subscribeToSaveResponse(this.attachmentService.update(attachment));
    } else {
      this.subscribeToSaveResponse(this.attachmentService.create(attachment));
    }
  }

  private createFromForm(): IAttachment {
    return {
      ...new Attachment(),
      id: this.editForm.get(['id'])!.value,
      filename: this.editForm.get(['filename'])!.value,
      originalFilename: this.editForm.get(['originalFilename'])!.value,
      extension: this.editForm.get(['extension'])!.value,
      sizeInBytes: this.editForm.get(['sizeInBytes'])!.value,
      sha256: this.editForm.get(['sha256'])!.value,
      contentType: this.editForm.get(['contentType'])!.value,
      uploadDate: this.editForm.get(['uploadDate'])!.value ? moment(this.editForm.get(['uploadDate'])!.value, DATE_TIME_FORMAT) : undefined
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAttachment>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }
}
