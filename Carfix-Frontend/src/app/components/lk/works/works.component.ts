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
  // Настройки пагинации
  rowsPerPage = 5; // Количество строк на странице
  currentPage = signal(1); // Текущая страница (сигнал)

  constructor(private workService: WorkService,private modalService: BsModalService) {
  }

  ngOnInit() {
    this.loadWorks();
  }

  getPaginatedData() {
    const start = (this.currentPage() - 1) * this.rowsPerPage;
    const end = this.currentPage() * this.rowsPerPage;
    return this.works().slice(start, end);
  }


  nextPage(): void {
    if (this.currentPage() < Math.ceil(this.works().length / this.rowsPerPage)) {
      this.currentPage.update(page => page + 1);
    }
  }

  prevPage(): void {
    if (this.currentPage() > 1) {
      this.currentPage.update(page => page - 1);
    }
  }


  getTotalPages(): number {
    return Math.ceil(this.works().length / this.rowsPerPage);
  }


  loadWorks(){
    this.workService.getWorks().pipe(
      catchError(err => {
        console.error("Ошибка при загрузке услуг",err);
        return of({ message: 'Ошибка', data: [] } as ApiResponse<Work[]>);
      })
    ).subscribe(
      response => {
        this.workService.works.set(response.data);
        this.works = this.workService.works;
      }
    );
  }

  showWorkDetails(work: Work | null, isEditMode: boolean = false){
    const initialState = {
      work: work ?? {
        id: null,
        name: '',
        description: '',
        imageUrl: '',
        workPrice: 0
      },
      isEditMode: isEditMode
    };
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
      this.workService.deleteWorkInSignal(id);
    })
  }

}
