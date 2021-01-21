import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UserMappingsComponent } from './user-mappings.component';

describe('UserMappingsComponent', () => {
  let component: UserMappingsComponent;
  let fixture: ComponentFixture<UserMappingsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UserMappingsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserMappingsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
