import {AfterViewInit, Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {AuthenticationService} from '../../services/AuthenticationService/authentication.service';
import {NzStepComponent, NzStepsComponent} from 'ng-zorro-antd/steps';
import {NzIconDirective, NzIconModule} from 'ng-zorro-antd/icon';
import {NzButtonComponent} from 'ng-zorro-antd/button';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {NgxMaskDirective} from 'ngx-mask';
import {CarDataComponent} from '../car-data/car-data.component';
import {ServiceDataComponent} from '../service-data/service-data.component';
import {RouterLink} from '@angular/router';


@Component({
  selector: 'app-registration-for-service',
  standalone: true,
  imports: [
    NzIconModule,
    NzStepsComponent,
    NzStepComponent,
    NzButtonComponent,
    ReactiveFormsModule,
    NgxMaskDirective,
    CarDataComponent,
    ServiceDataComponent,
    FormsModule,
    RouterLink
  ],
  templateUrl: './registration-for-service.component.html',
  styleUrl: './registration-for-service.component.css',
})
export class RegistrationForServiceComponent implements OnInit {

  decodedToken: any = null;
  current = 0;
  index = 'First-content';
  formattedPhoneNumber: string | null = null;

  constructor(private authService: AuthenticationService) {
  }

  ngOnInit() {
    this.decodedToken = this.authService.decodeToken();
    this.formattedPhoneNumber = this.formatPhoneNumber(this.decodedToken.phoneNumber);
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
    console.log('done');
  }

  changeContent(): void {
    switch (this.current) {
      case 0: {
        break;
      }
      case 1: {
        break;
      }
      case 2: {
        break;
      }
      default: {
        this.index = 'error';
      }
    }
  }

  formatPhoneNumber(phoneNumber: string): string {
    // Удаляем префикс +7, если он есть
    if (phoneNumber.startsWith('7')) {
      phoneNumber = phoneNumber.substring(1);
    }
    // Преобразуем номер в формат (000) 000-00-00
    const formattedNumber = `(${phoneNumber.substring(0, 3)}) ${phoneNumber.substring(3, 6)}-${phoneNumber.substring(6, 8)}-${phoneNumber.substring(8, 10)}`;
    return formattedNumber;
  }

}
