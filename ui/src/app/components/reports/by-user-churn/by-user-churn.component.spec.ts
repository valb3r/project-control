import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ByUserChurnComponent } from './by-user-churn.component';

describe('ByUserChurnComponent', () => {
  let component: ByUserChurnComponent;
  let fixture: ComponentFixture<ByUserChurnComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ByUserChurnComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ByUserChurnComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
