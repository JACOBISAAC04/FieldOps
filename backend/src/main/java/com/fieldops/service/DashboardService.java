package com.fieldops.service;

import com.fieldops.dto.DashboardResponse;
import com.fieldops.entity.Equipment;
import com.fieldops.entity.Engineer;
import com.fieldops.entity.WorkOrder;
import com.fieldops.repository.EquipmentRepository;
import com.fieldops.repository.EngineerRepository;
import com.fieldops.repository.WorkOrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DashboardService {

    private final EquipmentRepository equipmentRepository;
    private final EngineerRepository engineerRepository;
    private final WorkOrderRepository workOrderRepository;

    public DashboardService(
            EquipmentRepository equipmentRepository,
            EngineerRepository engineerRepository,
            WorkOrderRepository workOrderRepository) {
        this.equipmentRepository = equipmentRepository;
        this.engineerRepository = engineerRepository;
        this.workOrderRepository = workOrderRepository;
    }

    public DashboardResponse getSummary() {
        List<Equipment> equipment = equipmentRepository.findAll();
        List<Engineer> engineers = engineerRepository.findAll();
        List<WorkOrder> workOrders = workOrderRepository.findAll();

        DashboardResponse response = new DashboardResponse();

        response.setTotalEquipment(equipment.size());

        response.setOperationalEquipment(
                equipment.stream()
                        .filter(e -> "OPERATIONAL".equalsIgnoreCase(e.getStatus()))
                        .count()
        );

        response.setMaintenanceRequiredEquipment(
                equipment.stream()
                        .filter(e -> "MAINTENANCE_REQUIRED".equalsIgnoreCase(e.getStatus()))
                        .count()
        );

        response.setDeactivatedEquipment(
                equipment.stream()
                        .filter(e -> "DEACTIVATED".equalsIgnoreCase(e.getStatus()))
                        .count()
        );

        response.setTotalWorkOrders(workOrders.size());

        response.setOpenWorkOrders(
                workOrders.stream()
                        .filter(w -> "OPEN".equalsIgnoreCase(w.getStatus()))
                        .count()
        );

        response.setAssignedWorkOrders(
                workOrders.stream()
                        .filter(w -> "ASSIGNED".equalsIgnoreCase(w.getStatus()))
                        .count()
        );

        response.setInProgressWorkOrders(
                workOrders.stream()
                        .filter(w -> "IN_PROGRESS".equalsIgnoreCase(w.getStatus()))
                        .count()
        );

        LocalDateTime now = LocalDateTime.now();

        response.setOverdueWorkOrders(
                workOrders.stream()
                        .filter(w -> w.getDueDate() != null)
                        .filter(w -> w.getDueDate().isBefore(now))
                        .filter(w ->
                                !"COMPLETED".equalsIgnoreCase(w.getStatus()) &&
                                !"CANCELLED".equalsIgnoreCase(w.getStatus()))
                        .count()
        );

        response.setHighPriorityWorkOrders(
                workOrders.stream()
                        .filter(w ->
                                "HIGH".equalsIgnoreCase(w.getPriority()) ||
                                "CRITICAL".equalsIgnoreCase(w.getPriority()))
                        .filter(w ->
                                !"COMPLETED".equalsIgnoreCase(w.getStatus()) &&
                                !"CANCELLED".equalsIgnoreCase(w.getStatus()))
                        .count()
        );

        response.setCriticalWorkOrders(
                workOrders.stream()
                        .filter(w -> "CRITICAL".equalsIgnoreCase(w.getPriority()))
                        .filter(w ->
                                !"COMPLETED".equalsIgnoreCase(w.getStatus()) &&
                                !"CANCELLED".equalsIgnoreCase(w.getStatus()))
                        .count()
        );

        response.setAvailableEngineers(
                engineers.stream()
                        .filter(e -> "AVAILABLE".equalsIgnoreCase(e.getAvailability()))
                        .count()
        );

        response.setBusyEngineers(
                engineers.stream()
                        .filter(e -> "BUSY".equalsIgnoreCase(e.getAvailability()))
                        .count()
        );

        response.setUnavailableEngineers(
                engineers.stream()
                        .filter(e -> "UNAVAILABLE".equalsIgnoreCase(e.getAvailability()))
                        .count()
        );

        LocalDate today = LocalDate.now();

        response.setMaintenanceDueEquipment(
                equipment.stream()
                        .filter(e -> e.getNextMaintenanceDate() != null)
                        .filter(e -> !e.getNextMaintenanceDate().isAfter(today))
                        .filter(e -> !"DEACTIVATED".equalsIgnoreCase(e.getStatus()))
                        .count()
        );

        return response;
    }
}