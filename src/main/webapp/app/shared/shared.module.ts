import { NgModule } from '@angular/core';
import { JhAttachmentsSharedLibsModule } from './shared-libs.module';
import { FindLanguageFromKeyPipe } from './language/find-language-from-key.pipe';
import { AlertComponent } from './alert/alert.component';
import { AlertErrorComponent } from './alert/alert-error.component';
import { LoginModalComponent } from './login/login.component';
import { HasAnyAuthorityDirective } from './auth/has-any-authority.directive';
import { FileSizePipe } from './util/file-size.pipe';
import { JhiAttachmentUploadComponent } from './attachments/attachment-upload.component';
import { JhiAttachmentDownloadComponent } from './attachments/attachment-download.component';

@NgModule({
  imports: [JhAttachmentsSharedLibsModule],
  declarations: [
    FindLanguageFromKeyPipe,
    AlertComponent,
    AlertErrorComponent,
    LoginModalComponent,
    HasAnyAuthorityDirective,
    FileSizePipe,
    JhiAttachmentUploadComponent,
    JhiAttachmentDownloadComponent
  ],
  entryComponents: [LoginModalComponent],
  exports: [
    JhAttachmentsSharedLibsModule,
    FindLanguageFromKeyPipe,
    AlertComponent,
    AlertErrorComponent,
    LoginModalComponent,
    HasAnyAuthorityDirective,
    FileSizePipe,
    JhiAttachmentUploadComponent,
    JhiAttachmentDownloadComponent
  ]
})
export class JhAttachmentsSharedModule {}
