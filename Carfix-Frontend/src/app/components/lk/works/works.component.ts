import {Component, OnInit, signal} from '@angular/core';
import {Work} from '../../../model/Work';
import {catchError, of} from 'rxjs';
import {WorkService} from '../../../services/WorkService/work.service';
import {BsModalRef, BsModalService} from 'ngx-bootstrap/modal';
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
  // Настройки пагинации
  rowsPerPage = 5;
  currentPage = signal(1);
  constructor(protected workService: WorkService,private modalService: BsModalService) {
  }

  ngOnInit() {
    this.loadWorks();
  }

  getPaginatedData() {
    const start = (this.currentPage() - 1) * this.rowsPerPage;
    const end = this.currentPage() * this.rowsPerPage;
    return this.workService.works().slice(start, end);
  }


  nextPage(): void {
    if (this.currentPage() < Math.ceil(this.workService.works().length / this.rowsPerPage)) {
      this.currentPage.update(page => page + 1);
    }
  }

  prevPage(): void {
    if (this.currentPage() > 1) {
      this.currentPage.update(page => page - 1);
    }
  }


  getTotalPages(): number {
    return Math.ceil(this.workService.works().length / this.rowsPerPage);
  }


  loadWorks(){
    this.workService.getWorks();
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
