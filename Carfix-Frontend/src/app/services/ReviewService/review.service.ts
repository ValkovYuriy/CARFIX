import {Injectable, signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Review} from '../../model/Review';
import {environment} from '../../../environments/environment';
import {catchError, of} from 'rxjs';
import {ApiResponse} from '../../model/ApiResponse';

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
    return this.httpClient.post<ApiResponse<Review>>(`${this.baseUrl}/reviews`,review);
  }
}
