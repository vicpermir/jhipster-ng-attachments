import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IReport, Report } from 'app/shared/model/report.model';
import { ReportService } from './report.service';
import { ReportComponent } from './report.component';
import { ReportDetailComponent } from './report-detail.component';
import { ReportUpdateComponent } from './report-update.component';

@Injectable({ providedIn: 'root' })
export class ReportResolve implements Resolve<IReport> {
  constructor(private service: ReportService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IReport> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((report: HttpResponse<Report>) => {
          if (report.body) {
            return of(report.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Report());
  }
}

export const reportRoute: Routes = [
  {
    path: '',
    component: ReportComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams
    },
    data: {
      authorities: [Authority.USER],
      defaultSort: 'id,asc',
      pageTitle: 'jhAttachmentsApp.report.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: ReportDetailComponent,
    resolve: {
      report: ReportResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'jhAttachmentsApp.report.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: ReportUpdateComponent,
    resolve: {
      report: ReportResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'jhAttachmentsApp.report.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: ReportUpdateComponent,
    resolve: {
      report: ReportResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'jhAttachmentsApp.report.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];
