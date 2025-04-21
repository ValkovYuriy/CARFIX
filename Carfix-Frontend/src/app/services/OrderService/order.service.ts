import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Order} from '../../model/Order';
import {Status} from '../../model/Status';
import {environment} from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class OrderService {

  baseUrl = `${environment.baseUrl}/api`

  constructor(private httpClient: HttpClient) { }

  findAllOrders(userId: string | null){
    return userId === null ? this.httpClient.get<any>(`${this.baseUrl}/orders`) : this.httpClient.get<any>(`${this.baseUrl}/orders?userId=${userId}`)
  }

  updateOrderStatus(id: string, status: Status){
    const headers = { 'Content-Type': 'application/json' };
    return this.httpClient.patch<any>(`${this.baseUrl}/orders/${id}`,`"${status}"`, { headers });
  }

  createOrder(order: Order){
    return this.httpClient.post<any>(`${this.baseUrl}/orders`,order);
  }
}
