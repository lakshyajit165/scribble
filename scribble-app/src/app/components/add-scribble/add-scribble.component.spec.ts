import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddScribbleComponent } from './add-scribble.component';

describe('AddScribbleComponent', () => {
  let component: AddScribbleComponent;
  let fixture: ComponentFixture<AddScribbleComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddScribbleComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddScribbleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
