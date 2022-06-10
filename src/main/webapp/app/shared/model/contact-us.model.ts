import dayjs from 'dayjs';

export interface IContactUs {
  id?: string;
  userId?: string | null;
  email?: string;
  message?: string | null;
  createTime?: string | null;
}

export const defaultValue: Readonly<IContactUs> = {};
