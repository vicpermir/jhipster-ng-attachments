import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { JhAttachmentsTestModule } from '../../../test.module';
import { AttachmentUpdateComponent } from 'app/entities/attachment/attachment-update.component';
import { AttachmentService } from 'app/entities/attachment/attachment.service';
import { Attachment } from 'app/shared/model/attachment.model';

describe('Component Tests', () => {
  describe('Attachment Management Update Component', () => {
    let comp: AttachmentUpdateComponent;
    let fixture: ComponentFixture<AttachmentUpdateComponent>;
    let service: AttachmentService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [JhAttachmentsTestModule],
        declarations: [AttachmentUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(AttachmentUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(AttachmentUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(AttachmentService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Attachment(123);
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        const entity = new Attachment();
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
