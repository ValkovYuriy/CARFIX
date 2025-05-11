import {ChangeDetectorRef, Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import Choices from 'choices.js';
import {Work} from '../../../model/Work';
import {WorkService} from '../../../services/WorkService/work.service';
import {catchError, of} from 'rxjs';
import {ApiResponse} from '../../../model/ApiResponse';
import {
  AbstractControl,
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule, ValidationErrors,
  ValidatorFn,
  Validators
} from '@angular/forms';
import {NgIf, NgOptimizedImage} from '@angular/common';
import {
  MatTimepicker,
  MatTimepickerInput,
  MatTimepickerModule,
  MatTimepickerToggle
} from '@angular/material/timepicker';
import {MatFormField, MatHint, MatInput, MatInputModule, MatLabel} from '@angular/material/input';
import {
  MatDatepicker,
  MatDatepickerInput, MatDatepickerInputEvent,
  MatDatepickerModule,
  MatDatepickerToggle
} from '@angular/material/datepicker';
import {MatIcon, MatIconModule} from '@angular/material/icon';
import {NgxMaterialTimepickerModule} from 'ngx-material-timepicker';
import {ServiceDataForm} from '../../../form-groups/data-forms';
import {MatNativeDateModule} from '@angular/material/core';
import {MatFormFieldModule} from '@angular/material/form-field';
import {DateTime} from 'luxon'
import {OrderService} from '../../../services/OrderService/order.service';
import {NgxMatDatepickerInput, NgxMatDatepickerInputEvent, NgxMatDatetimepicker} from '@ngxmc/datetime-picker';
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
    NgOptimizedImage,
    MatDatepickerModule,
    MatInputModule,
    MatFormFieldModule,
    MatIconModule,
    MatNativeDateModule,
    ReactiveFormsModule,
    NgxMaterialTimepickerModule,
    MatTimepickerInput,
    MatTimepicker,
    MatTimepickerToggle
  ],
  templateUrl: './service-data.component.html',
  styleUrl: './service-data.component.css'
})
export class ServiceDataComponent implements OnInit {

  works: Work[] | null = null;
 // Выбранное время
  isDisabled: boolean = false; // Флаг для управления состоянием disabled
  selectedWorks: string[] = [];

  serviceData: { works: Work[], description: string, orderDate: Date } = {
    works: [],
    description: '',
    orderDate: new Date()
  };
  @ViewChild('descriptionInput') textarea!: ElementRef<HTMLTextAreaElement>;
  @ViewChild('microIcon') microIcon!: ElementRef<HTMLElement>;
  @ViewChild(NgxMatDatetimepicker) picker!: NgxMatDatetimepicker<any>;
  transcript = '';
  isRecording = false;
  recognition: any;
  interimTranscript = '';
  selectedDate: Date = new Date();
  datetimeForm: FormGroup = new FormGroup({
    date:  new FormControl(this.selectedDate,[Validators.required]),
    time:  new FormControl('',[Validators.required,this.timeValidator(() => this.busyTimes,() => this.selectedDate)])
  });
  minDate = new Date();
  maxDate = new Date();
  busyTimes: Date[] = [];



  constructor(private orderService: OrderService,private workService: WorkService,private cdr: ChangeDetectorRef) {
    NgxMaterialTimepickerModule.setOpts('ru-RU');
    this.maxDate.setMonth(this.minDate.getMonth() + 1);
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
      this.cdr.detectChanges();
    };

    this.recognition.onerror = (event: any) => {
      console.error('Ошибка распознавания:', event.error);
      this.stopRecording();
    };
  }


  ngOnInit() {
    this.initializeChoices();
    this.getBusyTimes();
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
      this.workService.getWorks();
      this.workService.works().forEach(function (service) {
              const option = document.createElement('option');
              option.value = service.name;
              option.textContent = service.name;
              serviceElement.appendChild(option);
      });
      choices.setChoices(
        this.workService.works().map(work => ({
          value: work.name,
          label: work.name,
        })),
        'value',
        'label',
        true
      );
      this.works = this.workService.works();
    }
  }

  getBusyTimes(){
    this.orderService.findAllOrderDates().pipe(
      catchError((err) => {
        console.error('Произошла ошибка при загрузке дат заказов',err);
        return of([])
      })
    ).subscribe(response => {
      this.busyTimes = response.data.map((dateString: string) => new Date(dateString));
      console.log(this.busyTimes);
    })
  }

  getServiceData() {
    this.serviceData.works = this.getSelectedWorks();
    this.serviceData.description = this.textarea.nativeElement.value;
    return this.serviceData;
  }


  getSelectedWorks(): Work[] {
    return this.works ? this.works.filter(work => this.selectedWorks.includes(work.name)) : [];
  }

  isTimeAllowed(dateTime: Date): boolean {
    const hours = dateTime.getHours();
    const minutes = dateTime.getMinutes();

    if (!(hours >= 8 && hours <= 18 && minutes % 30 === 0)) {
      return false;
    }

    const isBusy = this.busyTimes.some(busyTime => {
      return (
        busyTime.getFullYear() === dateTime.getFullYear() &&
        busyTime.getMonth() === dateTime.getMonth() &&
        busyTime.getDate() === dateTime.getDate() &&
        busyTime.getHours() === dateTime.getHours() &&
        busyTime.getMinutes() === dateTime.getMinutes()
      );
    });

    return !isBusy;
  }

  dateFilter = (date: Date | null): boolean => {
    const day = date?.getDay();
    return day !== 0 && day !== 6;
  };

  onDateChange(type: string, event: MatDatepickerInputEvent<any, any>) {
    this.selectedDate = event.value;
    this.datetimeForm.get('time')?.updateValueAndValidity();
  }

  timeValidator(getBusyTimes: () => Date[], getSelectedDate: () => Date): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const selectedTime = control.value;
      if (!selectedTime) {
        return null;
      }

      const busyTimes = getBusyTimes();
      const selectedDate = getSelectedDate();
      if (!selectedDate) {
        return null;
      }

      const hours = selectedTime.getHours();
      const minutes = selectedTime.getMinutes();
      const constDate = new Date(selectedDate);
      constDate.setHours(hours, minutes, 0, 0);

      const isBusy = busyTimes.some(busyTime => {
        return (
          busyTime.getFullYear() === constDate.getFullYear() &&
          busyTime.getMonth() === constDate.getMonth() &&
          busyTime.getDate() === constDate.getDate() &&
          busyTime.getHours() === constDate.getHours() &&
          busyTime.getMinutes() === constDate.getMinutes()
        );
      });

      return isBusy ? { timeConflict: true } : null;
    };
  }
}
