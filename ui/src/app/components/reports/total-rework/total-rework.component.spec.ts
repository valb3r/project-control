import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TotalReworkComponent } from './total-rework.component';

describe('TotalReworkComponent', () => {
  let component: TotalReworkComponent;
  let fixture: ComponentFixture<TotalReworkComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TotalReworkComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TotalReworkComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
