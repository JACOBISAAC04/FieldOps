package com.fieldops.service;

import com.fieldops.dto.EquipmentRequest;
import com.fieldops.dto.EquipmentResponse;
import com.fieldops.entity.Equipment;
import com.fieldops.exception.ResourceNotFoundException;
import com.fieldops.mapper.EquipmentMapper;
import com.fieldops.repository.EquipmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipmentServiceTest {

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private EquipmentMapper equipmentMapper;

    @InjectMocks
    private EquipmentService equipmentService;

    @Test
    void getEquipmentByIdReturnsEquipment() {
        Equipment equipment = new Equipment();
        equipment.setId(1L);

        EquipmentResponse response = new EquipmentResponse(
                1L,
                "Generator Unit C",
                "Generator",
                "Facility C",
                "OPERATIONAL",
                LocalDate.of(2025, 3, 10),
                LocalDate.of(2026, 11, 10)
        );

        when(equipmentRepository.findById(1L))
                .thenReturn(Optional.of(equipment));

        when(equipmentMapper.toResponse(equipment))
                .thenReturn(response);

        EquipmentResponse result = equipmentService.getEquipmentById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Generator Unit C", result.getName());

        verify(equipmentRepository).findById(1L);
        verify(equipmentMapper).toResponse(equipment);
    }

    @Test
    void getEquipmentByIdThrowsExceptionWhenNotFound() {
        when(equipmentRepository.findById(9999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> equipmentService.getEquipmentById(9999L)
        );

        verify(equipmentRepository).findById(9999L);
    }

    @Test
    void createEquipmentSavesEquipment() {
        EquipmentRequest request = new EquipmentRequest();
        request.setName("Generator Unit C");
        request.setType("Generator");
        request.setLocation("Facility C");
        request.setStatus("OPERATIONAL");
        request.setInstallationDate(LocalDate.of(2025, 3, 10));
        request.setNextMaintenanceDate(LocalDate.of(2026, 11, 10));

        Equipment equipment = new Equipment();

        Equipment savedEquipment = new Equipment();
        savedEquipment.setId(1L);

        EquipmentResponse response = new EquipmentResponse(
                1L,
                "Generator Unit C",
                "Generator",
                "Facility C",
                "OPERATIONAL",
                LocalDate.of(2025, 3, 10),
                LocalDate.of(2026, 11, 10)
        );

        when(equipmentMapper.toEntity(request))
                .thenReturn(equipment);

        when(equipmentRepository.save(equipment))
                .thenReturn(savedEquipment);

        when(equipmentMapper.toResponse(savedEquipment))
                .thenReturn(response);

        EquipmentResponse result = equipmentService.createEquipment(request);

        assertEquals(1L, result.getId());
        assertEquals("Generator Unit C", result.getName());

        verify(equipmentRepository).save(equipment);
    }

    @Test
    void deactivateEquipmentChangesStatus() {
        Equipment equipment = new Equipment();
        equipment.setId(1L);
        equipment.setStatus("OPERATIONAL");

        when(equipmentRepository.findById(1L))
                .thenReturn(Optional.of(equipment));

        equipmentService.deactivateEquipment(1L);

        assertEquals("DEACTIVATED", equipment.getStatus());

        verify(equipmentRepository).save(equipment);
    }

    @Test
    void getMaintenanceDueEquipmentReturnsDueEquipment() {
        Equipment equipment = new Equipment();
        equipment.setId(1L);
        equipment.setStatus("MAINTENANCE_REQUIRED");
        equipment.setNextMaintenanceDate(LocalDate.now().minusDays(1));

        EquipmentResponse response = new EquipmentResponse(
                1L,
                "Pump Unit D",
                "Pump",
                "Facility D",
                "MAINTENANCE_REQUIRED",
                LocalDate.of(2024, 5, 10),
                LocalDate.now().minusDays(1)
        );

        when(equipmentRepository.findAll())
                .thenReturn(List.of(equipment));

        when(equipmentMapper.toResponse(equipment))
                .thenReturn(response);

        List<EquipmentResponse> result =
                equipmentService.getMaintenanceDueEquipment();

        assertEquals(1, result.size());
        assertEquals("Pump Unit D", result.get(0).getName());
    }
}