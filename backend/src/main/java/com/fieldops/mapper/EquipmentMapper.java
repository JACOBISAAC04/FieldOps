package com.fieldops.mapper;

import com.fieldops.dto.EquipmentRequest;
import com.fieldops.dto.EquipmentResponse;
import com.fieldops.entity.Equipment;
import org.springframework.stereotype.Component;

@Component
public class EquipmentMapper {

    public Equipment toEntity(EquipmentRequest request) {
        Equipment equipment = new Equipment();
        equipment.setName(request.getName());
        equipment.setType(request.getType());
        equipment.setLocation(request.getLocation());
        equipment.setStatus(request.getStatus());
        equipment.setInstallationDate(request.getInstallationDate());
        equipment.setNextMaintenanceDate(request.getNextMaintenanceDate());
        return equipment;
    }

    public void updateEntity(Equipment equipment, EquipmentRequest request) {
        equipment.setName(request.getName());
        equipment.setType(request.getType());
        equipment.setLocation(request.getLocation());
        equipment.setStatus(request.getStatus());
        equipment.setInstallationDate(request.getInstallationDate());
        equipment.setNextMaintenanceDate(request.getNextMaintenanceDate());
    }

    public EquipmentResponse toResponse(Equipment equipment) {
        return new EquipmentResponse(
                equipment.getId(),
                equipment.getName(),
                equipment.getType(),
                equipment.getLocation(),
                equipment.getStatus(),
                equipment.getInstallationDate(),
                equipment.getNextMaintenanceDate()
        );
    }
}