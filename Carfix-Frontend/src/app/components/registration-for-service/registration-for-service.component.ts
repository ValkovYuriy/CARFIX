import {AfterViewInit, Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {AuthenticationService} from '../../services/AuthenticationService/authentication.service';
import {NzStepComponent, NzStepsComponent} from 'ng-zorro-antd/steps';
import {NzIconDirective, NzIconModule} from 'ng-zorro-antd/icon';
import {NzButtonComponent} from 'ng-zorro-antd/button';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {NgxMaskDirective} from 'ngx-mask';
import {CarDataComponent} from './car-data/car-data.component';
import {ServiceDataComponent} from './service-data/service-data.component';
import {Router, RouterLink} from '@angular/router';
import {Order} from '../../model/Order';
import {Car} from '../../model/Car';
import {User} from '../../model/User';
import {Status} from '../../model/Status';
import {Work} from '../../model/Work';
import Big from 'big.js';
import {PersonalDataComponent} from './personal-data/personal-data.component';
import {OrderService} from '../../services/OrderService/order.service';
import {catchError, of} from 'rxjs';


@Component({
  selector: 'app-registration-for-service',
  standalone: true,
  imports: [
    NzIconModule,
    NzStepsComponent,
    NzStepComponent,
    NzButtonComponent,
    ReactiveFormsModule,
    CarDataComponent,
    ServiceDataComponent,
    FormsModule,
    RouterLink,
    PersonalDataComponent
  ],
  templateUrl: './registration-for-service.component.html',
  styleUrl: './registration-for-service.component.css',
})
export class RegistrationForServiceComponent implements OnInit {

  current = 0;
  index = 'First-content';
  order : {
    works: Work[];
    carDto: Car;
    price: number;
    description: string;
    id: string | null;
    userDto: User;
    orderDate: Date;
    status: Status
  } = {
    id: null,
    carDto: {} as Car,
    userDto: {} as User,
    orderDate: new Date(),
    price: 0,
    status: 'PENDING' as Status,
    description: '',
    works: []
};

  @ViewChild(PersonalDataComponent) personalData!: PersonalDataComponent;
  @ViewChild(CarDataComponent) carData!: CarDataComponent;
  @ViewChild(ServiceDataComponent) serviceData!: ServiceDataComponent;


  constructor(private authService: AuthenticationService,private orderService: OrderService, private router: Router) {
  }

  ngOnInit() {

  }


  onIndexChange(current: number): void {
    this.current = current;
    this.changeContent();
  }

  pre(): void {
    this.current -= 1;
    this.changeContent();
  }

  next(): void {
    this.current += 1;
    this.changeContent();
  }

  done(): void {
    const serviceData = this.serviceData.getServiceData();
    this.order.orderDate = serviceData.orderDate;
    this.order.description = serviceData.description;
    this.order.works = serviceData.works;
    this.order.price = 0;
    console.log(this.order);
    this.orderService.createOrder(this.order).pipe(
      catchError(err => {
        console.error("Произошла ошибка при сохранении заявки на обслуживание",err);
        return of(null);
      })
    ).subscribe(response =>{
      console.log(response);
      this.router.navigate(['/']);
    });
  }

  changeContent(): void {
    switch (this.current) {
      case 0: {
        break;
      }
      case 1: {
        if(this.personalData.getUserData() === null){
          this.current -= 1;
        }else{
          this.order.userDto = this.personalData.getUserData()!;
          this.personalData.updateUserData();
        }
        break;
      }
      case 2: {
        this.order.carDto = this.carData.getCarData();
        break;
      }
      default: {
        this.index = 'error';
      }
    }
  }


}
