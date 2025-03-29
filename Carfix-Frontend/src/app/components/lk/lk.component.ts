import {Component, HostListener, OnInit} from '@angular/core';
import {NgClass} from '@angular/common';

@Component({
  selector: 'app-lk',
  standalone: true,
  imports: [
    NgClass
  ],
  templateUrl: './lk.component.html',
  styleUrl: './lk.component.css'
})
export class LkComponent implements OnInit{
  isSidebarActive: boolean = false;

  ngOnInit() {
    this.setFullHeight();
  }

  // Метод для переключения активности сайдбара
  toggleSidebar() {
    this.isSidebarActive = !this.isSidebarActive;
  }

  // Устанавливаем высоту сайдбара
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
