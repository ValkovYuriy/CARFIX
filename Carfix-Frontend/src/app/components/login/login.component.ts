import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {AuthenticationService} from '../../services/AuthenticationService/authentication.service';
import {catchError, of} from 'rxjs';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {NgClass, NgOptimizedImage} from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    FormsModule,
    RouterLink,
    ReactiveFormsModule,
    NgOptimizedImage,
    NgClass
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit{
  errorMessage: string | null = null;
  logout: boolean = false;

  constructor(private route: ActivatedRoute, private router: Router,private authenticationService: AuthenticationService) {}

  signIn: FormGroup = new FormGroup({
      email: new FormControl('', [
        Validators.required, Validators.email,Validators.minLength(5),Validators.maxLength(50)
      ]),
      password: new FormControl('', [
        Validators.required, Validators.minLength(4),Validators.maxLength(30)
      ])
    }
  );

  ngOnInit(): void {
    this.logout = this.route.snapshot.queryParamMap.has('logout');
  }

  login() {
    if(this.signIn.valid){
      this.authenticationService.login(this.signIn.get('email')?.value,this.signIn.get('password')?.value).pipe(
        catchError(err => {
          this.errorMessage = "Произошла ошибка при аутентификации";
          console.error(this.errorMessage,err);
          return of(null);
        })
      ).subscribe(response =>{
          localStorage.setItem("token", response.token);
          this.router.navigate(['/']);
      });
    }

  }
}
