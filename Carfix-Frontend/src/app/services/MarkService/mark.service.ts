import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environment';
import {ApiResponse} from '../../model/ApiResponse';
import {Mark} from '../../model/Mark';

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
