import { TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { EquipmentDetail } from './equipment-detail';
import { EquipmentService } from '../../../core/services/equipment.service';

describe('EquipmentDetail', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EquipmentDetail],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: () => '1'
              }
            }
          }
        },
        {
          provide: EquipmentService,
          useValue: {
            getEquipmentById: () => of({
              id: 1,
              name: 'Test Equipment',
              type: 'Generator',
              location: 'Test Location',
              status: 'OPERATIONAL',
              installationDate: '2026-01-01',
              nextMaintenanceDate: '2026-12-01'
            })
          }
        }
      ]
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(EquipmentDetail);
    expect(fixture.componentInstance).toBeTruthy();
  });
});