import {Component, Input, OnInit} from '@angular/core';
import {Work} from '../../../../model/Work';
import {BsModalRef} from 'ngx-bootstrap/modal';
import {FormControl, FormGroup, ReactiveFormsModule} from '@angular/forms';
import {WorkService} from '../../../../services/WorkService/work.service';
import {catchError, of} from 'rxjs';
import {FileUpload, FileUploadEvent, UploadEvent} from 'primeng/fileupload';
import {Toast} from 'primeng/toast';
import {NgForOf, NgIf} from '@angular/common';
import {MessageService} from 'primeng/api';
import {WorkDataForm} from '../../../../form-groups/data-forms';

@Component({
  selector: 'app-work-details',
  standalone: true,
  providers: [MessageService],
  imports: [
    ReactiveFormsModule,
    FileUpload,
    Toast
  ],
  templateUrl: './work-details.component.html',
  styleUrl: './work-details.component.css'
})
export class WorkDetailsComponent implements OnInit{

  @Input() work: Work | undefined;

  isEditMode: boolean = false;
  editWork: FormGroup = WorkDataForm.create();

  constructor(public bsModalRef: BsModalRef, private workService:WorkService,private messageService: MessageService) {
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

  onUpload(event: FileUploadEvent) {
    // for(let file of event.files) {
    //   this.uploadedFiles.push(file);
    // }
    // this.messageService.add({severity: 'info', summary: 'File Uploaded', detail: ''});
    const file = event.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = () => {
        const base64String = reader.result as string;
        this.work!.imageBase64 = base64String.split(',')[1];
      };
      reader.readAsDataURL(file);
    }
  }
}
