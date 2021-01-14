import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ByUserReworkFromOthersComponent } from './by-user-rework-from-others.component';

describe('ByUserReworkFromOthersComponent', () => {
  let component: ByUserReworkFromOthersComponent;
  let fixture: ComponentFixture<ByUserReworkFromOthersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ByUserReworkFromOthersComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ByUserReworkFromOthersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
