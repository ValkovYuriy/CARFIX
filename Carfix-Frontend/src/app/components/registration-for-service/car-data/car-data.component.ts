import {Component, model, OnInit} from '@angular/core';
import {Mark} from '../../../model/Mark';
import {catchError, of} from 'rxjs';
import {ApiResponse} from '../../../model/ApiResponse';
import {MarkService} from '../../../services/MarkService/mark.service';
import {Model} from '../../../model/Model';
import {NgForOf} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {Car} from '../../../model/Car';
import {User} from '../../../model/User';

@Component({
  selector: 'app-car-data',
  standalone: true,
  imports: [
    NgForOf,
    FormsModule
  ],
  templateUrl: './car-data.component.html',
  styleUrl: './car-data.component.css'
})
export class CarDataComponent implements OnInit{

  car: { vinNumber: string; modelDto: Model | null; id: string | null; govNumber: string }  = {
    id: null,
    govNumber: '',
    vinNumber: '',
    modelDto: null
  };
  marks: Mark[] = [];

  selectedBrand: string | null = null;
  selectedModel: string | null = null;

  models: { value: string; label: string }[] = [];

  constructor(private markService: MarkService) {}

  ngOnInit() {
    this.markService.getMarks().pipe(
      catchError(err => {
        console.error("Ошибка при загрузке марок",err);
        return of({message: 'Ошибка', data: []} as ApiResponse<Mark[]>);
      })
    ).subscribe(
      response => {
        this.marks = response.data.sort((a,b) => a.markName.localeCompare(b.markName));
      }
    )
  }

  onBrandChange(brand: string | null){
    this.selectedBrand = brand;
    this.models = []; // Очистка предыдущих моделей

    if (brand) {
      const selectedMark = this.marks.find(mark => mark.markName === brand);
      if (selectedMark && selectedMark.models) {
        this.models = selectedMark.models.map((model: Model) => ({
          value: model.modelName,
          label: model.modelName
        }));
      }
    } else {
      this.models = [
        { value: '', label: 'Сначала выберите марку...' }
      ];
    }
  }

  onModelChange(selectedModel: string | null){
    this.car.modelDto = this.marks
      .find(mark => mark.markName === this.selectedBrand)
      ?.models.find(model => model.modelName === selectedModel) as Model | null;
    }

  getCarData(): Car {

    return <Car>this.car;
  }
}
