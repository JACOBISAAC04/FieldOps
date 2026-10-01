package com.fieldops.mapper;

import com.fieldops.dto.WorkOrderRequest;
import com.fieldops.dto.WorkOrderResponse;
import com.fieldops.entity.Equipment;
import com.fieldops.entity.Engineer;
import com.fieldops.entity.WorkOrder;
import org.springframework.stereotype.Component;

@Component
public class WorkOrderMapper {

    public WorkOrder toEntity(
            WorkOrderRequest request,
            Equipment equipment,
            Engineer engineer) {

        WorkOrder workOrder = new WorkOrder();

        workOrder.setEquipment(equipment);
        workOrder.setEngineer(engineer);
        workOrder.setPriority(request.getPriority());
        workOrder.setDescription(request.getDescription());
        workOrder.setStatus("OPEN");
        workOrder.setDueDate(request.getDueDate());

        return workOrder;
    }

    public WorkOrderResponse toResponse(WorkOrder workOrder) {

        WorkOrderResponse response = new WorkOrderResponse();

        response.setId(workOrder.getId());
        response.setEquipmentId(workOrder.getEquipment().getId());

        if (workOrder.getEngineer() != null) {
            response.setEngineerId(workOrder.getEngineer().getId());
        }

        response.setPriority(workOrder.getPriority());
        response.setDescription(workOrder.getDescription());
        response.setStatus(workOrder.getStatus());
        response.setCreatedAt(workOrder.getCreatedAt());
        response.setDueDate(workOrder.getDueDate());
        response.setCompletedAt(workOrder.getCompletedAt());

        return response;
    }

    public void updateEntity(
            WorkOrder workOrder,
            WorkOrderRequest request,
            Equipment equipment,
            Engineer engineer) {

        workOrder.setEquipment(equipment);
        workOrder.setEngineer(engineer);
        workOrder.setPriority(request.getPriority());
        workOrder.setDescription(request.getDescription());
        workOrder.setDueDate(request.getDueDate());
    }
}