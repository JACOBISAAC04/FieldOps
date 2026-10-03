import { Component, OnInit, signal } from '@angular/core';
import { DatePipe, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { WorkOrderService } from '../../../core/services/work-order.service';
import { EngineerService } from '../../../core/services/engineer.service';
import { DocumentService } from '../../../core/services/document.service';
import { WorkOrder } from '../work-order.model';
import { Engineer } from '../../../core/models/engineer.model';
import { Document } from '../../../core/models/document.model';

@Component({
  selector: 'app-work-order-detail',
  standalone: true,
  imports: [FormsModule, DatePipe, DecimalPipe],
  templateUrl: './work-order-detail.html',
  styleUrl: './work-order-detail.scss'
})
export class WorkOrderDetail implements OnInit {

  workOrder = signal<WorkOrder | null>(null);
  engineers = signal<Engineer[]>([]);
  documents = signal<Document[]>([]);

  loading = signal(true);
  saving = signal(false);
  error = signal('');
  success = signal('');

  documentsLoading = signal(true);
  documentsError = signal('');
  uploadError = signal('');
  uploading = signal(false);
  selectedFile = signal<File | null>(null);
  selectedDocumentType = signal('OTHER');

  readonly documentTypes = [
    'MANUAL',
    'INSPECTION_REPORT',
    'MAINTENANCE_REPORT',
    'SERVICE_REPORT',
    'OTHER'
  ];

  selectedStatus = '';
  engineerId: number | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private workOrderService: WorkOrderService,
    private engineerService: EngineerService,
    private documentService: DocumentService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.loadEngineers();

    this.workOrderService.getWorkOrderById(id).subscribe({
      next: (workOrder) => {
        this.setWorkOrder(workOrder);
        this.loading.set(false);
        this.loadDocuments(workOrder.id);
      },
      error: () => {
        this.error.set('Work order not found.');
        this.loading.set(false);
      }
    });
  }

  private loadEngineers(): void {
    this.engineerService.getAllEngineers().subscribe({
      next: (engineers) => {
        this.engineers.set(engineers);
      },
      error: () => {
        this.error.set('Unable to load engineers.');
      }
    });
  }

  private loadDocuments(workOrderId: number): void {
    this.documentsLoading.set(true);
    this.documentsError.set('');

    this.documentService.getWorkOrderDocuments(workOrderId).subscribe({
      next: (documents) => {
        this.documents.set(documents);
        this.documentsLoading.set(false);
      },
      error: () => {
        this.documentsError.set('Unable to load documents.');
        this.documentsLoading.set(false);
      }
    });
  }

  private setWorkOrder(workOrder: WorkOrder): void {
    this.workOrder.set(workOrder);
    this.selectedStatus = workOrder.status;
    this.engineerId = workOrder.engineerId;
  }

  getAvailableStatuses(): string[] {
    const status = this.workOrder()?.status;

    if (status === 'OPEN') {
      return ['ASSIGNED', 'CANCELLED'];
    }

    if (status === 'ASSIGNED') {
      return ['IN_PROGRESS', 'CANCELLED'];
    }

    if (status === 'IN_PROGRESS') {
      return ['COMPLETED', 'CANCELLED'];
    }

    return [];
  }

  canAssignEngineer(): boolean {
    const status = this.workOrder()?.status;
    return status === 'OPEN' || status === 'ASSIGNED';
  }

  canUpdateStatus(): boolean {
    return this.getAvailableStatuses().length > 0;
  }

  assignEngineer(): void {
    const current = this.workOrder();

    if (!current || this.engineerId === null) {
      this.error.set('Select an engineer to assign.');
      return;
    }

    this.runUpdate(
      this.workOrderService.assignEngineer(current.id, this.engineerId),
      'Engineer assigned.',
      'Unable to assign engineer.'
    );
  }

  changeStatus(): void {
    const current = this.workOrder();

    if (!current || !this.selectedStatus) {
      return;
    }

    this.runUpdate(
      this.workOrderService.updateStatus(current.id, this.selectedStatus),
      'Status updated.',
      'Unable to update status.'
    );
  }

  private runUpdate(
    request: ReturnType<WorkOrderService['assignEngineer']>,
    successMessage: string,
    errorMessage: string
  ): void {
    this.saving.set(true);
    this.error.set('');
    this.success.set('');

    request.subscribe({
      next: (updated) => {
        this.setWorkOrder(updated);
        this.success.set(successMessage);
        this.saving.set(false);
      },
      error: () => {
        this.error.set(errorMessage);
        this.saving.set(false);
      }
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0] ?? null;

    this.selectedFile.set(file);
    this.uploadError.set('');
  }

  onDocumentTypeChange(event: Event): void {
    const select = event.target as HTMLSelectElement;
    this.selectedDocumentType.set(select.value);
  }

  uploadDocument(): void {
    const workOrderId = this.workOrder()?.id;
    const file = this.selectedFile();

    if (!workOrderId || !file) {
      this.uploadError.set('Please select a PDF file.');
      return;
    }

    if (file.type !== 'application/pdf') {
      this.uploadError.set('Only PDF files are allowed.');
      return;
    }

    this.uploading.set(true);
    this.uploadError.set('');

    this.documentService
      .uploadForWorkOrder(
        workOrderId,
        file,
        this.selectedDocumentType()
      )
      .subscribe({
        next: () => {
          this.uploading.set(false);
          this.selectedFile.set(null);
          this.loadDocuments(workOrderId);
        },
        error: (error) => {
          this.uploading.set(false);
          this.uploadError.set(
            error?.error?.message || 'Unable to upload document.'
          );
        }
      });
  }

  downloadDocument(documentId: number): void {
    window.open(
      this.documentService.getDownloadUrl(documentId),
      '_blank'
    );
  }

  deleteDocument(documentId: number): void {
    const confirmed = confirm(
      'Are you sure you want to delete this document?'
    );

    if (!confirmed) {
      return;
    }

    this.documentService.deleteDocument(documentId).subscribe({
      next: () => {
        const workOrderId = this.workOrder()?.id;

        if (workOrderId) {
          this.loadDocuments(workOrderId);
        }
      },
      error: () => {
        this.documentsError.set('Unable to delete document.');
      }
    });
  }

  back(): void {
    this.router.navigate(['/work-orders']);
  }
}