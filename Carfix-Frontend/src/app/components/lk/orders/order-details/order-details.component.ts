import {Component, Input} from '@angular/core';
import {Order} from '../../../../model/Order';
import {BsModalRef} from 'ngx-bootstrap/modal';
import {DatePipe, NgForOf, NgIf} from '@angular/common';

@Component({
  selector: 'app-order-details',
  standalone: true,
  imports: [
    DatePipe,
    NgForOf,
    NgIf
  ],
  templateUrl: './order-details.component.html',
  styleUrl: './order-details.component.css'
})
export class OrderDetailsComponent {

  @Input() order: Order | undefined;

  constructor(public bsModalRef: BsModalRef) {}

  ngOnInit(): void {}
}
