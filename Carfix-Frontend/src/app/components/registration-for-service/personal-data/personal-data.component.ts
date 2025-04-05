import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {AuthenticationService} from '../../../services/AuthenticationService/authentication.service';
import {NgxMaskDirective} from 'ngx-mask';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {User} from '../../../model/User';

@Component({
  selector: 'app-personal-data',
  standalone: true,
  imports: [
    NgxMaskDirective,
    ReactiveFormsModule,
    FormsModule
  ],
  templateUrl: './personal-data.component.html',
  styleUrl: './personal-data.component.css'
})
export class PersonalDataComponent implements OnInit{

  decodedToken: any = null;
  // formattedPhoneNumber: string | null = null;
  user: { firstName: string; lastName: string; phoneNumber: string; id: string; username: string } = {
    id: '' ,
    username: '',
    firstName: '' ,
    lastName: '' ,
    phoneNumber: ''
  };

  // Метод для получения текущих данных пользователя
  getUserData(): User {
    this.user.phoneNumber = '7' + this.user.phoneNumber
    return <User>this.user;
  }

  constructor(private authService: AuthenticationService) {
  }

  ngOnInit() {
    this.decodedToken = this.authService.decodeToken();
    this.user.id = this.decodedToken.id;
    this.user.phoneNumber = this.decodedToken.phoneNumber.substring(1,this.decodedToken.phoneNumber.length);
    this.user.username = this.decodedToken.sub;
    this.user.firstName = this.decodedToken.firstName;
    this.user.lastName = this.decodedToken.lastName;
  }

  // formatPhoneNumber(phoneNumber: string): string {
  //   // Удаляем префикс +7, если он есть
  //   if (phoneNumber.startsWith('7')) {
  //     phoneNumber = phoneNumber.substring(1);
  //   }
  //   // Преобразуем номер в формат (000) 000-00-00
  //   const formattedNumber = `(${phoneNumber.substring(0, 3)}) ${phoneNumber.substring(3, 6)}-${phoneNumber.substring(6, 8)}-${phoneNumber.substring(8, 10)}`;
  //   return formattedNumber;
  // }
}
