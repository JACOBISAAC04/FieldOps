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
    totalEquipment: 20,
    operationalEquipment: 14,
    maintenanceRequiredEquipment: 3,
    deactivatedEquipment: 3,
    totalWorkOrders: 15,
    openWorkOrders: 8,
    highPriorityWorkOrders: 4,
    availableEngineers: 5,
    busyEngineers: 3,
    unavailableEngineers: 2,
    maintenanceDueEquipment: 3
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