import {Component, OnInit, signal} from '@angular/core';
import {Work} from '../../../model/Work';
import {catchError, of} from 'rxjs';
import {ApiResponse} from '../../../model/ApiResponse';
import {WorkService} from '../../../services/WorkService/work.service';
import {DatePipe} from '@angular/common';
import {BsModalRef, BsModalService} from 'ngx-bootstrap/modal';
import {OrderDetailsComponent} from '../orders/order-details/order-details.component';
import {WorkDetailsComponent} from './work-details/work-details.component';

@Component({
  selector: 'app-works',
  standalone: true,
  imports: [],
  templateUrl: './works.component.html',
  styleUrl: './works.component.css'
})
export class WorksComponent implements OnInit{

  bsModalRef: BsModalRef | undefined;
  works= signal<Work[]>([]);

  ngOnInit() {
    this.loadWorks();
  }

  constructor(private workService: WorkService,private modalService: BsModalService) {
  }

  loadWorks(){
    this.workService.getWorks().pipe(
      catchError(err => {
        console.error("Ошибка при загрузке услуг",err);
        return of({ message: 'Ошибка', data: [] } as ApiResponse<Work[]>);
      })
    ).subscribe(
      response => {
        this.works.set(response.data)
      }
    );
  }

  showWorkDetails(work: Work){
    const initialState = {
      work: work
    }
    this.bsModalRef = this.modalService.show(WorkDetailsComponent,{initialState,class: 'modal-dialog-centered'} as any);
  }

  deleteWork(id: string){
    this.workService.deleteWork(id).pipe(
      catchError(err => {
        console.error('Произошла ошибка при удалении услуги',err);
        return of(null);
      })
    ).subscribe(response =>{
      console.log('Услуга успешно удалена')
    })
  }
}
