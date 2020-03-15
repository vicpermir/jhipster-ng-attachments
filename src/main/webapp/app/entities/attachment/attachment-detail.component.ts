import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IAttachment } from 'app/shared/model/attachment.model';

@Component({
  selector: 'jhi-attachment-detail',
  templateUrl: './attachment-detail.component.html'
})
export class AttachmentDetailComponent implements OnInit {
  attachment: IAttachment | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ attachment }) => (this.attachment = attachment));
  }

  previousState(): void {
    window.history.back();
  }
}
