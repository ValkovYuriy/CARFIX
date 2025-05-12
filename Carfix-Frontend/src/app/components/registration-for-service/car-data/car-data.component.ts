import {Component, model, OnInit} from '@angular/core';
import {Mark} from '../../../model/Mark';
import {catchError, of} from 'rxjs';
import {ApiResponse} from '../../../model/ApiResponse';
import {MarkService} from '../../../services/MarkService/mark.service';
import {Model} from '../../../model/Model';
import {NgForOf} from '@angular/common';
import {FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {Car} from '../../../model/Car';
import {User} from '../../../model/User';
import {CarDataForm} from '../../../form-groups/data-forms';
import {CarService} from '../../../services/CarService/car.service';

@Component({
  selector: 'app-car-data',
  standalone: true,
  imports: [
    NgForOf,
    FormsModule,
    ReactiveFormsModule
  ],
  templateUrl: './car-data.component.html',
  styleUrl: './car-data.component.css'
})
export class CarDataComponent implements OnInit{

  car: { vinNumber: string; modelDto: Model | null; id: string | null; govNumber: string } = {
    id: null,
    govNumber: '',
    vinNumber: '',
    modelDto: null
  };
  marks: Mark[] = [];

  carDataForm: FormGroup = CarDataForm.create();
  selectedBrand: string | null = null;

  models: { value: string; label: string }[] = [];

  constructor(private markService: MarkService, private carService: CarService) {}

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

  checkVin() {
    const vin = this.carDataForm.get('vin')?.value;
    if (vin && vin.length === 17) {
      this.carService.getCarByVin(vin).subscribe(response => {
        const carData = response.data;
        if (carData) {
          this.carDataForm.patchValue({
            gov: carData.govNumber,
            mark: carData.modelDto?.mark?.markName,
            model: carData.modelDto?.modelName
          });
          const selectedMark = this.marks.find(mark => mark.markName === carData.modelDto.mark.markName);
          if (selectedMark && selectedMark.models) {
            this.models = selectedMark.models.map(model => ({
              value: model.modelName,
              label: model.modelName
            }));
          }
          this.carDataForm.get('gov')?.disable();
          this.carDataForm.get('mark')?.disable();
          this.carDataForm.get('model')?.disable();
        } else {
          this.carDataForm.get('gov')?.enable();
          this.carDataForm.get('mark')?.enable();
          this.carDataForm.get('model')?.enable();
        }
      });
    }
  }

  onBrandChange(event: Event) {
    const target = event.target as HTMLSelectElement;
    this.selectedBrand = target.value;
    this.models = [];

    if (this.selectedBrand) {
      const selectedMark = this.marks.find(mark => mark.markName === this.selectedBrand);
      if (selectedMark && selectedMark.models) {
        this.models = selectedMark.models.map(model => ({
          value: model.modelName,
          label: model.modelName
        }));
      }
      this.carDataForm.get('model')?.enable();
    }else{
      this.carDataForm.get('model')?.disable();
    }
  }

  createModelFromForm(): Model {
    const markName = this.carDataForm.get('mark')?.value;
    const modelName = this.carDataForm.get('model')?.value;

    if (!markName || !modelName) {
      throw new Error('Марка или модель не выбраны');
    }

    const mark: Mark = {
      id: null,
      markName: markName,
      models: []
    };

    const model: Model = {
      id: null,
      modelName: modelName,
      mark: mark
    };

    return model;
  }

  getCarData(): Car | null {
    if(this.carDataForm.valid){
      this.car.vinNumber = this.carDataForm.get('vin')?.value;
      this.car.govNumber = this.carDataForm.get('gov')?.value;
      this.car.modelDto = this.createModelFromForm();
      return <Car>this.car;
    }
    return null;
  }

  protected readonly HTMLSelectElement = HTMLSelectElement;
}
