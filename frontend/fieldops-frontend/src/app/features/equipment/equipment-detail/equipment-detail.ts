import { Component, OnInit, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Equipment } from '../../../core/models/equipment.model';
import { EquipmentService } from '../../../core/services/equipment.service';
import { EquipmentRisk } from '../../../core/models/equipment-risk.model';
import { WorkOrder } from '../../work-orders/work-order.model';
import { WorkOrderService } from '../../../core/services/work-order.service';
import { Document } from '../../../core/models/document.model';
import { DocumentService } from '../../../core/services/document.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-equipment-detail',
  standalone: true,
  imports: [RouterLink, DatePipe],
  templateUrl: './equipment-detail.html',
  styleUrl: './equipment-detail.css'
})
export class EquipmentDetail implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private equipmentService = inject(EquipmentService);
  private workOrderService = inject(WorkOrderService);
  private documentService = inject(DocumentService);
  protected authService = inject(AuthService);

  equipment = signal<Equipment | null>(null);
  risk = signal<EquipmentRisk | null>(null);
  loading = signal(true);
  error = signal('');
  riskLoading = signal(true);
  riskError = signal('');
  maintenanceHistory = signal<WorkOrder[]>([]);
  historyLoading = signal(true);
  historyError = signal('');

  documents = signal<Document[]>([]);
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

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    if (!id) {
      this.error.set('Invalid equipment ID.');
      this.loading.set(false);
      return;
    }

    this.loadEquipment(id);
    this.loadRisk(id);
    this.loadMaintenanceHistory(id);
    this.loadDocuments(id);
  }

  private loadEquipment(id: number): void {
    this.equipmentService.getEquipmentById(id).subscribe({
      next: (data) => {
        this.equipment.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Unable to load equipment.');
        this.loading.set(false);
      }
    });
  }

  private loadRisk(id: number): void {
    this.equipmentService.getEquipmentRisk(id).subscribe({
      next: (data) => {
        this.risk.set(data);
        this.riskLoading.set(false);
      },
      error: () => {
        this.riskError.set('Unable to load equipment risk.');
        this.riskLoading.set(false);
      }
    });
  }

  private loadMaintenanceHistory(id: number): void {
    this.workOrderService.getMaintenanceHistory(id).subscribe({
      next: (data) => {
        this.maintenanceHistory.set(data);
        this.historyLoading.set(false);
      },
      error: () => {
        this.historyError.set('Unable to load maintenance history.');
        this.historyLoading.set(false);
      }
    });
  }

  private loadDocuments(id: number): void {
    this.documentsLoading.set(true);
    this.documentsError.set('');

    this.documentService.getEquipmentDocuments(id).subscribe({
      next: (data) => {
        this.documents.set(data);
        this.documentsLoading.set(false);
      },
      error: () => {
        this.documentsError.set('Unable to load documents.');
        this.documentsLoading.set(false);
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
    const equipmentId = this.equipment()?.id;
    const file = this.selectedFile();

    if (!equipmentId || !file) {
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
      .uploadForEquipment(
        equipmentId,
        file,
        this.selectedDocumentType()
      )
      .subscribe({
        next: () => {
          this.uploading.set(false);
          this.selectedFile.set(null);
          this.loadDocuments(equipmentId);
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
        const equipmentId = this.equipment()?.id;

        if (equipmentId) {
          this.loadDocuments(equipmentId);
        }
      },
      error: () => {
        this.documentsError.set('Unable to delete document.');
      }
    });
  }

  deactivate(): void {
    const equipmentId = this.equipment()?.id;

    if (!equipmentId) {
      return;
    }

    const confirmed = confirm(
      `Are you sure you want to deactivate ${this.equipment()?.name}?`
    );

    if (!confirmed) {
      return;
    }

    this.loading.set(true);
    this.error.set('');

    this.equipmentService.deactivateEquipment(equipmentId).subscribe({
      next: () => {
        this.router.navigate(['/equipment']);
      },
      error: () => {
        this.error.set('Unable to deactivate equipment.');
        this.loading.set(false);
      }
    });
  }
}
