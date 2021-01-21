import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ByUserReworkByOthersComponent } from './by-user-rework-by-others.component';

describe('ByUserReworkByOthersComponent', () => {
  let component: ByUserReworkByOthersComponent;
  let fixture: ComponentFixture<ByUserReworkByOthersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ByUserReworkByOthersComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ByUserReworkByOthersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
