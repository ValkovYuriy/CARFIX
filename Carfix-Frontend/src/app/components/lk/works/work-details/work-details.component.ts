import {Component, Input, NO_ERRORS_SCHEMA, OnInit} from '@angular/core';
import {Work} from '../../../../model/Work';
import {BsModalRef} from 'ngx-bootstrap/modal';
import {FormGroup, ReactiveFormsModule} from '@angular/forms';
import {WorkService} from '../../../../services/WorkService/work.service';
import {catchError, of} from 'rxjs';
import {MessageService} from 'primeng/api';
import {WorkDataForm} from '../../../../form-groups/data-forms';

@Component({
  selector: 'app-work-details',
  schemas: [NO_ERRORS_SCHEMA],
  standalone: true,
  providers: [MessageService],
  imports: [
    ReactiveFormsModule
  ],
  templateUrl: './work-details.component.html',
  styleUrl: './work-details.component.css'
})
export class WorkDetailsComponent implements OnInit{

  @Input() work: Work | undefined;

  isEditMode: boolean = false;
  editWork: FormGroup = WorkDataForm.create();

  constructor(public bsModalRef: BsModalRef, private workService:WorkService, private messageService: MessageService) {
  }

  ngOnInit() {
    this.editWork.patchValue(
      {
        name: this.work?.name,
        description: this.work?.description,
        workPrice: this.work?.workPrice
      },
    );
    this.isEditMode = !!this.work?.id;
  }

  saveEditedWork(id: string) {
    if (this.isEditMode){
      if (this.editWork.valid && this.work?.imageBase64) {
        this.work.workPrice = this.editWork.get('workPrice')?.value;
        this.work.name = this.editWork.get('name')?.value;
        this.work.description = this.editWork.get('description')?.value;
        this.workService.updateWork(id, this.work).pipe(
          catchError(err => {
            console.error('Произошла ошибка при обновлении услуги', err);
            alert('Произошла ошибка при обновлении услуги')
            return of(null);
          })
        ).subscribe(response => {
          if (response) {
            this.bsModalRef.hide();
            this.workService.updateWorkInSignal(this.work!);
          }
        })
      }
    }else{
      if (this.editWork.valid && this.work?.imageBase64) {
        this.work.workPrice = this.editWork.get('workPrice')?.value;
        this.work.name = this.editWork.get('name')?.value;
        this.work.description = this.editWork.get('description')?.value;
        this.workService.createWork(this.work).pipe(
          catchError(err => {
            console.error('Произошла ошибка при добавлении услуги', err);
            alert('Произошла ошибка при добавлении услуги');
            return of(null);
          })
        ).subscribe(response => {
          if(response){
            this.bsModalRef.hide();
            this.workService.addWorkToSignal(this.work!);
          }
        })
      }
    }

  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    this.readFile(file);
  }

  private readFile(file: File) {
    if (!this.work) return;

    // простая валидация
    if (!file.type.startsWith('image/')) {
      this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: 'Выберите изображение' });
      return;
    }
    if (file.size > 10 * 1024 * 1024) {
      this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: 'Файл больше 10 МБ' });
      return;
    }

    const reader = new FileReader();
    reader.onload = () => {
      this.work!.imageBase64 = (reader.result as string).split(',')[1];
    };
    reader.onerror = () => {
      this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: 'Не удалось прочитать файл' });
    };
    reader.readAsDataURL(file);
  }

  clearImage() {
    if (this.work) {
      this.work.imageBase64 = '';
    }
  }

  isDragging = false;

  onDragOver(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = true;
  }

  onDragLeave(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = false;
  }

  onDrop(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = false;

    const file = event.dataTransfer?.files?.[0];
    if (file) this.readFile(file);
  }
}
