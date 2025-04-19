import {AfterViewInit, Component, ElementRef, HostListener, OnInit, ViewChild} from '@angular/core';
import {NgClass, NgOptimizedImage} from '@angular/common';
import {EditProfileComponent} from './edit-profile/edit-profile.component';
import {OrdersComponent} from './orders/orders.component';
import {AuthenticationService} from '../../services/AuthenticationService/authentication.service';
import {WorksComponent} from './works/works.component';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-lk',
  standalone: true,
  imports: [
    NgClass,
    NgOptimizedImage,
    EditProfileComponent,
    OrdersComponent,
    WorksComponent,
    RouterLink
  ],
  templateUrl: './lk.component.html',
  styleUrl: './lk.component.css'
})
export class LkComponent implements OnInit{
  @ViewChild(EditProfileComponent) editProfile!: ElementRef;
  @ViewChild(OrdersComponent) ordersComponent!: ElementRef;
  @ViewChild(WorksComponent) worksComponent!:ElementRef;
  isSidebarActive: boolean = false;
  currentContent: number = 0;

  ngOnInit() {
    this.setFullHeight();
  }

  constructor(protected authService: AuthenticationService) {
  }


  changeContent(currentContent: number){
   this.currentContent = currentContent;
  }


  toggleSidebar() {
    this.isSidebarActive = !this.isSidebarActive;
  }

  setFullHeight() {
    const sidebar = document.getElementById('sidebar');
    if (sidebar) {
      sidebar.style.height = `${window.innerHeight}px`;
    }
  }

  @HostListener('window:resize', ['$event'])
  onResize() {
    this.setFullHeight();
  }

}
