import {AfterViewInit, Component, OnInit} from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import {NgxMaskDirective} from 'ngx-mask';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {NgOptimizedImage} from '@angular/common';
import {catchError, of} from 'rxjs';
import {AuthenticationService} from '../../services/AuthenticationService/authentication.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    RouterLink,
    NgxMaskDirective,
    FormsModule,
    NgOptimizedImage,
    ReactiveFormsModule
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent implements OnInit{

  signUp: FormGroup = new FormGroup({
      email: new FormControl('', [
        Validators.required
      ]),
      password: new FormControl('', [
        Validators.required
      ]),
      phoneNumber: new FormControl(),
      firstName: new FormControl(),
      lastName: new FormControl()
    }
  );

  constructor(private authenticationService: AuthenticationService, private router: Router) {

  }

  ngOnInit() {

  }

  register(){
    if(this.signUp.valid){
      this.authenticationService.register(
        this.signUp.get('email')?.value,
        this.signUp.get('password')?.value,
        '7' + this.signUp.get('phoneNumber')?.value,
        this.signUp.get('firstName')?.value,
        this.signUp.get('lastName')?.value)
        .pipe(
        catchError(err => {
          console.error("Произошла ошибка при регистрации",err);
          return of(null);
        })
      ).subscribe(response =>{
        localStorage.setItem("token",response.token);
        this.router.navigate(['']);
      });
    }
  }
}
