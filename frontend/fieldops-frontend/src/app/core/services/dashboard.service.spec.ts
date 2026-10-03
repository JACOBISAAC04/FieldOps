import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting
} from '@angular/common/http/testing';
import { DashboardService } from './dashboard.service';
import { DashboardSummary } from '../models/dashboard.model';

describe('DashboardService', () => {
  let service: DashboardService;
  let httpTesting: HttpTestingController;

  const dashboardSummary: DashboardSummary = {
  totalEquipment: 3,
  operationalEquipment: 1,
  maintenanceRequiredEquipment: 1,
  deactivatedEquipment: 1,
  totalWorkOrders: 6,
  openWorkOrders: 0,
  assignedWorkOrders: 3,
  inProgressWorkOrders: 1,
  overdueWorkOrders: 0,
  highPriorityWorkOrders: 1,
  criticalWorkOrders: 1,
  availableEngineers: 4,
  busyEngineers: 3,
  unavailableEngineers: 2,
  maintenanceDueEquipment: 1
};

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        DashboardService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(DashboardService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get dashboard summary', () => {
    service.getSummary().subscribe(result => {
      expect(result).toEqual(dashboardSummary);
    });

    const req = httpTesting.expectOne(
      'http://localhost:8080/api/dashboard/summary'
    );

    expect(req.request.method).toBe('GET');

    req.flush(dashboardSummary);
  });
});