package com.fieldops.service;

import com.fieldops.dto.DocumentResponse;
import com.fieldops.entity.Document;
import com.fieldops.entity.Equipment;
import com.fieldops.entity.WorkOrder;
import com.fieldops.repository.DocumentRepository;
import com.fieldops.repository.EquipmentRepository;
import com.fieldops.repository.WorkOrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentService {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    private final DocumentRepository documentRepository;
    private final EquipmentRepository equipmentRepository;
    private final WorkOrderRepository workOrderRepository;
    private final Path storageDirectory;

    public DocumentService(
            DocumentRepository documentRepository,
            EquipmentRepository equipmentRepository,
            WorkOrderRepository workOrderRepository,
            @Value("${fieldops.storage.document-directory}") String documentDirectory) {

        this.documentRepository = documentRepository;
        this.equipmentRepository = equipmentRepository;
        this.workOrderRepository = workOrderRepository;
        this.storageDirectory = Paths.get(documentDirectory)
                .toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(this.storageDirectory);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Unable to initialize document storage",
                    e
            );
        }
    }

    @Transactional
    public DocumentResponse uploadForEquipment(
            Long equipmentId,
            MultipartFile file,
            String documentType) {

        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Equipment not found: " + equipmentId
                ));

        validateFile(file);
        validateDocumentType(documentType);

        return saveDocument(
                file,
                documentType,
                equipment,
                null
        );
    }

    @Transactional
    public DocumentResponse uploadForWorkOrder(
            Long workOrderId,
            MultipartFile file,
            String documentType) {

        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Work order not found: " + workOrderId
                ));

        validateFile(file);
        validateDocumentType(documentType);

        return saveDocument(
                file,
                documentType,
                null,
                workOrder
        );
    }

    @Transactional(readOnly = true)
    public List<DocumentResponse> getEquipmentDocuments(Long equipmentId) {

        if (!equipmentRepository.existsById(equipmentId)) {
            throw new IllegalArgumentException(
                    "Equipment not found: " + equipmentId
            );
        }

        return documentRepository
                .findByEquipmentIdOrderByUploadedAtDesc(equipmentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DocumentResponse> getWorkOrderDocuments(Long workOrderId) {

        if (!workOrderRepository.existsById(workOrderId)) {
            throw new IllegalArgumentException(
                    "Work order not found: " + workOrderId
            );
        }

        return documentRepository
                .findByWorkOrderIdOrderByUploadedAtDesc(workOrderId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Resource getDocumentResource(Long documentId) {

        Document document = findDocument(documentId);

        try {
            Path filePath = storageDirectory
                    .resolve(document.getStoredFileName())
                    .normalize();

            if (!filePath.startsWith(storageDirectory)) {
                throw new IllegalStateException(
                        "Invalid document storage path"
                );
            }

            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new IllegalStateException(
                        "Document file not found"
                );
            }

            return resource;

        } catch (MalformedURLException e) {
            throw new IllegalStateException(
                    "Unable to load document",
                    e
            );
        }
    }

    @Transactional
    public void deleteDocument(Long documentId) {

        Document document = findDocument(documentId);

        Path filePath = storageDirectory
                .resolve(document.getStoredFileName())
                .normalize();

        try {
            if (filePath.startsWith(storageDirectory)) {
                Files.deleteIfExists(filePath);
            }
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Unable to delete document file",
                    e
            );
        }

        documentRepository.delete(document);
    }

    private DocumentResponse saveDocument(
            MultipartFile file,
            String documentType,
            Equipment equipment,
            WorkOrder workOrder) {

        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null || originalFileName.isBlank()) {
            throw new IllegalArgumentException(
                    "File name is required"
            );
        }

        String cleanFileName = Paths.get(originalFileName)
                .getFileName()
                .toString();

        String storedFileName =
                UUID.randomUUID() + ".pdf";

        Path targetPath = storageDirectory
                .resolve(storedFileName)
                .normalize();

        if (!targetPath.startsWith(storageDirectory)) {
            throw new IllegalStateException(
                    "Invalid document storage path"
            );
        }

        try {
            Files.copy(
                    file.getInputStream(),
                    targetPath,
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Unable to store document",
                    e
            );
        }

        Document document = new Document();

        document.setFileName(cleanFileName);
        document.setStoredFileName(storedFileName);
        document.setContentType("application/pdf");
        document.setFileSize(file.getSize());
        document.setStoragePath(targetPath.toString());
        document.setDocumentType(documentType.trim().toUpperCase());
        document.setEquipment(equipment);
        document.setWorkOrder(workOrder);
        document.setUploadedAt(LocalDateTime.now());

        try {
            return toResponse(
                    documentRepository.save(document)
            );
        } catch (RuntimeException e) {
            try {
                Files.deleteIfExists(targetPath);
            } catch (IOException ignored) {
            }

            throw e;
        }
    }

    private void validateFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "PDF file is required"
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    "PDF file size must not exceed 10 MB"
            );
        }

        String contentType = file.getContentType();

        if (!"application/pdf".equalsIgnoreCase(contentType)) {
            throw new IllegalArgumentException(
                    "Only PDF files are allowed"
            );
        }

        String fileName = file.getOriginalFilename();

        if (fileName == null ||
            !fileName.toLowerCase().endsWith(".pdf")) {

            throw new IllegalArgumentException(
                    "File must have a .pdf extension"
            );
        }
    }

    private void validateDocumentType(String documentType) {

        if (documentType == null || documentType.isBlank()) {
            throw new IllegalArgumentException(
                    "Document type is required"
            );
        }

        String normalized =
                documentType.trim().toUpperCase();

        if (!List.of(
                "MANUAL",
                "INSPECTION_REPORT",
                "MAINTENANCE_REPORT",
                "SERVICE_REPORT",
                "OTHER"
        ).contains(normalized)) {

            throw new IllegalArgumentException(
                    "Invalid document type"
            );
        }
    }

    private Document findDocument(Long documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Document not found: " + documentId
                ));
    }

    private DocumentResponse toResponse(Document document) {

        DocumentResponse response = new DocumentResponse();

        response.setId(document.getId());
        response.setFileName(document.getFileName());
        response.setContentType(document.getContentType());
        response.setFileSize(document.getFileSize());
        response.setDocumentType(document.getDocumentType());
        response.setUploadedAt(document.getUploadedAt());

        if (document.getEquipment() != null) {
            response.setEquipmentId(
                    document.getEquipment().getId()
            );
        }

        if (document.getWorkOrder() != null) {
            response.setWorkOrderId(
                    document.getWorkOrder().getId()
            );
        }

        return response;
    }
}