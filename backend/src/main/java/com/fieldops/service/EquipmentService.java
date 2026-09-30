package com.fieldops.service;

import com.fieldops.dto.EquipmentRequest;
import com.fieldops.dto.EquipmentResponse;
import com.fieldops.entity.Equipment;
import com.fieldops.exception.ResourceNotFoundException;
import com.fieldops.mapper.EquipmentMapper;
import com.fieldops.repository.EquipmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final EquipmentMapper equipmentMapper;

    public EquipmentService(
            EquipmentRepository equipmentRepository,
            EquipmentMapper equipmentMapper) {
        this.equipmentRepository = equipmentRepository;
        this.equipmentMapper = equipmentMapper;
    }

    @Transactional(readOnly = true)
    public List<EquipmentResponse> getAllEquipment() {
        return equipmentRepository.findAll()
                .stream()
                .map(equipmentMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EquipmentResponse getEquipmentById(Long id) {
        Equipment equipment = findEquipment(id);
        return equipmentMapper.toResponse(equipment);
    }

    public EquipmentResponse createEquipment(EquipmentRequest request) {
        Equipment equipment = equipmentMapper.toEntity(request);
        Equipment savedEquipment = equipmentRepository.save(equipment);
        return equipmentMapper.toResponse(savedEquipment);
    }

    public EquipmentResponse updateEquipment(Long id, EquipmentRequest request) {
        Equipment equipment = findEquipment(id);
        equipmentMapper.updateEntity(equipment, request);
        Equipment updatedEquipment = equipmentRepository.save(equipment);
        return equipmentMapper.toResponse(updatedEquipment);
    }

    public void deactivateEquipment(Long id) {
        Equipment equipment = findEquipment(id);
        equipment.setStatus("DEACTIVATED");
        equipmentRepository.save(equipment);
    }

    @Transactional(readOnly = true)
    public List<EquipmentResponse> searchEquipment(
            String status,
            String location,
            String type,
            String name) {

        List<Equipment> equipment;

        if (status != null && !status.isBlank()) {
            equipment = equipmentRepository.findByStatusIgnoreCase(status);
        } else if (location != null && !location.isBlank()) {
            equipment = equipmentRepository.findByLocationIgnoreCase(location);
        } else if (type != null && !type.isBlank()) {
            equipment = equipmentRepository.findByTypeIgnoreCase(type);
        } else if (name != null && !name.isBlank()) {
            equipment = equipmentRepository.findByNameContainingIgnoreCase(name);
        } else {
            equipment = equipmentRepository.findAll();
        }

        return equipment.stream()
                .map(equipmentMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EquipmentResponse> getMaintenanceDueEquipment() {
        return equipmentRepository.findAll()
                .stream()
                .filter(equipment ->
                        equipment.getNextMaintenanceDate() != null &&
                        !equipment.getNextMaintenanceDate().isAfter(LocalDate.now()) &&
                        !"DEACTIVATED".equalsIgnoreCase(equipment.getStatus()))
                .map(equipmentMapper::toResponse)
                .toList();
    }

    private Equipment findEquipment(Long id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Equipment not found with id: " + id
                        ));
    }
}