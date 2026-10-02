import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import {
  provideHttpClientTesting,
  HttpTestingController
} from '@angular/common/http/testing';

import { EngineerService } from './engineer.service';
import {
  Engineer,
  EngineerRequest,
  AvailableUser
} from '../models/engineer.model';

describe('EngineerService', () => {
  let service: EngineerService;
  let httpTesting: HttpTestingController;

  const engineer: Engineer = {
    id: 1,
    userId: 10,
    name: 'John Engineer',
    email: 'john@example.com',
    specialization: 'Mechanical',
    location: 'Plant A',
    availability: 'AVAILABLE'
  };

  const request: EngineerRequest = {
    userId: 10,
    specialization: 'Mechanical',
    location: 'Plant A',
    availability: 'AVAILABLE'
  };

  const availableUser: AvailableUser = {
    id: 10,
    name: 'John Engineer',
    email: 'john@example.com'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        EngineerService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(EngineerService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get all engineers', () => {
    service.getAllEngineers().subscribe(result => {
      expect(result).toEqual([engineer]);
    });

    const req = httpTesting.expectOne(
      'http://localhost:8080/api/engineers'
    );

    expect(req.request.method).toBe('GET');

    req.flush([engineer]);
  });

  it('should get engineer by id', () => {
    service.getEngineerById(1).subscribe(result => {
      expect(result).toEqual(engineer);
    });

    const req = httpTesting.expectOne(
      'http://localhost:8080/api/engineers/1'
    );

    expect(req.request.method).toBe('GET');

    req.flush(engineer);
  });

  it('should create engineer', () => {
    service.createEngineer(request).subscribe(result => {
      expect(result).toEqual(engineer);
    });

    const req = httpTesting.expectOne(
      'http://localhost:8080/api/engineers'
    );

    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(request);

    req.flush(engineer);
  });

  it('should update engineer', () => {
    service.updateEngineer(1, request).subscribe(result => {
      expect(result).toEqual(engineer);
    });

    const req = httpTesting.expectOne(
      'http://localhost:8080/api/engineers/1'
    );

    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(request);

    req.flush(engineer);
  });

  it('should filter engineers by specialization', () => {
    service.getBySpecialization('Mechanical').subscribe(result => {
      expect(result).toEqual([engineer]);
    });

    const req = httpTesting.expectOne(
      'http://localhost:8080/api/engineers/specialization/Mechanical'
    );

    expect(req.request.method).toBe('GET');

    req.flush([engineer]);
  });

  it('should filter engineers by location', () => {
    service.getByLocation('Plant A').subscribe(result => {
      expect(result).toEqual([engineer]);
    });

    const req = httpTesting.expectOne(
      'http://localhost:8080/api/engineers/location/Plant A'
    );

    expect(req.request.method).toBe('GET');

    req.flush([engineer]);
  });

  it('should filter engineers by availability', () => {
    service.getByAvailability('AVAILABLE').subscribe(result => {
      expect(result).toEqual([engineer]);
    });

    const req = httpTesting.expectOne(
      'http://localhost:8080/api/engineers/availability/AVAILABLE'
    );

    expect(req.request.method).toBe('GET');

    req.flush([engineer]);
  });

  it('should get available engineers', () => {
    service.getAvailableEngineers().subscribe(result => {
      expect(result).toEqual([availableUser]);
    });

    const req = httpTesting.expectOne(
      'http://localhost:8080/api/users/available-engineers'
    );

    expect(req.request.method).toBe('GET');

    req.flush([availableUser]);
  });
});