import { IAttachment } from 'app/shared/model/attachment.model';

export interface IReport {
  id?: number;
  name?: string;
  attachments?: IAttachment[];
}

export class Report implements IReport {
  constructor(public id?: number, public name?: string, public attachments?: IAttachment[]) {}
}
