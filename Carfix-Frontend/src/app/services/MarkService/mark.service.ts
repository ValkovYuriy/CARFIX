import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ApiResponse} from '../../model/ApiResponse';
import {Mark} from '../../model/Mark';
import {environment} from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MarkService {

  private baseUrl = `${environment.baseUrl}/api`

  constructor(private httpClient: HttpClient) { }

  getMarks(){
    return this.httpClient.get<ApiResponse<Mark[]>>(`${this.baseUrl}/marks`)
  }
}
