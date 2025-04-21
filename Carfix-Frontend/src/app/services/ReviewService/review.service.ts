import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Review} from '../../model/Review';
import {environment} from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ReviewService {

  baseUrl = `${environment.baseUrl}/api`;

  constructor(private httpClient: HttpClient) { }

  getReviews(){
    return this.httpClient.get<any>(`${this.baseUrl}/reviews`);
  }

  createReview(review: Review){
    return this.httpClient.post<any>(`${this.baseUrl}/reviews`,review);
  }
}
