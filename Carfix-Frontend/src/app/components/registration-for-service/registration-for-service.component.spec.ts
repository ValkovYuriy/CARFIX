import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegistrationForServiceComponent } from './registration-for-service.component';

describe('RegistrationForServiceComponent', () => {
  let component: RegistrationForServiceComponent;
  let fixture: ComponentFixture<RegistrationForServiceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegistrationForServiceComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RegistrationForServiceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
