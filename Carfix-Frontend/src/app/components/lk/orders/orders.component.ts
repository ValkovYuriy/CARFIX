import {Component, OnInit, signal} from '@angular/core';
import {OrderService} from '../../../services/OrderService/order.service';
import {Order} from '../../../model/Order';
import {AuthenticationService} from '../../../services/AuthenticationService/authentication.service';
import {catchError, of} from 'rxjs';
import {DatePipe} from '@angular/common';
import {BsModalRef, BsModalService} from 'ngx-bootstrap/modal';
import {OrderDetailsComponent} from './order-details/order-details.component';
import {Status} from '../../../model/Status';

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

  orders = signal<Order[]>([]);
  decodedToken: any = null;
  bsModalRef: BsModalRef | undefined;
  rowsPerPage = 10;
  currentPage = signal(1);

  constructor(private orderService: OrderService, protected authService: AuthenticationService, private modalService: BsModalService) {

  }


  ngOnInit() {
    this.decodedToken = this.authService.decodeToken();
    this.getAllOrders();
  }

  getPaginatedData() {
    const start = (this.currentPage() - 1) * this.rowsPerPage;
    const end = this.currentPage() * this.rowsPerPage;
    return this.orders().slice(start, end);
  }

  nextPage(): void {
    if (this.currentPage() < Math.ceil(this.orders().length / this.rowsPerPage)) {
      this.currentPage.update(page => page + 1);
    }
  }

  prevPage(): void {
    if (this.currentPage() > 1) {
      this.currentPage.update(page => page - 1);
    }
  }


  getTotalPages(): number {
    return Math.ceil(this.orders().length / this.rowsPerPage);
  }

  getAllOrders(){
    this.orderService.findAllOrders(this.authService.isAdmin() ? null : this.decodedToken.id).pipe(
      catchError(err => {
        console.error("Ошибка при загрузке заказов",err);
        return of([]);
      })
    ).subscribe(response => {
      this.orders.set(response.data);
    })
  }

  showOrderDetails(order: Order): void {
    const initialState = {
      order: order
    };
    this.bsModalRef = this.modalService.show(OrderDetailsComponent,{initialState,class: 'modal-dialog-centered'} as any);

  }

  updateStatus(order: Order,status: Status) {
    this.orderService.updateOrderStatus(order.id!,status).pipe(
      catchError(err => {
        console.error('Произошла ошибка при обновлении статуса заявки',err);
        alert('Произошла ошибка при обновлении статуса заявки');
        return of(null);
      })
    ).subscribe(response => {
      console.log('Статус заявки изменен',response.data);
      this.orders.update((currentOrders) => currentOrders.map((order) =>
        (order.id === response.data.id ? response.data : order)))
    })
  }

  protected readonly Status = Status;
}
