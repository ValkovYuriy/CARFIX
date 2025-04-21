import {Component, Input, OnInit} from '@angular/core';
import {Work} from '../../../../model/Work';
import {BsModalRef} from 'ngx-bootstrap/modal';
import {FormControl, FormGroup, ReactiveFormsModule} from '@angular/forms';
import {WorkService} from '../../../../services/WorkService/work.service';
import {catchError, of} from 'rxjs';

@Component({
  selector: 'app-work-details',
  standalone: true,
  imports: [
    ReactiveFormsModule
  ],
  templateUrl: './work-details.component.html',
  styleUrl: './work-details.component.css'
})
export class WorkDetailsComponent implements OnInit{

  @Input() work: Work | undefined;

  isEditMode: boolean = false;
  editWork: FormGroup = new FormGroup({
    name: new FormControl(),
    description: new FormControl(),
    workPrice: new FormControl(),
    imageUrl: new FormControl(),
  });

  constructor(public bsModalRef: BsModalRef, private workService:WorkService) {
  }

  ngOnInit() {
    this.editWork.patchValue(
      {
        name: this.work?.name,
        description: this.work?.description,
        workPrice: this.work?.workPrice,
        imageUrl: this.work?.imageUrl
      },
    );
    this.isEditMode = !!this.work?.id;
  }

  saveEditedWork(id: string) {
    if (this.isEditMode){
      this.workService.updateWork(id,this.editWork.value).pipe(
        catchError(err => {
          console.error('Произошла ошибка при обновлении услуги',err);
          alert('Произошла ошибка при обновлении услуги')
          return of(null);
        })
      ).subscribe(response =>{
        this.bsModalRef.hide();
        this.work!.name = this.editWork.get('name')?.value;
        this.work!.description = this.editWork.get('description')?.value;
        this.work!.workPrice = this.editWork.get('workPrice')?.value;
        this.work!.imageUrl = this.editWork.get('imageUrl')?.value;
        this.workService.updateWorkInSignal(this.work!);
      })
    }else{
      this.workService.createWork(this.editWork.value).pipe(
        catchError(err => {
          console.error('Произошла ошибка при добавлении услуги',err);
          alert('Произошла ошибка при добавлении услуги');
          return of(null);
        })
      ).subscribe(response =>{
        this.bsModalRef.hide();
        this.work!.name = this.editWork.get('name')?.value;
        this.work!.description = this.editWork.get('description')?.value;
        this.work!.workPrice = this.editWork.get('workPrice')?.value;
        this.work!.imageUrl = this.editWork.get('imageUrl')?.value;
        this.workService.addWorkToSignal(this.work!);
      })
    }

  }


}
