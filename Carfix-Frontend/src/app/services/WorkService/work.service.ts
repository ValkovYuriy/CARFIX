import {Injectable, signal, WritableSignal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Work} from '../../model/Work';
import {ApiResponse} from '../../model/ApiResponse';
import {BehaviorSubject, catchError, of, tap} from 'rxjs';
import {environment} from '../../../environments/environment';
import {PopularWork} from '../../model/PopularWork';

@Injectable({
  providedIn: 'root'
})
export class WorkService {

  private baseUrl = `${environment.baseUrl}/api`;
  works: WritableSignal<Work[]> = signal([]);
  popularWorks = signal<PopularWork[]>([]);
  private searchDataSubject = new BehaviorSubject<string[]>([]);
  searchData$ = this.searchDataSubject.asObservable();

  setSearchData(data: string[]) {
    this.searchDataSubject.next(data);
  }

  constructor(private httpClient: HttpClient) { }

  getWorks(){
    return this.httpClient.get<ApiResponse<Work[]>>(`${this.baseUrl}/works`).pipe(
      catchError(err => {
        console.error("Ошибка при загрузке услуг",err);
        return of({ message: 'Ошибка', data: [] } as ApiResponse<Work[]>);
      })
    ).subscribe(
      response => {
        this.setWorks(response.data);
      }
    );
  }

  getPopularWorks(){
    return this.httpClient.get<ApiResponse<PopularWork[]>>(`${this.baseUrl}/works/popular`).pipe(
      catchError(err => {
        console.error("Ошибка при загрузке услуг",err);
        return of({ message: 'Ошибка', data: [] } as ApiResponse<any>);
      })
    ).subscribe(
      response => {
        this.setPopularWorks(response.data)
      }
    );
  }

  createWork(work: Work){
    return this.httpClient.post<any>(`${this.baseUrl}/works`,work);
  }

  updateWork(id: string, work: Work){
    return this.httpClient.put<any>(`${this.baseUrl}/works/${id}`,work);
  }

  deleteWork(id: string){
    return this.httpClient.delete<any>(`${this.baseUrl}/works/${id}`);
  }

  addWorkToSignal(work: Work): void {
    this.works.update((currentWorks) => [...currentWorks, work]);
  }

  updateWorkInSignal(updatedWork: Work): void {
    this.works.update((currentWorks) =>
      currentWorks.map((work) => (work.id === updatedWork.id ? updatedWork : work))
    );
  }

  deleteWorkInSignal(workId: string): void {
    this.works.update((currentWorks) =>
      currentWorks.filter((work) => work.id !== workId)
    );
  }

  setWorks(works: Work[]){
    this.works.set(works);
  }

  setPopularWorks(popularWorks: PopularWork[]){
    this.popularWorks.set(popularWorks);
  }

}
