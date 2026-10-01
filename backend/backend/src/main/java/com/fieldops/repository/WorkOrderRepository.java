package com.fieldops.repository;

import com.fieldops.entity.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {

    List<WorkOrder> findByStatusIgnoreCase(String status);

    List<WorkOrder> findByPriorityIgnoreCase(String priority);

    List<WorkOrder> findByEquipmentId(Long equipmentId);

    List<WorkOrder> findByEngineerId(Long engineerId);
}