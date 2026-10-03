package com.fieldops.service;
import java.io.IOException;
import com.fieldops.dto.DocumentResponse;
import com.fieldops.entity.Document;
import com.fieldops.entity.Equipment;
import com.fieldops.entity.WorkOrder;
import com.fieldops.repository.DocumentRepository;
import com.fieldops.repository.EquipmentRepository;
import com.fieldops.repository.WorkOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class DocumentServiceTest {

    @TempDir
    Path tempDirectory;

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private WorkOrderRepository workOrderRepository;

    private DocumentService documentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        documentService = new DocumentService(
                documentRepository,
                equipmentRepository,
                workOrderRepository,
                tempDirectory.toString()
        );
    }
    private long countStoredFiles() {
    try (var files = Files.list(tempDirectory)) {
        return files.count();
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
}

    @Test
    void uploadsPdfForEquipment() {
        Equipment equipment = new Equipment();
        equipment.setId(1L);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "manual.pdf",
                "application/pdf",
                "test pdf content".getBytes()
        );

        when(equipmentRepository.findById(1L))
                .thenReturn(Optional.of(equipment));

        when(documentRepository.save(any(Document.class)))
                .thenAnswer(invocation -> {
                    Document document = invocation.getArgument(0);
                    document.setId(10L);
                    return document;
                });

        DocumentResponse response =
                documentService.uploadForEquipment(
                        1L,
                        file,
                        "MANUAL"
                );

        assertEquals(10L, response.getId());
        assertEquals("manual.pdf", response.getFileName());
        assertEquals("application/pdf", response.getContentType());
        assertEquals("MANUAL", response.getDocumentType());
        assertEquals(1L, response.getEquipmentId());
        assertNull(response.getWorkOrderId());

        assertEquals(1, countStoredFiles());

        verify(documentRepository).save(any(Document.class));
    }

    @Test
    void uploadsPdfForWorkOrder() {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setId(5L);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "maintenance.pdf",
                "application/pdf",
                "maintenance report".getBytes()
        );

        when(workOrderRepository.findById(5L))
                .thenReturn(Optional.of(workOrder));

        when(documentRepository.save(any(Document.class)))
                .thenAnswer(invocation -> {
                    Document document = invocation.getArgument(0);
                    document.setId(20L);
                    return document;
                });

        DocumentResponse response =
                documentService.uploadForWorkOrder(
                        5L,
                        file,
                        "MAINTENANCE_REPORT"
                );

        assertEquals(20L, response.getId());
        assertEquals("maintenance.pdf", response.getFileName());
        assertEquals("MAINTENANCE_REPORT", response.getDocumentType());
        assertEquals(5L, response.getWorkOrderId());
        assertNull(response.getEquipmentId());

        assertEquals(1, countStoredFiles());

        verify(documentRepository).save(any(Document.class));
    }

    @Test
    void rejectsNonPdfFile() {
        Equipment equipment = new Equipment();
        equipment.setId(1L);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "manual.txt",
                "text/plain",
                "text content".getBytes()
        );

        when(equipmentRepository.findById(1L))
                .thenReturn(Optional.of(equipment));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> documentService.uploadForEquipment(
                                1L,
                                file,
                                "MANUAL"
                        )
                );

        assertEquals(
                "Only PDF files are allowed",
                exception.getMessage()
        );

        verify(documentRepository, never()).save(any());
    }

    @Test
    void rejectsFileWithoutPdfExtension() {
        Equipment equipment = new Equipment();
        equipment.setId(1L);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "manual.exe",
                "application/pdf",
                "content".getBytes()
        );

        when(equipmentRepository.findById(1L))
                .thenReturn(Optional.of(equipment));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> documentService.uploadForEquipment(
                                1L,
                                file,
                                "MANUAL"
                        )
                );

        assertEquals(
                "File must have a .pdf extension",
                exception.getMessage()
        );

        verify(documentRepository, never()).save(any());
    }

    @Test
    void rejectsOversizedFile() {
        Equipment equipment = new Equipment();
        equipment.setId(1L);

        byte[] content = new byte[10 * 1024 * 1024 + 1];

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "large.pdf",
                "application/pdf",
                content
        );

        when(equipmentRepository.findById(1L))
                .thenReturn(Optional.of(equipment));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> documentService.uploadForEquipment(
                                1L,
                                file,
                                "MANUAL"
                        )
                );

        assertEquals(
                "PDF file size must not exceed 10 MB",
                exception.getMessage()
        );

        verify(documentRepository, never()).save(any());
    }

    @Test
    void rejectsInvalidDocumentType() {
        Equipment equipment = new Equipment();
        equipment.setId(1L);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "manual.pdf",
                "application/pdf",
                "content".getBytes()
        );

        when(equipmentRepository.findById(1L))
                .thenReturn(Optional.of(equipment));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> documentService.uploadForEquipment(
                                1L,
                                file,
                                "INVALID"
                        )
                );

        assertEquals(
                "Invalid document type",
                exception.getMessage()
        );

        verify(documentRepository, never()).save(any());
    }

    @Test
    void rejectsMissingEquipment() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "manual.pdf",
                "application/pdf",
                "content".getBytes()
        );

        when(equipmentRepository.findById(99L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> documentService.uploadForEquipment(
                                99L,
                                file,
                                "MANUAL"
                        )
                );

        assertEquals(
                "Equipment not found: 99",
                exception.getMessage()
        );

        verify(documentRepository, never()).save(any());
    }

    @Test
    void rejectsMissingWorkOrder() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "report.pdf",
                "application/pdf",
                "content".getBytes()
        );

        when(workOrderRepository.findById(99L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> documentService.uploadForWorkOrder(
                                99L,
                                file,
                                "SERVICE_REPORT"
                        )
                );

        assertEquals(
                "Work order not found: 99",
                exception.getMessage()
        );

        verify(documentRepository, never()).save(any());
    }

    @Test
    void returnsEquipmentDocuments() {
        Equipment equipment = new Equipment();
        equipment.setId(1L);

        Document document = new Document();
        document.setId(10L);
        document.setFileName("manual.pdf");
        document.setContentType("application/pdf");
        document.setFileSize(100L);
        document.setDocumentType("MANUAL");
        document.setEquipment(equipment);
        document.setUploadedAt(LocalDateTime.now());

        when(equipmentRepository.existsById(1L))
                .thenReturn(true);

        when(documentRepository
                .findByEquipmentIdOrderByUploadedAtDesc(1L))
                .thenReturn(List.of(document));

        List<DocumentResponse> result =
                documentService.getEquipmentDocuments(1L);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getId());
        assertEquals("manual.pdf", result.get(0).getFileName());
        assertEquals(1L, result.get(0).getEquipmentId());
    }

    @Test
    void returnsWorkOrderDocuments() {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setId(5L);

        Document document = new Document();
        document.setId(20L);
        document.setFileName("report.pdf");
        document.setContentType("application/pdf");
        document.setFileSize(200L);
        document.setDocumentType("SERVICE_REPORT");
        document.setWorkOrder(workOrder);
        document.setUploadedAt(LocalDateTime.now());

        when(workOrderRepository.existsById(5L))
                .thenReturn(true);

        when(documentRepository
                .findByWorkOrderIdOrderByUploadedAtDesc(5L))
                .thenReturn(List.of(document));

        List<DocumentResponse> result =
                documentService.getWorkOrderDocuments(5L);

        assertEquals(1, result.size());
        assertEquals(20L, result.get(0).getId());
        assertEquals("report.pdf", result.get(0).getFileName());
        assertEquals(5L, result.get(0).getWorkOrderId());
    }

    @Test
    void rejectsMissingEquipmentWhenGettingDocuments() {
        when(equipmentRepository.existsById(99L))
                .thenReturn(false);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> documentService.getEquipmentDocuments(99L)
                );

        assertEquals(
                "Equipment not found: 99",
                exception.getMessage()
        );
    }

    @Test
    void rejectsMissingWorkOrderWhenGettingDocuments() {
        when(workOrderRepository.existsById(99L))
                .thenReturn(false);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> documentService.getWorkOrderDocuments(99L)
                );

        assertEquals(
                "Work order not found: 99",
                exception.getMessage()
        );
    }

    @Test
    void returnsDocumentResource() throws Exception {
        Path filePath = tempDirectory.resolve("stored.pdf");

        Files.write(
                filePath,
                "pdf content".getBytes()
        );

        Document document = new Document();
        document.setId(10L);
        document.setFileName("manual.pdf");
        document.setStoredFileName("stored.pdf");
        document.setContentType("application/pdf");

        when(documentRepository.findById(10L))
                .thenReturn(Optional.of(document));

        Resource resource =
                documentService.getDocumentResource(10L);

        assertTrue(resource.exists());
        assertTrue(resource.isReadable());
        assertEquals(
                "stored.pdf",
                resource.getFilename()
        );
    }

    @Test
    void rejectsMissingDocument() {
        when(documentRepository.findById(99L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> documentService.getDocumentResource(99L)
                );

        assertEquals(
                "Document not found: 99",
                exception.getMessage()
        );
    }

    @Test
    void deletesDocumentAndStoredFile() throws Exception {
        Path filePath = tempDirectory.resolve("stored.pdf");

        Files.write(
                filePath,
                "pdf content".getBytes()
        );

        Document document = new Document();
        document.setId(10L);
        document.setStoredFileName("stored.pdf");

        when(documentRepository.findById(10L))
                .thenReturn(Optional.of(document));

        documentService.deleteDocument(10L);

        assertFalse(Files.exists(filePath));

        verify(documentRepository).delete(document);
    }

    @Test
    void doesNotSaveDocumentWhenRepositorySaveFails() throws Exception {
        Equipment equipment = new Equipment();
        equipment.setId(1L);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "manual.pdf",
                "application/pdf",
                "content".getBytes()
        );

        when(equipmentRepository.findById(1L))
                .thenReturn(Optional.of(equipment));

        when(documentRepository.save(any(Document.class)))
                .thenThrow(new RuntimeException("Database failure"));

        assertThrows(
                RuntimeException.class,
                () -> documentService.uploadForEquipment(
                        1L,
                        file,
                        "MANUAL"
                )
        );

        assertEquals(0, Files.list(tempDirectory).count());
    }
}