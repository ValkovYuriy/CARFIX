import {Component, OnInit} from '@angular/core';
import Choices from 'choices.js';
import {Work} from '../../../model/Work';
import {WorkService} from '../../../services/WorkService/work.service';
import {catchError, of} from 'rxjs';
import {ApiResponse} from '../../../model/ApiResponse';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-service-data',
  standalone: true,
  imports: [
    FormsModule
  ],
  templateUrl: './service-data.component.html',
  styleUrl: './service-data.component.css'
})
export class ServiceDataComponent implements OnInit{

  works: Work[] | null = null;

  selectedWorks: string[] = [];

  serviceData : {works: Work[], description: string, orderDate: Date} = {works: [], description: '', orderDate: new Date()};

  constructor(private workService: WorkService) {
  }

  ngOnInit() {
    this.initializeChoices();
  }

  initializeChoices(): void {
    const serviceElement = document.getElementById('service');
    if (serviceElement) {
      const choices = new Choices(serviceElement, {
        removeItemButton: true, // Включает кнопку удаления выбранного элемента
        searchEnabled: true, // Включает поиск
        placeholder: true, // Включает плейсхолдер
        placeholderValue: 'Выберете услугу...',
        noResultsText: 'Результаты не найдены',
        itemSelectText: 'Добавить'
      });
      this.workService.getWorks().pipe(
        catchError(err => {
          console.error("Ошибка при загрузке услуг",err);
          return of({ message: 'Ошибка', data: [] } as ApiResponse<Work[]>);
        })
      ).subscribe(
        response => {
          this.works = response.data;
          this.works.forEach(function (service) {
            const option = document.createElement('option');
            option.value = service.name;
            option.textContent = service.name;
            serviceElement.appendChild(option);
          });
          choices.setChoices(
            this.works.map(work => ({
              value: work.name,
              label: work.name,
            })),
            'value',
            'label',
            true
          );
        }
      );
    }
  }

  getServiceData(){
    this.serviceData.works = this.getSelectedWorks();
    return this.serviceData;
  }

  getSelectedWorks(): Work[] {
    return this.works ? this.works.filter(work => this.selectedWorks.includes(work.name)) : [];
  }

}
