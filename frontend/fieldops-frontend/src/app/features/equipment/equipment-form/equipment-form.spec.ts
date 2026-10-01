import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { EquipmentForm } from './equipment-form';
import { EquipmentService } from '../../../core/services/equipment.service';

describe('EquipmentForm', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EquipmentForm],
      providers: [
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: () => null
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
            }),
            createEquipment: () => of({
              id: 1,
              name: 'Test Equipment',
              type: 'Generator',
              location: 'Test Location',
              status: 'OPERATIONAL',
              installationDate: '2026-01-01',
              nextMaintenanceDate: '2026-12-01'
            }),
            updateEquipment: () => of({
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
    const fixture = TestBed.createComponent(EquipmentForm);
    expect(fixture.componentInstance).toBeTruthy();
  });
});