import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ByUserReworkOwnComponent } from './by-user-rework-own.component';

describe('ByUserReworkOwnComponent', () => {
  let component: ByUserReworkOwnComponent;
  let fixture: ComponentFixture<ByUserReworkOwnComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ByUserReworkOwnComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ByUserReworkOwnComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
