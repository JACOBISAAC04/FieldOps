import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { WorkOrderDetail } from './work-order-detail';
import { WorkOrderService } from '../../../core/services/work-order.service';
import { EngineerService } from '../../../core/services/engineer.service';

describe('WorkOrderDetail', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WorkOrderDetail],
      providers: [
        provideRouter([]),
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
          provide: WorkOrderService,
          useValue: {
            getWorkOrderById: () => of({
              id: 1,
              equipmentId: 1,
              engineerId: 1,
              priority: 'HIGH',
              description: 'Test maintenance work',
              status: 'OPEN',
              createdAt: '2026-01-01T10:00:00',
              dueDate: '2026-12-01T10:00:00',
              completedAt: null
            }),
            assignEngineer: () => of({
              id: 1,
              equipmentId: 1,
              engineerId: 1,
              priority: 'HIGH',
              description: 'Test maintenance work',
              status: 'ASSIGNED',
              createdAt: '2026-01-01T10:00:00',
              dueDate: '2026-12-01T10:00:00',
              completedAt: null
            }),
            updateStatus: () => of({
              id: 1,
              equipmentId: 1,
              engineerId: 1,
              priority: 'HIGH',
              description: 'Test maintenance work',
              status: 'IN_PROGRESS',
              createdAt: '2026-01-01T10:00:00',
              dueDate: '2026-12-01T10:00:00',
              completedAt: null
            })
          }
        },
        {
          provide: EngineerService,
          useValue: {
            getAllEngineers: () => of([
              {
                id: 1,
                userId: 1,
                name: 'Jacob Isaac',
                email: 'jacob@fieldops.local',
                specialization: 'Mechanical Maintenance',
                location: 'Kerala',
                availability: 'AVAILABLE'
              }
            ])
          }
        }
      ]
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(WorkOrderDetail);
    expect(fixture.componentInstance).toBeTruthy();
  });
});