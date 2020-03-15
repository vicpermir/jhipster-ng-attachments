import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { AttachmentService } from 'app/entities/attachment/attachment.service';
import { IAttachment, Attachment } from 'app/shared/model/attachment.model';

describe('Service Tests', () => {
  describe('Attachment Service', () => {
    let injector: TestBed;
    let service: AttachmentService;
    let httpMock: HttpTestingController;
    let elemDefault: IAttachment;
    let expectedResult: IAttachment | IAttachment[] | boolean | null;
    let currentDate: moment.Moment;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule]
      });
      expectedResult = null;
      injector = getTestBed();
      service = injector.get(AttachmentService);
      httpMock = injector.get(HttpTestingController);
      currentDate = moment();

      elemDefault = new Attachment(0, 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 0, 'AAAAAAA', 'AAAAAAA', currentDate);
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            uploadDate: currentDate.format(DATE_TIME_FORMAT)
          },
          elemDefault
        );

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a Attachment', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            uploadDate: currentDate.format(DATE_TIME_FORMAT)
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            uploadDate: currentDate
          },
          returnedFromService
        );

        service.create(new Attachment()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Attachment', () => {
        const returnedFromService = Object.assign(
          {
            filename: 'BBBBBB',
            originalFilename: 'BBBBBB',
            extension: 'BBBBBB',
            sizeInBytes: 1,
            sha256: 'BBBBBB',
            contentType: 'BBBBBB',
            uploadDate: currentDate.format(DATE_TIME_FORMAT)
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            uploadDate: currentDate
          },
          returnedFromService
        );

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Attachment', () => {
        const returnedFromService = Object.assign(
          {
            filename: 'BBBBBB',
            originalFilename: 'BBBBBB',
            extension: 'BBBBBB',
            sizeInBytes: 1,
            sha256: 'BBBBBB',
            contentType: 'BBBBBB',
            uploadDate: currentDate.format(DATE_TIME_FORMAT)
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            uploadDate: currentDate
          },
          returnedFromService
        );

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Attachment', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
