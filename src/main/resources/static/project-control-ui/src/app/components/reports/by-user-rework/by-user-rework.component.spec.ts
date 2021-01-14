import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ByUserReworkComponent } from './by-user-rework.component';

describe('ByUserReworkComponent', () => {
  let component: ByUserReworkComponent;
  let fixture: ComponentFixture<ByUserReworkComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ByUserReworkComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ByUserReworkComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
