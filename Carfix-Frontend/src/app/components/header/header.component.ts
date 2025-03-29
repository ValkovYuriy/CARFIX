import {Component, Input, OnInit} from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import {AppComponent} from '../../app.component';
import {AuthenticationService} from '../../services/AuthenticationService/authentication.service';
import {MatAutocomplete, MatAutocompleteTrigger, MatOption} from '@angular/material/autocomplete';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {async, map, Observable, of, startWith} from 'rxjs';
import {AsyncPipe, NgOptimizedImage} from '@angular/common';
import {MatFormField} from '@angular/material/form-field';
import {WorkService} from '../../services/WorkService/work.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    RouterLink,
    MatAutocompleteTrigger,
    ReactiveFormsModule,
    AsyncPipe,
    MatAutocomplete,
    MatOption,
    MatOption,
    NgOptimizedImage
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent implements OnInit{
  @Input() decodedToken: any = null;
  flag = true;
  control = new FormControl('');
  services: string[] = [];
  filteredServices: Observable<string[]> = of([]);


  constructor(private router: Router, private mainComponent: AppComponent, protected authService: AuthenticationService, private workService: WorkService) {}

  ngOnInit() {
    this.workService.searchData$.subscribe((services) => {
      this.services = services; // Получаем данные и сохраняем их в компоненте
      this.filteredServices = this.control.valueChanges.pipe(
        startWith(''),
        map(value => this._filter(value || '')),
      );
    });
  }

  private _filter(value: string): string[] {
    const filterValue = this._normalizeValue(value);
    return this.services.filter(service => this._normalizeValue(service).includes(filterValue));
  }

  private _normalizeValue(value: string): string {
    return value.toLowerCase().replace(/\s/g, '');
  }


  protected readonly async = async;
}
