import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ByUserCommitsComponent } from './by-user-commits.component';

describe('ByUserCommitsComponent', () => {
  let component: ByUserCommitsComponent;
  let fixture: ComponentFixture<ByUserCommitsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ByUserCommitsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ByUserCommitsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
