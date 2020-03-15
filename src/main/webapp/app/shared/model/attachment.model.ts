import { Moment } from 'moment';

export interface IAttachment {
  id?: number;
  filename?: string;
  originalFilename?: string;
  extension?: string;
  sizeInBytes?: number;
  sha256?: string;
  contentType?: string;
  uploadDate?: Moment;
  file?: any;
  processing?: boolean;
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
    public file?: any,
    public processing?: boolean
  ) {}
}
