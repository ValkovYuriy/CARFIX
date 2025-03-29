import { Injectable } from '@angular/core';
import {environment} from '../../environment';
import {HttpClient} from '@angular/common/http';
import {Work} from '../../model/Work';
import {ApiResponse} from '../../model/ApiResponse';
import {BehaviorSubject, tap} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class WorkService {

  private baseUrl = `${environment.baseUrl}/api`;

  private searchDataSubject = new BehaviorSubject<string[]>([]);
  searchData$ = this.searchDataSubject.asObservable();

  setSearchData(data: string[]) {
    this.searchDataSubject.next(data);
  }

  constructor(private httpClient: HttpClient) { }

  getWorks(){
    return this.httpClient.get<ApiResponse<Work[]>>(`${this.baseUrl}/works`)
  }
}
