import {AfterViewInit, Component, OnInit} from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import {NgxMaskDirective} from 'ngx-mask';
import {
  AbstractControl,
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  ValidationErrors, ValidatorFn,
  Validators
} from "@angular/forms";
import {NgClass, NgIf, NgOptimizedImage} from '@angular/common';
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
    ReactiveFormsModule,
    NgClass
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent implements OnInit{

  errorMessage: string | null = null;

  signUp: FormGroup = new FormGroup({
      email: new FormControl('', [
        Validators.required, Validators.email,Validators.minLength(5),Validators.maxLength(50)
      ]),
      password: new FormControl('', [
        Validators.required, Validators.minLength(4),Validators.maxLength(30)
      ]),
      confirmPassword: new FormControl('',[Validators.required,this.passwordMatchValidator()]),
      phoneNumber: new FormControl('',[Validators.pattern(/^\+?[0-9]{10,15}$/)]),
      firstName: new FormControl('',[Validators.minLength(2),Validators.maxLength(100)]),
      lastName: new FormControl('',[Validators.minLength(2),Validators.maxLength(100)]),
      agreeToTerms: new FormControl(false,Validators.requiredTrue)
    }
  );

  passwordMatchValidator(): ValidatorFn {
    return (control:AbstractControl) : ValidationErrors | null => {

      const value = control.value;

      if (!value) {
        return null;
      }

      if(this.signUp){
        const confirmPassword = this.signUp.get('confirmPassword')?.value;
        const password = this.signUp.get('password')?.value;
        return password === confirmPassword ? null : { mismatch: true };
      }
      return null;
    }
  }

  constructor(private authenticationService: AuthenticationService, private router: Router) {}

  ngOnInit() {

  }

  register(){
    if(!this.signUp.get('agreeToTerms')?.value){
      this.errorMessage = 'Пожалуйста подтвердите согласие на обработку персональных данных'
    }
    else if(this.signUp.valid){
      this.authenticationService.register(
        this.signUp.get('email')?.value,
        this.signUp.get('password')?.value,
        '7' + this.signUp.get('phoneNumber')?.value,
        this.signUp.get('firstName')?.value,
        this.signUp.get('lastName')?.value)
        .pipe(
        catchError(err => {
          this.errorMessage = "Произошла ошибка при регистрации";
          console.error(this.errorMessage,err);
          return of(null);
        })
      ).subscribe(response =>{
        localStorage.setItem("token",response.token);
        this.router.navigate(['/']);
      });
    }
    else {
      this.errorMessage = 'Заполните все необходимые поля'
    }
  }

}
