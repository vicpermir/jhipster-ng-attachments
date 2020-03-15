import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { JhAttachmentsTestModule } from '../../../test.module';
import { AttachmentDetailComponent } from 'app/entities/attachment/attachment-detail.component';
import { Attachment } from 'app/shared/model/attachment.model';

describe('Component Tests', () => {
  describe('Attachment Management Detail Component', () => {
    let comp: AttachmentDetailComponent;
    let fixture: ComponentFixture<AttachmentDetailComponent>;
    const route = ({ data: of({ attachment: new Attachment(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [JhAttachmentsTestModule],
        declarations: [AttachmentDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(AttachmentDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(AttachmentDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load attachment on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.attachment).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
