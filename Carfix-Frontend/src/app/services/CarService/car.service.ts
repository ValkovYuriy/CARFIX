import { Injectable } from '@angular/core';
import {environment} from '../../../environments/environment';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class CarService {

  private baseUrl: string = `${environment.baseUrl}/api`

  constructor(private httpClient: HttpClient) { }

  getCarByVin(vin: string){
    return this.httpClient.get<any>(`${this.baseUrl}/cars/${vin.toUpperCase()}`);
  }
}
