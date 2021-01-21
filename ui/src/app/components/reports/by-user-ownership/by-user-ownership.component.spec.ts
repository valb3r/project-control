import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ByUserOwnershipComponent } from './by-user-ownership.component';

describe('ByUserOwnershipComponent', () => {
  let component: ByUserOwnershipComponent;
  let fixture: ComponentFixture<ByUserOwnershipComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ByUserOwnershipComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ByUserOwnershipComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
