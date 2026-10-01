import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { EquipmentList } from './equipment-list';
import { EquipmentService } from '../../../core/services/equipment.service';

describe('EquipmentList', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EquipmentList],
      providers: [
        provideRouter([]),
        {
          provide: EquipmentService,
          useValue: {
            getAllEquipment: () => of([]),
            getMaintenanceDueEquipment: () => of([])
          }
        }
      ]
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(EquipmentList);
    expect(fixture.componentInstance).toBeTruthy();
  });
});