package com.fieldops;

import com.fieldops.entity.Document;
import com.fieldops.entity.Equipment;
import com.fieldops.entity.WorkOrder;
import com.fieldops.repository.DocumentRepository;
import com.fieldops.repository.EquipmentRepository;
import com.fieldops.repository.WorkOrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class DocumentRepositoryIntegrationTest {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Test
    void documentCanBeSavedAndFoundByEquipment() {
        Equipment equipment = createEquipment();
        Equipment savedEquipment = equipmentRepository.save(equipment);

        Document document = new Document();
        document.setFileName("inspection-report.pdf");
        document.setStoredFileName("integration-inspection-report.pdf");
        document.setContentType("application/pdf");
        document.setFileSize(1024L);
        document.setStoragePath("test/integration-inspection-report.pdf");
        document.setDocumentType("INSPECTION");
        document.setEquipment(savedEquipment);
        document.setUploadedAt(LocalDateTime.now());

        Document savedDocument = documentRepository.save(document);

        List<Document> results =
                documentRepository.findByEquipmentIdOrderByUploadedAtDesc(savedEquipment.getId());

        assertTrue(results.stream()
                .anyMatch(item -> item.getId().equals(savedDocument.getId())));

        documentRepository.delete(savedDocument);
        equipmentRepository.delete(savedEquipment);
    }

    @Test
    void documentCanBeSavedAndFoundByWorkOrder() {
        Equipment equipment = createEquipment();
        Equipment savedEquipment = equipmentRepository.save(equipment);

        WorkOrder workOrder = new WorkOrder();
        workOrder.setEquipment(savedEquipment);
        workOrder.setPriority("MEDIUM");
        workOrder.setDescription("Document integration test work order");
        workOrder.setStatus("OPEN");
        workOrder.setCreatedAt(LocalDateTime.now());

        WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);

        Document document = new Document();
        document.setFileName("maintenance-report.pdf");
        document.setStoredFileName("integration-maintenance-report.pdf");
        document.setContentType("application/pdf");
        document.setFileSize(2048L);
        document.setStoragePath("test/integration-maintenance-report.pdf");
        document.setDocumentType("MAINTENANCE");
        document.setWorkOrder(savedWorkOrder);
        document.setUploadedAt(LocalDateTime.now());

        Document savedDocument = documentRepository.save(document);

        List<Document> results =
                documentRepository.findByWorkOrderIdOrderByUploadedAtDesc(savedWorkOrder.getId());

        assertTrue(results.stream()
                .anyMatch(item -> item.getId().equals(savedDocument.getId())));

        documentRepository.delete(savedDocument);
        workOrderRepository.delete(savedWorkOrder);
        equipmentRepository.delete(savedEquipment);
    }

    private Equipment createEquipment() {
        Equipment equipment = new Equipment();
        equipment.setName("Document Integration Equipment");
        equipment.setType("Pump");
        equipment.setLocation("Test Facility");
        equipment.setStatus("ACTIVE");
        equipment.setInstallationDate(LocalDate.of(2026, 1, 1));
        return equipment;
    }
}
