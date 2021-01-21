import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TotalChurnComponent } from './total-churn.component';

describe('TotalChurnComponent', () => {
  let component: TotalChurnComponent;
  let fixture: ComponentFixture<TotalChurnComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TotalChurnComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TotalChurnComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
