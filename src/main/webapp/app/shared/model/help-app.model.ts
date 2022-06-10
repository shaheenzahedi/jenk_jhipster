import { IStaticPage } from 'app/shared/model/static-page.model';

export interface IHelpApp {
  id?: string;
  staticPageId?: string | null;
  staticPageIds?: IStaticPage[] | null;
}

export const defaultValue: Readonly<IHelpApp> = {};
