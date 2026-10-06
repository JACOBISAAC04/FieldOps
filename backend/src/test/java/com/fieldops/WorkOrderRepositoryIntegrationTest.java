package com.fieldops;

import com.fieldops.entity.Equipment;
import com.fieldops.entity.WorkOrder;
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
@ActiveProfiles("production")
class WorkOrderRepositoryIntegrationTest {

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Test
    void workOrderCanBeSavedAndRetrievedWithEquipment() {
        Equipment equipment = new Equipment();
        equipment.setName("Work Order Integration Equipment");
        equipment.setType("Pump");
        equipment.setLocation("Test Facility");
        equipment.setStatus("ACTIVE");
        equipment.setInstallationDate(LocalDate.of(2026, 1, 1));

        Equipment savedEquipment = equipmentRepository.save(equipment);

        WorkOrder workOrder = new WorkOrder();
        workOrder.setEquipment(savedEquipment);
        workOrder.setPriority("HIGH");
        workOrder.setDescription("Integration test work order");
        workOrder.setStatus("OPEN");
        workOrder.setCreatedAt(LocalDateTime.now());
        workOrder.setDueDate(LocalDateTime.now().plusDays(7));

        WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);

        assertNotNull(savedWorkOrder.getId());

        WorkOrder found = workOrderRepository
                .findById(savedWorkOrder.getId())
                .orElseThrow();

        assertEquals("HIGH", found.getPriority());
        assertEquals("Integration test work order", found.getDescription());
        assertEquals("OPEN", found.getStatus());
        assertEquals(savedEquipment.getId(), found.getEquipment().getId());

        workOrderRepository.delete(found);
        equipmentRepository.delete(savedEquipment);
    }

    @Test
    void workOrdersCanBeFoundByEquipmentId() {
        Equipment equipment = new Equipment();
        equipment.setName("Equipment Query Test");
        equipment.setType("Compressor");
        equipment.setLocation("Test Facility");
        equipment.setStatus("ACTIVE");
        equipment.setInstallationDate(LocalDate.of(2026, 1, 1));

        Equipment savedEquipment = equipmentRepository.save(equipment);

        WorkOrder workOrder = new WorkOrder();
        workOrder.setEquipment(savedEquipment);
        workOrder.setPriority("MEDIUM");
        workOrder.setDescription("Equipment query integration test");
        workOrder.setStatus("OPEN");
        workOrder.setCreatedAt(LocalDateTime.now());

        WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);

        List<WorkOrder> results =
                workOrderRepository.findByEquipmentId(savedEquipment.getId());

        assertTrue(results.stream()
                .anyMatch(item -> item.getId().equals(savedWorkOrder.getId())));

        workOrderRepository.delete(savedWorkOrder);
        equipmentRepository.delete(savedEquipment);
    }
}