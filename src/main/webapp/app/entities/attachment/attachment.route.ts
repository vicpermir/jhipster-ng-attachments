import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IAttachment, Attachment } from 'app/shared/model/attachment.model';
import { AttachmentService } from './attachment.service';
import { AttachmentComponent } from './attachment.component';
import { AttachmentDetailComponent } from './attachment-detail.component';
import { AttachmentUpdateComponent } from './attachment-update.component';

@Injectable({ providedIn: 'root' })
export class AttachmentResolve implements Resolve<IAttachment> {
  constructor(private service: AttachmentService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IAttachment> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((attachment: HttpResponse<Attachment>) => {
          if (attachment.body) {
            return of(attachment.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Attachment());
  }
}

export const attachmentRoute: Routes = [
  {
    path: '',
    component: AttachmentComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams
    },
    data: {
      authorities: [Authority.USER],
      defaultSort: 'id,asc',
      pageTitle: 'jhAttachmentsApp.attachment.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: AttachmentDetailComponent,
    resolve: {
      attachment: AttachmentResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'jhAttachmentsApp.attachment.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: AttachmentUpdateComponent,
    resolve: {
      attachment: AttachmentResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'jhAttachmentsApp.attachment.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: AttachmentUpdateComponent,
    resolve: {
      attachment: AttachmentResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'jhAttachmentsApp.attachment.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];
