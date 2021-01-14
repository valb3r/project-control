import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TotalLinesCountComponent } from './total-lines-count.component';

describe('TotalOwnershipComponent', () => {
  let component: TotalLinesCountComponent;
  let fixture: ComponentFixture<TotalLinesCountComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TotalLinesCountComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TotalLinesCountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
