import {Role} from './Role';

export interface User{
  id: string,
  username: string,
  password: string,
  phoneNumber: string,
  firstName: string,
  lastName: string,
  role: Role
}
