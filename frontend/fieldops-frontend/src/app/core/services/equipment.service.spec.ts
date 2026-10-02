import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import {
  provideHttpClientTesting,
  HttpTestingController
} from '@angular/common/http/testing';

import { EquipmentService } from './equipment.service';
import { Equipment, EquipmentRequest } from '../models/equipment.model';
import { EquipmentRisk } from '../models/equipment-risk.model';

describe('EquipmentService', () => {
  let service: EquipmentService;
  let httpTesting: HttpTestingController;

  const equipment: Equipment = {
    id: 1,
    name: 'Pump A',
    type: 'Pump',
    location: 'Plant A',
    status: 'ACTIVE',
    installationDate: '2025-01-15',
    nextMaintenanceDate: '2026-10-20'
  };

  const request: EquipmentRequest = {
    name: 'Pump A',
    type: 'Pump',
    location: 'Plant A',
    status: 'ACTIVE',
    installationDate: '2025-01-15',
    nextMaintenanceDate: '2026-10-20'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        EquipmentService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(EquipmentService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get all equipment', () => {
    service.getAllEquipment().subscribe(result => {
      expect(result).toEqual([equipment]);
    });

    const req = httpTesting.expectOne(
      'http://localhost:8080/api/equipment'
    );

    expect(req.request.method).toBe('GET');

    req.flush([equipment]);
  });

  it('should get equipment with filters', () => {
    service.getAllEquipment({
      status: 'ACTIVE',
      location: 'Plant A',
      type: 'Pump',
      name: 'Pump A'
    }).subscribe(result => {
      expect(result).toEqual([equipment]);
    });

    const req = httpTesting.expectOne(
      request => request.url === 'http://localhost:8080/api/equipment'
    );

    expect(req.request.method).toBe('GET');
    expect(req.request.params.get('status')).toBe('ACTIVE');
    expect(req.request.params.get('location')).toBe('Plant A');
    expect(req.request.params.get('type')).toBe('Pump');
    expect(req.request.params.get('name')).toBe('Pump A');

    req.flush([equipment]);
  });

  it('should get equipment by id', () => {
    service.getEquipmentById(1).subscribe(result => {
      expect(result).toEqual(equipment);
    });

    const req = httpTesting.expectOne(
      'http://localhost:8080/api/equipment/1'
    );

    expect(req.request.method).toBe('GET');

    req.flush(equipment);
  });

  it('should get equipment risk', () => {
    const risk: EquipmentRisk = {
      equipmentId: 1,
      riskLevel: 'HIGH',
      maintenanceDue: true,
      reasons: ['Maintenance due']
    };

    service.getEquipmentRisk(1).subscribe(result => {
      expect(result).toEqual(risk);
    });

    const req = httpTesting.expectOne(
      'http://localhost:8080/api/analytics/equipment/1'
    );

    expect(req.request.method).toBe('GET');

    req.flush(risk);
  });

  it('should create equipment', () => {
    service.createEquipment(request).subscribe(result => {
      expect(result).toEqual(equipment);
    });

    const req = httpTesting.expectOne(
      'http://localhost:8080/api/equipment'
    );

    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(request);

    req.flush(equipment);
  });

  it('should update equipment', () => {
    service.updateEquipment(1, request).subscribe(result => {
      expect(result).toEqual(equipment);
    });

    const req = httpTesting.expectOne(
      'http://localhost:8080/api/equipment/1'
    );

    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(request);

    req.flush(equipment);
  });

  it('should deactivate equipment', () => {
  service.deactivateEquipment(1).subscribe();

  const req = httpTesting.expectOne(
    'http://localhost:8080/api/equipment/1'
  );

  expect(req.request.method).toBe('DELETE');

  req.flush(null);
  });

  it('should get maintenance due equipment', () => {
    service.getMaintenanceDueEquipment().subscribe(result => {
      expect(result).toEqual([equipment]);
    });

    const req = httpTesting.expectOne(
      'http://localhost:8080/api/equipment/maintenance-due'
    );

    expect(req.request.method).toBe('GET');

    req.flush([equipment]);
  });
});