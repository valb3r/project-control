import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ByUserWorkDetailsComponent } from './by-user-work-details.component';

describe('ByUserWorkDetailsComponent', () => {
  let component: ByUserWorkDetailsComponent;
  let fixture: ComponentFixture<ByUserWorkDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ByUserWorkDetailsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ByUserWorkDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
