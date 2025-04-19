import {Injectable, Output} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {environment} from '../../environment';
import {JwtHelperService} from '@auth0/angular-jwt';
import {Router} from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  private token: any = null;

  private decodedToken: any = null;

  private baseUrl = `${environment.baseUrl}`;

  private jwtHelper: JwtHelperService;

  constructor(private httpClient: HttpClient, private router: Router) {
    this.jwtHelper = new JwtHelperService();
  }

  login(username: string, password: string) {
    const credentials = {username, password};
    return this.httpClient.post<any>(`${this.baseUrl}/auth/login`, credentials);
  }

  register(username: string, password: string, phoneNumber: string, firstName: string, lastName: string){
    const body = {username,phoneNumber,password,firstName,lastName};
    return this.httpClient.post<any>(`${this.baseUrl}/auth/register`,body);
  }

  getAuthToken(): string | null {
    return localStorage.getItem('token');
  }

  isLoggedIn(): boolean {
    return this.getAuthToken() != null;
  }

  decodeToken() {
    this.token = this.getAuthToken();
    if(this.token != null){
      this.decodedToken = this.jwtHelper.decodeToken(this.token);
    }
    return this.decodedToken;
  }

  isAdmin(){
    this.decodedToken = this.decodeToken();
    return this.decodedToken.role === 'ROLE_ADMIN'
  }

  isTokenValid() {
    const token = this.token;
    if (!token) {
      return false;
    }

    const isTokenExpired = this.jwtHelper.isTokenExpired(token);
    if (isTokenExpired) {
      localStorage.clear();
      this.redirectToLogin();
      return false;
    }
    return true;
  }

  logout(){
    this.httpClient.post(`${this.baseUrl}/logout`,null);
    localStorage.clear();
  }

  private redirectToLogin(): void {
    this.router.navigate(['/login']); // Измените на путь вашей страницы логина
  }

}
