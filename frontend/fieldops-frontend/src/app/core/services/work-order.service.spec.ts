import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import {
  provideHttpClientTesting,
  HttpTestingController
} from '@angular/common/http/testing';

import { WorkOrderService } from './work-order.service';
import {
  WorkOrder,
  WorkOrderRequest
} from '../../features/work-orders/work-order.model';

describe('WorkOrderService', () => {
  let service: WorkOrderService;
  let httpTesting: HttpTestingController;

  const workOrder: WorkOrder = {
    id: 1,
    equipmentId: 10,
    engineerId: 20,
    priority: 'HIGH',
    description: 'Inspect hydraulic system',
    status: 'OPEN',
    createdAt: '2026-10-01T10:00:00',
    dueDate: '2026-10-10',
    completedAt: null
  };

  const request: WorkOrderRequest = {
    equipmentId: 10,
    engineerId: 20,
    priority: 'HIGH',
    description: 'Inspect hydraulic system',
    dueDate: '2026-10-10'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        WorkOrderService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(WorkOrderService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get all work orders', () => {
    service.getAllWorkOrders().subscribe(result => {
      expect(result).toEqual([workOrder]);
    });

    const req = httpTesting.expectOne(
      'http://localhost:8080/api/work-orders'
    );

    expect(req.request.method).toBe('GET');

    req.flush([workOrder]);
  });

  it('should get work order by id', () => {
    service.getWorkOrderById(1).subscribe(result => {
      expect(result).toEqual(workOrder);
    });

    const req = httpTesting.expectOne(
      'http://localhost:8080/api/work-orders/1'
    );

    expect(req.request.method).toBe('GET');

    req.flush(workOrder);
  });

  it('should create work order', () => {
    service.createWorkOrder(request).subscribe(result => {
      expect(result).toEqual(workOrder);
    });

    const req = httpTesting.expectOne(
      'http://localhost:8080/api/work-orders'
    );

    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(request);

    req.flush(workOrder);
  });

  it('should update work order', () => {
    service.updateWorkOrder(1, request).subscribe(result => {
      expect(result).toEqual(workOrder);
    });

    const req = httpTesting.expectOne(
      'http://localhost:8080/api/work-orders/1'
    );

    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(request);

    req.flush(workOrder);
  });

  it('should assign engineer to work order', () => {
    service.assignEngineer(1, 20).subscribe(result => {
      expect(result).toEqual(workOrder);
    });

    const req = httpTesting.expectOne(
      'http://localhost:8080/api/work-orders/1/assign/20'
    );

    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual({});

    req.flush(workOrder);
  });

    it('should update work order status', () => {
    service.updateStatus(1, 'IN_PROGRESS').subscribe(result => {
        expect(result).toEqual(workOrder);
    });

    const req = httpTesting.expectOne(
        request =>
        request.url === 'http://localhost:8080/api/work-orders/1/status'
    );

    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toBeNull();
    expect(req.request.params.get('status')).toBe('IN_PROGRESS');

    req.flush(workOrder);
    });

  it('should filter work orders by status', () => {
    service.getByStatus('OPEN').subscribe(result => {
      expect(result).toEqual([workOrder]);
    });

    const req = httpTesting.expectOne(
      'http://localhost:8080/api/work-orders/status/OPEN'
    );

    expect(req.request.method).toBe('GET');

    req.flush([workOrder]);
  });

  it('should filter work orders by priority', () => {
    service.getByPriority('HIGH').subscribe(result => {
      expect(result).toEqual([workOrder]);
    });

    const req = httpTesting.expectOne(
      'http://localhost:8080/api/work-orders/priority/HIGH'
    );

    expect(req.request.method).toBe('GET');

    req.flush([workOrder]);
  });

  it('should filter work orders by equipment', () => {
    service.getByEquipment(10).subscribe(result => {
      expect(result).toEqual([workOrder]);
    });

    const req = httpTesting.expectOne(
      'http://localhost:8080/api/work-orders/equipment/10'
    );

    expect(req.request.method).toBe('GET');

    req.flush([workOrder]);
  });

  it('should filter work orders by engineer', () => {
    service.getByEngineer(20).subscribe(result => {
      expect(result).toEqual([workOrder]);
    });

    const req = httpTesting.expectOne(
      'http://localhost:8080/api/work-orders/engineer/20'
    );

    expect(req.request.method).toBe('GET');

    req.flush([workOrder]);
  });
});