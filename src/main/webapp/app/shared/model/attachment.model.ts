import { Moment } from 'moment';
import { IReport } from 'app/shared/model/report.model';

export interface IAttachment {
  id?: number;
  filename?: string;
  originalFilename?: string;
  extension?: string;
  sizeInBytes?: number;
  sha256?: string;
  contentType?: string;
  uploadDate?: Moment;
  reports?: IReport[];
}

export class Attachment implements IAttachment {
  constructor(
    public id?: number,
    public filename?: string,
    public originalFilename?: string,
    public extension?: string,
    public sizeInBytes?: number,
    public sha256?: string,
    public contentType?: string,
    public uploadDate?: Moment,
    public reports?: IReport[]
  ) {}
}
