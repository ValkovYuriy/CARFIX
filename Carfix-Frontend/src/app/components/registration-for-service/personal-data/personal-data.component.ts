import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {AuthenticationService} from '../../../services/AuthenticationService/authentication.service';
import {NgxMaskDirective} from 'ngx-mask';
import {FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {User} from '../../../model/User';
import {PersonalDataForm} from '../../../form-groups/data-forms';
import {catchError, of} from 'rxjs';
import {UserService} from '../../../services/UserService/user.service';
import {NgClass} from '@angular/common';

@Component({
  selector: 'app-personal-data',
  standalone: true,
  imports: [
    NgxMaskDirective,
    ReactiveFormsModule,
    FormsModule,
    NgClass
  ],
  templateUrl: './personal-data.component.html',
  styleUrl: './personal-data.component.css'
})
export class PersonalDataComponent implements OnInit{

  errorMessage: string | null = null;

  user: { firstName: string; lastName: string; phoneNumber: string; id: string; username: string } = {
    id: '' ,
    username: '',
    firstName: '' ,
    lastName: '' ,
    phoneNumber: ''
  };

  personalData: FormGroup = PersonalDataForm.create();

  getUserData(): User | null {
    if(this.personalData.valid){
      this.user.username = this.personalData.get('email')?.value;
      this.user.phoneNumber = '7' + this.personalData.get('phoneNumber')?.value;
      this.user.firstName = this.personalData.get('firstName')?.value;
      this.user.lastName = this.personalData.get('lastName')?.value;
      return <User>this.user;
    }else {
      return null;
    }
  }

  constructor(private authService: AuthenticationService, private userService: UserService) {
  }

  ngOnInit() {
    const decodedToken = this.authService.decodeToken();
    this.personalData.patchValue({
      email: decodedToken.sub,
      phoneNumber: decodedToken.phoneNumber.substring(1,decodedToken.phoneNumber.length),
      firstName: decodedToken.firstName,
      lastName: decodedToken.lastName
    })
  }

  updateUserData() {
    if (this.personalData.valid){
      const user= {
        username: this.personalData.get('email')?.value,
        phoneNumber: '7' + this.personalData.get('phoneNumber')?.value,
        firstName: this.personalData.get('firstName')?.value,
        lastName: this.personalData.get('lastName')?.value,
      }
      this.userService.updateUser(user,this.authService.getCurrentUserId()).pipe(
        catchError(err => {
          console.error('Произошла ошибка при обновлении данных пользователя',err);
          this.errorMessage = 'Произошла ошибка при обновлении данных пользователя';
          return of(null);
        })
      ).subscribe(response =>{
        console.log('Данные успешно обновлены');
        localStorage.setItem('token',response.data);
      });
    }
  }

}
