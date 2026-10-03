package com.fieldops.repository;

import com.fieldops.entity.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {

    List<WorkOrder> findByStatusIgnoreCase(String status);

    List<WorkOrder> findByPriorityIgnoreCase(String priority);

    List<WorkOrder> findByEquipmentId(Long equipmentId);
    List<WorkOrder> findByEquipmentIdAndStatusIgnoreCaseOrderByCompletedAtDesc(
            Long equipmentId,
            String status
    );

    List<WorkOrder> findByEngineerId(Long engineerId);

    List<WorkOrder> findByDueDateBeforeAndStatusIn(
            LocalDateTime dueDate,
            List<String> statuses
    );

    boolean existsByEquipmentIdAndPriorityInAndStatusIn(
            Long equipmentId,
            List<String> priorities,
            List<String> statuses
    );
}