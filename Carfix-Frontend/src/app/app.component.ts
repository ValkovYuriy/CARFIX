import {AfterViewInit, Component, inject, Input, OnInit, signal} from '@angular/core';
import {NavigationEnd, Router, RouterOutlet} from '@angular/router';
import {HeaderComponent} from './components/header/header.component';
import {FooterComponent} from './components/footer/footer.component';
import {Work} from './model/Work';
import {WorkService} from './services/WorkService/work.service';
import {catchError, filter, of, tap} from 'rxjs';
import {ApiResponse} from './model/ApiResponse';
import * as serviceDetails from './shared/js/services.js'
import {AuthenticationService} from './services/AuthenticationService/authentication.service';
import {NgOptimizedImage} from '@angular/common';



@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, HeaderComponent, FooterComponent, NgOptimizedImage],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit{
  title = 'CARFIX';
  services= signal<Work[]>([]);
  searchData: string[] = [];
  token : any = null;
  decodedToken : {firstName: string, lastName: string} = {firstName: '', lastName: ''};

  constructor(private router: Router, private workService: WorkService, protected authService: AuthenticationService) {}

  isMain(){
    return this.router.url === '/';
  }

  ngOnInit() {
    this.loadServices();
  }

  loadServices(){
    this.workService.getWorks().pipe(
      catchError(err => {
        console.error("Ошибка при загрузке услуг",err);
        return of({ message: 'Ошибка', data: [] } as ApiResponse<Work[]>);
      })
    ).subscribe(
      response => {
        this.services.set(response.data)
        this.services().forEach(work =>{
          this.searchData.push(work.name);
        })
        this.workService.setSearchData(this.searchData)
      }
    );
  }

  showServiceDetails(service: Work){
    serviceDetails.showServiceDetails(service);
  }

  scrollTo(id: string): void {
    const element = document.getElementById(id);
    if (element) {
      element.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  }

}
