import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {User} from '../../model/User';
import {environment} from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  baseUrl = `${environment.baseUrl}/api`

  constructor(private httpClient: HttpClient) { }

  updateUser(user: any, id: string){
    return this.httpClient.patch<any>(`${this.baseUrl}/users/${id}`,user);
  }
}
