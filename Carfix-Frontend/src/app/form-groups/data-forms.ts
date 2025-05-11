import {AbstractControl, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators} from '@angular/forms';

export class PersonalDataForm {
  static create(): FormGroup {
    return new FormGroup({
      email: new FormControl('', [
        Validators.email,
        Validators.minLength(5),
        Validators.maxLength(50)
      ]),
      phoneNumber: new FormControl('', [
        Validators.pattern(/^\+?[0-9]{10,15}$/)
      ]),
      firstName: new FormControl('', [
        Validators.minLength(2),
        Validators.maxLength(100)
      ]),
      lastName: new FormControl('', [
        Validators.minLength(2),
        Validators.maxLength(100)
      ])
    });
  }
}

export class CarDataForm{
  static create(): FormGroup{
    return new FormGroup({
      vin: new FormControl('',[Validators.minLength(17),Validators.maxLength(17)]),
      gos: new FormControl('',[Validators.minLength(6),Validators.maxLength(6)]),
      mark: new FormControl('',Validators.required),
      model: new FormControl('',Validators.required)
    })
  }
}

export class ServiceDataForm{
  static create(): FormGroup{
    return new FormGroup({
      description: new FormControl(''),
      works: new FormControl('')
    })
  }
}


