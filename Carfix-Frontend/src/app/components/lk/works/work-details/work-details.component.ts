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
  }

  saveEditedWork(id: string) {
      this.workService.updateWork(id,this.editWork.value).pipe(
        catchError(err => {
          console.error('Произошла ошибка при обновлении услуги',err);
          return of(null);
        })
      ).subscribe(response =>{
        this.bsModalRef.hide();
      })
  }


}
