import {Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
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
export class ServiceDataComponent implements OnInit,OnDestroy {

  works: Work[] | null = null;

  selectedWorks: string[] = [];

  serviceData: { works: Work[], description: string, orderDate: Date } = {
    works: [],
    description: '',
    orderDate: new Date()
  };
  @ViewChild('descriptionInput') textarea!: ElementRef<HTMLTextAreaElement>;
  @ViewChild('microIcon') microIcon!: ElementRef<HTMLElement>;
  recognition: any;
  isListening = false;


  constructor(private workService: WorkService) {
    this.initRecognition();
  }

  initRecognition() {
    const SpeechRecognition = (window as any).SpeechRecognition ||
      (window as any).webkitSpeechRecognition;

    if (!SpeechRecognition) {
      console.error('Браузер не поддерживает Web Speech API');
      return;
    }

    this.recognition = new SpeechRecognition();
    this.recognition.continuous = true;
    this.recognition.interimResults = true;
    this.recognition.lang = 'ru-RU';

    this.recognition.onstart = () => {
      console.log('Распознавание начато');
      this.isListening = true;
    };

    this.recognition.onresult = (event: any) => {
      let interim = '';
      let final = '';

      for (let i = 0; i < event.results.length; i++) {
        // Добавляем проверку на существование результата
        if (!event?.results[i] || !event?.results[i][0]) {
          continue;
        }

        const transcript = event?.results[i][0].transcript;
        if (event?.results[i].isFinal) {
          final += transcript + ' '; // Добавляем пробел между фразами
        } else {
          interim += transcript;
        }
      }

      // Обновляем текст, отдавая приоритет финальным результатам
      this.serviceData.description = final.trim() || interim;
    };
    this.recognition.onerror = (event: any) => {
      console.error('Ошибка распознавания:', event.error);
      this.isListening = false;
    };

    this.recognition.onend = () => {
      console.log('Распознавание завершено');
      this.isListening = false;
    };

  }



  toggleVoiceRecognition() {
    if (this.isListening) {
      this.stopRecognition();
    } else {
      this.startRecognition();
    }
  }

  startRecognition() {
    if (!this.recognition) {
      this.initRecognition();
    }

    try {
      this.recognition.start();
      console.log('Попытка запуска распознавания');
    } catch (e) {
      console.error('Ошибка при запуске:', e);
      // Попробуем снова через небольшой интервал
      setTimeout(() => this.recognition.start(), 100);
    }
  }

  stopRecognition() {
    if (this.recognition) {
      this.recognition.stop();
    }
    this.isListening = false;
  }

  ngOnDestroy() {
    this.stopRecognition();
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
          console.error("Ошибка при загрузке услуг", err);
          return of({message: 'Ошибка', data: []} as ApiResponse<Work[]>);
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

  getServiceData() {
    this.serviceData.works = this.getSelectedWorks();
    return this.serviceData;
  }

  getSelectedWorks(): Work[] {
    return this.works ? this.works.filter(work => this.selectedWorks.includes(work.name)) : [];
  }


}
