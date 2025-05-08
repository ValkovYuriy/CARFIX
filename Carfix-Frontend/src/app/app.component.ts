import {AfterViewInit, Component, effect, inject, Input, OnInit, signal} from '@angular/core';
import {NavigationEnd, Router, RouterLink, RouterOutlet} from '@angular/router';
import {HeaderComponent} from './components/header/header.component';
import {FooterComponent} from './components/footer/footer.component';
import {Work} from './model/Work';
import {WorkService} from './services/WorkService/work.service';
import {catchError, filter, of, tap} from 'rxjs';
import {ApiResponse} from './model/ApiResponse';
import * as serviceDetails from './shared/js/services.js'
import {AuthenticationService} from './services/AuthenticationService/authentication.service';
import {NgOptimizedImage} from '@angular/common';
import {TooltipModule} from 'ngx-bootstrap/tooltip';
import {YandexMapComponent} from './components/yandex-map/yandex-map.component';
import {PopularWork} from './model/PopularWork';



@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, HeaderComponent, FooterComponent, NgOptimizedImage, TooltipModule, YandexMapComponent, RouterLink],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit{
  title = 'CARFIX';
  searchData: string[] = [];
  token : any = null;
  constructor(private router: Router, protected workService: WorkService, protected authService: AuthenticationService) {}

  isMain(){
    return this.router.url === '/home';
  }

  ngOnInit() {
    this.loadWorks();
    this.loadPopularWorks();
  }

  loadWorks(){
    this.workService.getWorks();
  }

  loadPopularWorks(){
    this.workService.getPopularWorks();
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
