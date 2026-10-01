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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkOrderServiceTest {

    @Mock
    private WorkOrderRepository workOrderRepository;

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private EngineerRepository engineerRepository;

    @Mock
    private WorkOrderMapper workOrderMapper;

    @InjectMocks
    private WorkOrderService workOrderService;

    @Test
    void assignEngineerToOpenWorkOrder() {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setId(1L);
        workOrder.setStatus("OPEN");

        Engineer engineer = new Engineer();
        engineer.setId(5L);

        WorkOrderResponse response = new WorkOrderResponse();
        response.setId(1L);
        response.setEngineerId(5L);
        response.setStatus("ASSIGNED");

        when(workOrderRepository.findById(1L))
                .thenReturn(Optional.of(workOrder));

        when(engineerRepository.findById(5L))
                .thenReturn(Optional.of(engineer));

        when(workOrderRepository.save(any(WorkOrder.class)))
                .thenReturn(workOrder);

        when(workOrderMapper.toResponse(workOrder))
                .thenReturn(response);

        WorkOrderResponse result =
                workOrderService.assignEngineer(1L, 5L);

        assertEquals("ASSIGNED", result.getStatus());
        assertEquals(5L, result.getEngineerId());
    }

    @Test
    void cannotAssignEngineerToNonOpenWorkOrder() {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setId(1L);
        workOrder.setStatus("IN_PROGRESS");

        Engineer engineer = new Engineer();
        engineer.setId(5L);

        when(workOrderRepository.findById(1L))
                .thenReturn(Optional.of(workOrder));

        when(engineerRepository.findById(5L))
                .thenReturn(Optional.of(engineer));

        assertThrows(
                IllegalStateException.class,
                () -> workOrderService.assignEngineer(1L, 5L)
        );
    }

    @Test
    void openToAssignedIsValid() {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setId(1L);
        workOrder.setStatus("OPEN");

        when(workOrderRepository.findById(1L))
                .thenReturn(Optional.of(workOrder));

        when(workOrderRepository.save(any(WorkOrder.class)))
                .thenReturn(workOrder);

        WorkOrderResponse response = new WorkOrderResponse();
        response.setStatus("ASSIGNED");

        when(workOrderMapper.toResponse(workOrder))
                .thenReturn(response);

        WorkOrderResponse result =
                workOrderService.updateStatus(1L, "ASSIGNED");

        assertEquals("ASSIGNED", result.getStatus());
    }

    @Test
    void assignedToInProgressIsValid() {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setId(1L);
        workOrder.setStatus("ASSIGNED");

        when(workOrderRepository.findById(1L))
                .thenReturn(Optional.of(workOrder));

        when(workOrderRepository.save(any(WorkOrder.class)))
                .thenReturn(workOrder);

        WorkOrderResponse response = new WorkOrderResponse();
        response.setStatus("IN_PROGRESS");

        when(workOrderMapper.toResponse(workOrder))
                .thenReturn(response);

        WorkOrderResponse result =
                workOrderService.updateStatus(1L, "IN_PROGRESS");

        assertEquals("IN_PROGRESS", result.getStatus());
    }

    @Test
    void inProgressToCompletedSetsCompletedAt() {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setId(1L);
        workOrder.setStatus("IN_PROGRESS");

        when(workOrderRepository.findById(1L))
                .thenReturn(Optional.of(workOrder));

        when(workOrderRepository.save(any(WorkOrder.class)))
                .thenReturn(workOrder);

        WorkOrderResponse response = new WorkOrderResponse();
        response.setStatus("COMPLETED");

        when(workOrderMapper.toResponse(workOrder))
                .thenReturn(response);

        WorkOrderResponse result =
                workOrderService.updateStatus(1L, "COMPLETED");

        assertEquals("COMPLETED", result.getStatus());
        assertEquals(
                "COMPLETED",
                workOrder.getStatus()
        );
    }

    @Test
    void completedWorkOrderCannotChangeStatus() {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setId(1L);
        workOrder.setStatus("COMPLETED");

        when(workOrderRepository.findById(1L))
                .thenReturn(Optional.of(workOrder));

        assertThrows(
                IllegalStateException.class,
                () -> workOrderService.updateStatus(1L, "OPEN")
        );
    }

    @Test
    void invalidTransitionThrowsException() {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setId(1L);
        workOrder.setStatus("OPEN");

        when(workOrderRepository.findById(1L))
                .thenReturn(Optional.of(workOrder));

        assertThrows(
                IllegalStateException.class,
                () -> workOrderService.updateStatus(
                        1L,
                        "COMPLETED"
                )
        );
    }

    @Test
    void missingWorkOrderThrowsException() {
        when(workOrderRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> workOrderService.getWorkOrderById(99L)
        );
    }
}