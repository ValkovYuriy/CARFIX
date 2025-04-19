import {Component, OnInit} from '@angular/core';
import {OrderService} from '../../../services/OrderService/order.service';
import {Order} from '../../../model/Order';
import {AuthenticationService} from '../../../services/AuthenticationService/authentication.service';
import {catchError, of} from 'rxjs';
import {DatePipe} from '@angular/common';
import {BsModalService,BsModalRef} from 'ngx-bootstrap/modal';
import {OrderDetailsComponent} from './order-details/order-details.component';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [
    DatePipe
  ],
  templateUrl: './orders.component.html',
  styleUrl: './orders.component.css'
})
export class OrdersComponent implements OnInit{

  orders: Order[] = [];
  decodedToken: any = null;
  bsModalRef: BsModalRef | undefined;

  constructor(private orderService: OrderService, private authService: AuthenticationService, private modalService: BsModalService) {

  }


  ngOnInit() {
    this.decodedToken = this.authService.decodeToken();
    this.getAllOrders();
  }

  getAllOrders(){
    this.orderService.findAllOrders(this.decodedToken.id).pipe(
      catchError(err => {
        console.error("Ошибка при загрузке заказов",err);
        return of([]);
      })
    ).subscribe(response => {
      this.orders = response.data;
    })
  }

  showOrderDetails(order: Order): void {
    const initialState = {
      order: order
    };
    this.bsModalRef = this.modalService.show(OrderDetailsComponent,{initialState,class: 'modal-dialog-centered'} as any);

  }
}
