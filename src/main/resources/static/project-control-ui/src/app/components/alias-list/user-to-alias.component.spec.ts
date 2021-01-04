import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserToAliasComponent } from './user-to-alias.component';

describe('AliasListComponent', () => {
  let component: UserToAliasComponent;
  let fixture: ComponentFixture<UserToAliasComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UserToAliasComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UserToAliasComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
