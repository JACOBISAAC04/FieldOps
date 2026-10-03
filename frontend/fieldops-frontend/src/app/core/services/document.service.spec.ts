import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import {
  provideHttpClientTesting,
  HttpTestingController
} from '@angular/common/http/testing';
import { DocumentService } from './document.service';
import { Document } from '../models/document.model';

describe('DocumentService', () => {
  let service: DocumentService;
  let httpTesting: HttpTestingController;

  const document: Document = {
    id: 1,
    fileName: 'manual.pdf',
    contentType: 'application/pdf',
    fileSize: 1024,
    documentType: 'MANUAL',
    equipmentId: 10,
    workOrderId: null,
    uploadedAt: '2026-10-03T22:00:00'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        DocumentService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(DocumentService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should upload document for equipment', () => {
    const file = new File(
      ['pdf content'],
      'manual.pdf',
      { type: 'application/pdf' }
    );

    service.uploadForEquipment(
      10,
      file,
      'MANUAL'
    ).subscribe(result => {
      expect(result).toEqual(document);
    });

    const req = httpTesting.expectOne(
      'http://localhost:8080/api/documents/equipment/10'
    );

    expect(req.request.method).toBe('POST');
    expect(req.request.body instanceof FormData).toBe(true);

    const body = req.request.body as FormData;

    expect(body.get('documentType')).toBe('MANUAL');
    expect(body.get('file')).toBe(file);

    req.flush(document);
  });

  it('should upload document for work order', () => {
    const file = new File(
      ['pdf content'],
      'maintenance.pdf',
      { type: 'application/pdf' }
    );

    const workOrderDocument: Document = {
      ...document,
      workOrderId: 20,
      equipmentId: null,
      fileName: 'maintenance.pdf',
      documentType: 'MAINTENANCE_REPORT'
    };

    service.uploadForWorkOrder(
      20,
      file,
      'MAINTENANCE_REPORT'
    ).subscribe(result => {
      expect(result).toEqual(workOrderDocument);
    });

    const req = httpTesting.expectOne(
      'http://localhost:8080/api/documents/work-order/20'
    );

    expect(req.request.method).toBe('POST');
    expect(req.request.body instanceof FormData).toBe(true);

    const body = req.request.body as FormData;

    expect(body.get('documentType')).toBe(
      'MAINTENANCE_REPORT'
    );

    expect(body.get('file')).toBe(file);

    req.flush(workOrderDocument);
  });

  it('should get equipment documents', () => {
    service.getEquipmentDocuments(10).subscribe(result => {
      expect(result).toEqual([document]);
    });

    const req = httpTesting.expectOne(
      'http://localhost:8080/api/documents/equipment/10'
    );

    expect(req.request.method).toBe('GET');

    req.flush([document]);
  });

  it('should get work order documents', () => {
    const workOrderDocument: Document = {
      ...document,
      workOrderId: 20,
      equipmentId: null
    };

    service.getWorkOrderDocuments(20).subscribe(result => {
      expect(result).toEqual([workOrderDocument]);
    });

    const req = httpTesting.expectOne(
      'http://localhost:8080/api/documents/work-order/20'
    );

    expect(req.request.method).toBe('GET');

    req.flush([workOrderDocument]);
  });

  it('should generate document download url', () => {
    const url = service.getDownloadUrl(15);

    expect(url).toBe(
      'http://localhost:8080/api/documents/15/download'
    );
  });

  it('should delete document', () => {
    service.deleteDocument(15).subscribe(result => {
        expect(result).toBeNull();
    });

    const req = httpTesting.expectOne(
        'http://localhost:8080/api/documents/15'
    );

    expect(req.request.method).toBe('DELETE');

    req.flush(null);
    });
});