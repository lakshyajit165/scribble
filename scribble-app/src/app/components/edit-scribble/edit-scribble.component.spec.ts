import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditScribbleComponent } from './edit-scribble.component';

describe('EditScribbleComponent', () => {
  let component: EditScribbleComponent;
  let fixture: ComponentFixture<EditScribbleComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EditScribbleComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditScribbleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
