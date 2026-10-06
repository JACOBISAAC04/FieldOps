package com.fieldops;

import com.fieldops.entity.Equipment;
import com.fieldops.repository.EquipmentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("production")
class EquipmentRepositoryIntegrationTest {

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Test
    void equipmentCanBeSavedAndRetrieved() {
        Equipment equipment = new Equipment();
        equipment.setName("Integration Test Pump");
        equipment.setType("Pump");
        equipment.setLocation("Test Facility");
        equipment.setStatus("ACTIVE");
        equipment.setInstallationDate(LocalDate.of(2026, 1, 1));
        equipment.setNextMaintenanceDate(LocalDate.of(2026, 12, 1));

        Equipment saved = equipmentRepository.save(equipment);

        assertNotNull(saved.getId());

        Equipment found = equipmentRepository.findById(saved.getId()).orElseThrow();

        assertEquals("Integration Test Pump", found.getName());
        assertEquals("Pump", found.getType());
        assertEquals("Test Facility", found.getLocation());
        assertEquals("ACTIVE", found.getStatus());

        equipmentRepository.delete(found);
    }

    @Test
    void equipmentCanBeFoundByStatus() {
        Equipment equipment = new Equipment();
        equipment.setName("Status Test Pump");
        equipment.setType("Pump");
        equipment.setLocation("Test Facility");
        equipment.setStatus("AVAILABLE");
        equipment.setInstallationDate(LocalDate.of(2026, 1, 1));

        Equipment saved = equipmentRepository.save(equipment);

        List<Equipment> results =
                equipmentRepository.findByStatusIgnoreCase("available");

        assertTrue(results.stream()
                .anyMatch(item -> item.getId().equals(saved.getId())));

        equipmentRepository.delete(saved);
    }

    @Test
    void equipmentCanBeFoundByName() {
        Equipment equipment = new Equipment();
        equipment.setName("Unique Integration Equipment");
        equipment.setType("Compressor");
        equipment.setLocation("Test Facility");
        equipment.setStatus("ACTIVE");
        equipment.setInstallationDate(LocalDate.of(2026, 1, 1));

        Equipment saved = equipmentRepository.save(equipment);

        List<Equipment> results =
                equipmentRepository.findByNameContainingIgnoreCase("unique integration");

        assertTrue(results.stream()
                .anyMatch(item -> item.getId().equals(saved.getId())));

        equipmentRepository.delete(saved);
    }
}