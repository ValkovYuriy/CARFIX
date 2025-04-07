import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Order} from '../../model/Order';
import {environment} from '../../environment';

@Injectable({
  providedIn: 'root'
})
export class OrderService {

  baseUrl = `${environment.baseUrl}/api`

  constructor(private httpClient: HttpClient) { }

  findAllOrders(userId: string){
    return this.httpClient.get<any>(`${this.baseUrl}/orders?userid=${userId}`)
  }

  createOrder(order: Order){
    return this.httpClient.post<any>(`${this.baseUrl}/orders`,order);
  }
}
