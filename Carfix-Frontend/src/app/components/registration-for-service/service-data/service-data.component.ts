import {ChangeDetectorRef, Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import Choices from 'choices.js';
import {Work} from '../../../model/Work';
import {WorkService} from '../../../services/WorkService/work.service';
import {catchError, of} from 'rxjs';
import {ApiResponse} from '../../../model/ApiResponse';
import {FormsModule} from '@angular/forms';
import {NgIf, NgOptimizedImage} from '@angular/common';
declare global {
  interface Window {
    webkitSpeechRecognition: any;
    SpeechRecognition: any;
  }
}
@Component({
  selector: 'app-service-data',
  standalone: true,
  imports: [
    FormsModule,
    NgIf,
    NgOptimizedImage
  ],
  templateUrl: './service-data.component.html',
  styleUrl: './service-data.component.css'
})
export class ServiceDataComponent implements OnInit {

  works: Work[] | null = null;

  selectedWorks: string[] = [];

  serviceData: { works: Work[], description: string, orderDate: Date } = {
    works: [],
    description: '',
    orderDate: new Date()
  };
  @ViewChild('descriptionInput') textarea!: ElementRef<HTMLTextAreaElement>;
  @ViewChild('microIcon') microIcon!: ElementRef<HTMLElement>;
  transcript = '';
  isRecording = false;
  recognition: any;
  interimTranscript = '';


  constructor(private workService: WorkService,private cdr: ChangeDetectorRef) {
    const SpeechRecognition = window['SpeechRecognition'] || window['webkitSpeechRecognition'];
    this.recognition = new SpeechRecognition();
    this.recognition.lang = 'ru-RU';
    this.recognition.interimResults = true;
    this.recognition.continuous = true;

    this.recognition.onresult = (event: any) => {
      let interim = '';
      for (let i = event.resultIndex; i < event.results.length; i++) {
        const transcript = event?.results[i][0].transcript;
        console.log(transcript);
        if (event?.results[i].isFinal) {
          this.transcript += transcript + ' ';
        } else {
          interim += transcript;
        }
      }
      this.interimTranscript = interim;
      this.cdr.detectChanges(); // Принудительное обновление интерфейса
    };

    this.recognition.onerror = (event: any) => {
      console.error('Ошибка распознавания:', event.error);
      this.stopRecording();
    };
  }



  ngOnInit() {
    this.initializeChoices();
  }

  toggleRecording() {
    this.isRecording = !this.isRecording;
    if (this.isRecording) {
      this.startRecording();
    } else {
      this.stopRecording();
    }
  }

  startRecording() {
    this.recognition.start();
  }

  stopRecording() {
    this.recognition.stop();
    this.isRecording = false;
  }

  clearText() {
    this.transcript = '';
    this.interimTranscript = '';
  }

  get combinedText(): string {
    return this.transcript + this.interimTranscript;
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
    this.serviceData.description = this.textarea.nativeElement.value;
    return this.serviceData;
  }

  getSelectedWorks(): Work[] {
    return this.works ? this.works.filter(work => this.selectedWorks.includes(work.name)) : [];
  }

}
