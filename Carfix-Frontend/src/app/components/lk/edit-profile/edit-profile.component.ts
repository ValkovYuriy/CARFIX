import {Component, OnInit} from '@angular/core';
import {NgxMaskDirective} from "ngx-mask";
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {RouterLink} from "@angular/router";
import {NgClass} from '@angular/common';
import {UserService} from '../../../services/UserService/user.service';
import {AuthenticationService} from '../../../services/AuthenticationService/authentication.service';
import {catchError, of} from 'rxjs';
import {getXHRResponse} from 'rxjs/internal/ajax/getXHRResponse';
import {PersonalDataForm} from '../../../form-groups/data-forms';

@Component({
  selector: 'app-edit-profile',
  standalone: true,
  imports: [
    NgxMaskDirective,
    ReactiveFormsModule,
    NgClass
  ],
  templateUrl: './edit-profile.component.html',
  styleUrl: './edit-profile.component.css'
})
export class EditProfileComponent implements OnInit{

  errorMessage: string | null = null;

  editProfile: FormGroup = PersonalDataForm.create();
  // new FormGroup({
  //     email: new FormControl('', [
  //       Validators.email,Validators.minLength(5),Validators.maxLength(50)
  //     ]),
  //     phoneNumber: new FormControl('',[Validators.pattern(/^\+?[0-9]{10,15}$/)]),
  //     firstName: new FormControl('',[Validators.minLength(2),Validators.maxLength(100)]),
  //     lastName: new FormControl('',[Validators.minLength(2),Validators.maxLength(100)])
  //   }
  // );

  ngOnInit() {
    const decodedToken = this.authService.decodeToken();
    this.editProfile.patchValue({
      email: decodedToken.sub,
      phoneNumber: decodedToken.phoneNumber.substring(1,decodedToken.phoneNumber.length),
      firstName: decodedToken.firstName,
      lastName: decodedToken.lastName
    })
  }

  constructor(private userService: UserService, private authService: AuthenticationService) {
  }

  updateProfile() {
    if (this.editProfile.valid){
      const user= {
        username: this.editProfile.get('email')?.value,
        phoneNumber: '7' + this.editProfile.get('phoneNumber')?.value,
        firstName: this.editProfile.get('firstName')?.value,
        lastName: this.editProfile.get('lastName')?.value,
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
