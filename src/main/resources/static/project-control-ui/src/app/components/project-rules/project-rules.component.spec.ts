import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectRulesComponent } from './project-rules.component';

describe('ProjectRulesComponent', () => {
  let component: ProjectRulesComponent;
  let fixture: ComponentFixture<ProjectRulesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProjectRulesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectRulesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
