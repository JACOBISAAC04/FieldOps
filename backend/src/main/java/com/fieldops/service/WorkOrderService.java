package com.fieldops.service;

import com.fieldops.dto.WorkOrderRequest;
import com.fieldops.dto.WorkOrderResponse;
import com.fieldops.entity.Equipment;
import com.fieldops.entity.Engineer;
import com.fieldops.entity.WorkOrder;
import com.fieldops.exception.ResourceNotFoundException;
import com.fieldops.mapper.WorkOrderMapper;
import com.fieldops.repository.EquipmentRepository;
import com.fieldops.repository.EngineerRepository;
import com.fieldops.repository.WorkOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class WorkOrderService {

    private final WorkOrderRepository workOrderRepository;
    private final EquipmentRepository equipmentRepository;
    private final EngineerRepository engineerRepository;
    private final WorkOrderMapper workOrderMapper;

    public WorkOrderService(
            WorkOrderRepository workOrderRepository,
            EquipmentRepository equipmentRepository,
            EngineerRepository engineerRepository,
            WorkOrderMapper workOrderMapper) {
        this.workOrderRepository = workOrderRepository;
        this.equipmentRepository = equipmentRepository;
        this.engineerRepository = engineerRepository;
        this.workOrderMapper = workOrderMapper;
    }

    @Transactional(readOnly = true)
    public List<WorkOrderResponse> getAllWorkOrders() {
        return workOrderRepository.findAll()
                .stream()
                .map(workOrderMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public WorkOrderResponse getWorkOrderById(Long id) {
        return workOrderMapper.toResponse(findWorkOrder(id));
    }

    public WorkOrderResponse createWorkOrder(WorkOrderRequest request) {
        Equipment equipment = findEquipment(request.getEquipmentId());
        Engineer engineer = findEngineer(request.getEngineerId());

        WorkOrder workOrder =
                workOrderMapper.toEntity(request, equipment, engineer);

        workOrder.setCreatedAt(LocalDateTime.now());

        WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);

        return workOrderMapper.toResponse(savedWorkOrder);
    }

    public WorkOrderResponse updateWorkOrder(
            Long id,
            WorkOrderRequest request) {

        WorkOrder workOrder = findWorkOrder(id);

        Equipment equipment = findEquipment(request.getEquipmentId());
        Engineer engineer = findEngineer(request.getEngineerId());

        workOrderMapper.updateEntity(
                workOrder,
                request,
                equipment,
                engineer
        );

        return workOrderMapper.toResponse(
                workOrderRepository.save(workOrder)
        );
    }

    public WorkOrderResponse assignEngineer(
                Long workOrderId,
                Long engineerId) {

        WorkOrder workOrder = findWorkOrder(workOrderId);
        Engineer engineer = findEngineer(engineerId);

        if (!"OPEN".equals(workOrder.getStatus()) &&
                !"ASSIGNED".equals(workOrder.getStatus())) {
                throw new IllegalStateException(
                        "Engineer can only be assigned to an OPEN or ASSIGNED work order"
                );
        }

        if (engineer == null) {
                throw new IllegalArgumentException(
                        "Engineer ID cannot be null"
                );
        }

        if (!"AVAILABLE".equalsIgnoreCase(engineer.getAvailability())) {
                throw new IllegalStateException(
                        "Engineer is not available for assignment"
                );
        }

        workOrder.setEngineer(engineer);
        workOrder.setStatus("ASSIGNED");

        return workOrderMapper.toResponse(
                workOrderRepository.save(workOrder)
        );
        }

    public WorkOrderResponse updateStatus(
            Long id,
            String newStatus) {

        WorkOrder workOrder = findWorkOrder(id);

        String currentStatus = workOrder.getStatus();
        String normalizedStatus = normalizeStatus(newStatus);

        validateStatus(normalizedStatus);
        validateTransition(currentStatus, normalizedStatus);

        workOrder.setStatus(normalizedStatus);

        if ("COMPLETED".equals(normalizedStatus)) {
            workOrder.setCompletedAt(LocalDateTime.now());
        }

        if ("CANCELLED".equals(normalizedStatus)) {
            workOrder.setCompletedAt(null);
        }

        return workOrderMapper.toResponse(
                workOrderRepository.save(workOrder)
        );
    }

    @Transactional(readOnly = true)
    public List<WorkOrderResponse> getByStatus(String status) {
        return workOrderRepository.findByStatusIgnoreCase(status)
                .stream()
                .map(workOrderMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<WorkOrderResponse> getByPriority(String priority) {
        return workOrderRepository.findByPriorityIgnoreCase(priority)
                .stream()
                .map(workOrderMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<WorkOrderResponse> getByEquipment(Long equipmentId) {
        return workOrderRepository.findByEquipmentId(equipmentId)
                .stream()
                .map(workOrderMapper::toResponse)
                .toList();
    }
    @Transactional(readOnly = true)
        public List<WorkOrderResponse> getMaintenanceHistory(Long equipmentId) {
        return workOrderRepository
                .findByEquipmentIdAndStatusIgnoreCaseOrderByCompletedAtDesc(
                        equipmentId,
                        "COMPLETED"
                )
                .stream()
                .map(workOrderMapper::toResponse)
                .toList();
        }

    @Transactional(readOnly = true)
    public boolean hasHighPriorityActiveWorkOrder(Long equipmentId) {
        return workOrderRepository.existsByEquipmentIdAndPriorityInAndStatusIn(
                equipmentId,
                List.of("HIGH", "CRITICAL"),
                List.of("OPEN", "ASSIGNED", "IN_PROGRESS")
        );
    }

    @Transactional(readOnly = true)
    public List<WorkOrderResponse> getByEngineer(Long engineerId) {
        return workOrderRepository.findByEngineerId(engineerId)
                .stream()
                .map(workOrderMapper::toResponse)
                .toList();
    }
    @Transactional(readOnly = true)
        public List<WorkOrderResponse> getOverdueWorkOrders() {
        return workOrderRepository.findByDueDateBeforeAndStatusIn(
                        LocalDateTime.now(),
                        List.of("OPEN", "ASSIGNED", "IN_PROGRESS")
                )
                .stream()
                .map(workOrderMapper::toResponse)
                .toList();
        }

    private String normalizeStatus(String status) {
        if (status == null) {
            throw new IllegalArgumentException(
                    "Work order status cannot be null"
            );
        }

        return status.trim().toUpperCase();
    }

    private void validateStatus(String status) {
        if (!List.of(
                "OPEN",
                "ASSIGNED",
                "IN_PROGRESS",
                "COMPLETED",
                "CANCELLED"
        ).contains(status)) {
            throw new IllegalArgumentException(
                    "Invalid work order status: " + status
            );
        }
    }

    private void validateTransition(
            String currentStatus,
            String newStatus) {

        boolean valid = switch (currentStatus) {
            case "OPEN" ->
                    "ASSIGNED".equals(newStatus)
                            || "CANCELLED".equals(newStatus);

            case "ASSIGNED" ->
                    "IN_PROGRESS".equals(newStatus)
                            || "CANCELLED".equals(newStatus);

            case "IN_PROGRESS" ->
                    "COMPLETED".equals(newStatus)
                            || "CANCELLED".equals(newStatus);

            case "COMPLETED", "CANCELLED" ->
                    false;

            default -> false;
        };

        if (!valid) {
            throw new IllegalStateException(
                    "Invalid work order transition from "
                            + currentStatus
                            + " to "
                            + newStatus
            );
        }
    }

    private WorkOrder findWorkOrder(Long id) {
        return workOrderRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Work order not found with id: " + id
                        )
                );
    }

    private Equipment findEquipment(Long id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Equipment not found with id: " + id
                        )
                );
    }

    private Engineer findEngineer(Long id) {
        if (id == null) {
            return null;
        }

        return engineerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Engineer not found with id: " + id
                        )
                );
    }
}