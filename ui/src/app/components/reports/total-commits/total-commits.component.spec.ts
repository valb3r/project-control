import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TotalCommitsComponent } from './total-commits.component';

describe('TotalCommitsComponent', () => {
  let component: TotalCommitsComponent;
  let fixture: ComponentFixture<TotalCommitsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TotalCommitsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TotalCommitsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
