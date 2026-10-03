package com.fieldops.controller;

import com.fieldops.dto.DocumentResponse;
import com.fieldops.service.DocumentService;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(
            value = "/equipment/{equipmentId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<DocumentResponse> uploadForEquipment(
            @PathVariable Long equipmentId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") String documentType) {

        return ResponseEntity.ok(
                documentService.uploadForEquipment(
                        equipmentId,
                        file,
                        documentType
                )
        );
    }

    @PostMapping(
            value = "/work-order/{workOrderId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<DocumentResponse> uploadForWorkOrder(
            @PathVariable Long workOrderId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") String documentType) {

        return ResponseEntity.ok(
                documentService.uploadForWorkOrder(
                        workOrderId,
                        file,
                        documentType
                )
        );
    }

    @GetMapping("/equipment/{equipmentId}")
    public ResponseEntity<List<DocumentResponse>> getEquipmentDocuments(
            @PathVariable Long equipmentId) {

        return ResponseEntity.ok(
                documentService.getEquipmentDocuments(equipmentId)
        );
    }

    @GetMapping("/work-order/{workOrderId}")
    public ResponseEntity<List<DocumentResponse>> getWorkOrderDocuments(
            @PathVariable Long workOrderId) {

        return ResponseEntity.ok(
                documentService.getWorkOrderDocuments(workOrderId)
        );
    }

    @GetMapping("/{documentId}/download")
    public ResponseEntity<Resource> downloadDocument(
            @PathVariable Long documentId) {

        Resource resource =
                documentService.getDocumentResource(documentId);

        String fileName = resource.getFilename();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline()
                                .filename(fileName == null ? "document.pdf" : fileName)
                                .build()
                                .toString()
                )
                .body(resource);
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable Long documentId) {

        documentService.deleteDocument(documentId);

        return ResponseEntity.noContent().build();
    }
}