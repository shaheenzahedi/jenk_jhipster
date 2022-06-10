import { IHelpApp } from 'app/shared/model/help-app.model';
import { StaticPageStatus } from 'app/shared/model/enumerations/static-page-status.model';

export interface IStaticPage {
  id?: string;
  name?: string;
  content?: string;
  status?: StaticPageStatus | null;
  fileId?: string | null;
  helpApp?: IHelpApp | null;
}

export const defaultValue: Readonly<IStaticPage> = {};
