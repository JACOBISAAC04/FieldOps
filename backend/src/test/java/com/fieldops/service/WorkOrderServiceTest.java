package com.fieldops.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fieldops.dto.WorkOrderResponse;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        engineer.setAvailability("AVAILABLE");

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
        assertEquals("ASSIGNED", workOrder.getStatus());
        assertEquals(engineer, workOrder.getEngineer());
    }

    @Test
    void reassignEngineerOnAssignedWorkOrder() {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setId(1L);
        workOrder.setStatus("ASSIGNED");

        Engineer firstEngineer = new Engineer();
        firstEngineer.setId(5L);
        firstEngineer.setAvailability("AVAILABLE");

        Engineer secondEngineer = new Engineer();
        secondEngineer.setId(6L);
        secondEngineer.setAvailability("AVAILABLE");

        workOrder.setEngineer(firstEngineer);

        WorkOrderResponse response = new WorkOrderResponse();
        response.setId(1L);
        response.setEngineerId(6L);
        response.setStatus("ASSIGNED");

        when(workOrderRepository.findById(1L))
                .thenReturn(Optional.of(workOrder));

        when(engineerRepository.findById(6L))
                .thenReturn(Optional.of(secondEngineer));

        when(workOrderRepository.save(any(WorkOrder.class)))
                .thenReturn(workOrder);

        when(workOrderMapper.toResponse(workOrder))
                .thenReturn(response);

        WorkOrderResponse result =
                workOrderService.assignEngineer(1L, 6L);

        assertEquals("ASSIGNED", result.getStatus());
        assertEquals(6L, result.getEngineerId());
        assertEquals(secondEngineer, workOrder.getEngineer());
    }

    @Test
    void cannotAssignEngineerToNonOpenWorkOrder() {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setId(1L);
        workOrder.setStatus("IN_PROGRESS");

        Engineer engineer = new Engineer();
        engineer.setId(5L);
        engineer.setAvailability("AVAILABLE");

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
    void cannotAssignBusyEngineer() {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setId(1L);
        workOrder.setStatus("OPEN");

        Engineer engineer = new Engineer();
        engineer.setId(5L);
        engineer.setAvailability("BUSY");

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
    void cannotAssignUnavailableEngineer() {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setId(1L);
        workOrder.setStatus("OPEN");

        Engineer engineer = new Engineer();
        engineer.setId(5L);
        engineer.setAvailability("UNAVAILABLE");

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
        assertEquals("ASSIGNED", workOrder.getStatus());
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
        assertEquals("IN_PROGRESS", workOrder.getStatus());
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
        assertEquals("COMPLETED", workOrder.getStatus());
        assertNotNull(workOrder.getCompletedAt());
    }

    @Test
    void openToCancelledIsValid() {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setId(1L);
        workOrder.setStatus("OPEN");

        when(workOrderRepository.findById(1L))
                .thenReturn(Optional.of(workOrder));

        when(workOrderRepository.save(any(WorkOrder.class)))
                .thenReturn(workOrder);

        WorkOrderResponse response = new WorkOrderResponse();
        response.setStatus("CANCELLED");

        when(workOrderMapper.toResponse(workOrder))
                .thenReturn(response);

        WorkOrderResponse result =
                workOrderService.updateStatus(1L, "CANCELLED");

        assertEquals("CANCELLED", result.getStatus());
        assertEquals("CANCELLED", workOrder.getStatus());
        assertNull(workOrder.getCompletedAt());
    }

    @Test
    void assignedToCancelledIsValid() {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setId(1L);
        workOrder.setStatus("ASSIGNED");

        when(workOrderRepository.findById(1L))
                .thenReturn(Optional.of(workOrder));

        when(workOrderRepository.save(any(WorkOrder.class)))
                .thenReturn(workOrder);

        WorkOrderResponse response = new WorkOrderResponse();
        response.setStatus("CANCELLED");

        when(workOrderMapper.toResponse(workOrder))
                .thenReturn(response);

        WorkOrderResponse result =
                workOrderService.updateStatus(1L, "CANCELLED");

        assertEquals("CANCELLED", result.getStatus());
        assertEquals("CANCELLED", workOrder.getStatus());
    }

    @Test
    void inProgressToCancelledIsValid() {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setId(1L);
        workOrder.setStatus("IN_PROGRESS");

        when(workOrderRepository.findById(1L))
                .thenReturn(Optional.of(workOrder));

        when(workOrderRepository.save(any(WorkOrder.class)))
                .thenReturn(workOrder);

        WorkOrderResponse response = new WorkOrderResponse();
        response.setStatus("CANCELLED");

        when(workOrderMapper.toResponse(workOrder))
                .thenReturn(response);

        WorkOrderResponse result =
                workOrderService.updateStatus(1L, "CANCELLED");

        assertEquals("CANCELLED", result.getStatus());
        assertEquals("CANCELLED", workOrder.getStatus());
    }

    @Test
    void cancelledWorkOrderCannotChangeStatus() {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setId(1L);
        workOrder.setStatus("CANCELLED");

        when(workOrderRepository.findById(1L))
                .thenReturn(Optional.of(workOrder));

        assertThrows(
                IllegalStateException.class,
                () -> workOrderService.updateStatus(1L, "OPEN")
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
    void invalidStatusThrowsException() {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setId(1L);
        workOrder.setStatus("OPEN");

        when(workOrderRepository.findById(1L))
                .thenReturn(Optional.of(workOrder));

        assertThrows(
                IllegalArgumentException.class,
                () -> workOrderService.updateStatus(
                        1L,
                        "INVALID_STATUS"
                )
        );
    }

    @Test
    void nullStatusThrowsException() {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setId(1L);
        workOrder.setStatus("OPEN");

        when(workOrderRepository.findById(1L))
                .thenReturn(Optional.of(workOrder));

        assertThrows(
                IllegalArgumentException.class,
                () -> workOrderService.updateStatus(
                        1L,
                        null
                )
        );
    }

    @Test
    void statusIsNormalizedBeforeTransition() {
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
                workOrderService.updateStatus(
                        1L,
                        "  assigned  "
                );

        assertEquals("ASSIGNED", workOrder.getStatus());
        assertEquals("ASSIGNED", result.getStatus());
    }

    @Test
    void cancellingCompletedWorkOrderIsRejected() {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setId(1L);
        workOrder.setStatus("COMPLETED");

        when(workOrderRepository.findById(1L))
                .thenReturn(Optional.of(workOrder));

        assertThrows(
                IllegalStateException.class,
                () -> workOrderService.updateStatus(
                        1L,
                        "CANCELLED"
                )
        );
    }

    @Test
    void completingCancelledWorkOrderIsRejected() {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setId(1L);
        workOrder.setStatus("CANCELLED");

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
    void cancellingWorkOrderClearsCompletedAt() {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setId(1L);
        workOrder.setStatus("IN_PROGRESS");
        workOrder.setCompletedAt(
                LocalDateTime.now().minusHours(2)
        );

        when(workOrderRepository.findById(1L))
                .thenReturn(Optional.of(workOrder));

        when(workOrderRepository.save(any(WorkOrder.class)))
                .thenReturn(workOrder);

        WorkOrderResponse response = new WorkOrderResponse();
        response.setStatus("CANCELLED");

        when(workOrderMapper.toResponse(workOrder))
                .thenReturn(response);

        workOrderService.updateStatus(1L, "CANCELLED");

        assertEquals("CANCELLED", workOrder.getStatus());
        assertNull(workOrder.getCompletedAt());
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

    @Test
    void detectsHighPriorityActiveWorkOrder() {
        when(workOrderRepository.existsByEquipmentIdAndPriorityInAndStatusIn(
                eq(1L),
                eq(List.of("HIGH", "CRITICAL")),
                eq(List.of("OPEN", "ASSIGNED", "IN_PROGRESS"))
        )).thenReturn(true);

        boolean result =
                workOrderService.hasHighPriorityActiveWorkOrder(1L);

        assertEquals(true, result);
    }

    @Test
    void ignoresEquipmentWithoutHighPriorityActiveWorkOrder() {
        when(workOrderRepository.existsByEquipmentIdAndPriorityInAndStatusIn(
                eq(2L),
                eq(List.of("HIGH", "CRITICAL")),
                eq(List.of("OPEN", "ASSIGNED", "IN_PROGRESS"))
        )).thenReturn(false);

        boolean result =
                workOrderService.hasHighPriorityActiveWorkOrder(2L);

        assertFalse(result);
    }

    @Test
    void returnsOverdueActiveWorkOrders() {
        WorkOrder openWorkOrder = new WorkOrder();
        openWorkOrder.setId(1L);
        openWorkOrder.setStatus("OPEN");
        openWorkOrder.setDueDate(
                LocalDateTime.now().minusHours(2)
        );

        WorkOrder assignedWorkOrder = new WorkOrder();
        assignedWorkOrder.setId(2L);
        assignedWorkOrder.setStatus("ASSIGNED");
        assignedWorkOrder.setDueDate(
                LocalDateTime.now().minusHours(1)
        );

        WorkOrderResponse openResponse = new WorkOrderResponse();
        openResponse.setId(1L);
        openResponse.setStatus("OPEN");

        WorkOrderResponse assignedResponse = new WorkOrderResponse();
        assignedResponse.setId(2L);
        assignedResponse.setStatus("ASSIGNED");

        when(workOrderRepository.findByDueDateBeforeAndStatusIn(
                any(LocalDateTime.class),
                eq(List.of("OPEN", "ASSIGNED", "IN_PROGRESS"))
        )).thenReturn(
                List.of(openWorkOrder, assignedWorkOrder)
        );

        when(workOrderMapper.toResponse(openWorkOrder))
                .thenReturn(openResponse);

        when(workOrderMapper.toResponse(assignedWorkOrder))
                .thenReturn(assignedResponse);

        List<WorkOrderResponse> result =
                workOrderService.getOverdueWorkOrders();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }
    @Test
        void returnsMaintenanceHistoryForEquipment() {
        WorkOrder latest = new WorkOrder();
        latest.setId(10L);
        latest.setStatus("COMPLETED");
        latest.setCompletedAt(LocalDateTime.now().minusDays(1));

        WorkOrder older = new WorkOrder();
        older.setId(5L);
        older.setStatus("COMPLETED");
        older.setCompletedAt(LocalDateTime.now().minusDays(10));

        WorkOrderResponse latestResponse = new WorkOrderResponse();
        latestResponse.setId(10L);
        latestResponse.setStatus("COMPLETED");

        WorkOrderResponse olderResponse = new WorkOrderResponse();
        olderResponse.setId(5L);
        olderResponse.setStatus("COMPLETED");

        when(workOrderRepository
                .findByEquipmentIdAndStatusIgnoreCaseOrderByCompletedAtDesc(
                        eq(1L),
                        eq("COMPLETED")
                ))
                .thenReturn(List.of(latest, older));

        when(workOrderMapper.toResponse(latest))
                .thenReturn(latestResponse);

        when(workOrderMapper.toResponse(older))
                .thenReturn(olderResponse);

        List<WorkOrderResponse> result =
                workOrderService.getMaintenanceHistory(1L);

        assertEquals(2, result.size());
        assertEquals(10L, result.get(0).getId());
        assertEquals(5L, result.get(1).getId());
        assertEquals("COMPLETED", result.get(0).getStatus());
        assertEquals("COMPLETED", result.get(1).getStatus());
        }

    @Test
    void returnsOverdueInProgressWorkOrders() {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setId(3L);
        workOrder.setStatus("IN_PROGRESS");
        workOrder.setDueDate(
                LocalDateTime.now().minusDays(1)
        );

        WorkOrderResponse response = new WorkOrderResponse();
        response.setId(3L);
        response.setStatus("IN_PROGRESS");

        when(workOrderRepository.findByDueDateBeforeAndStatusIn(
                any(LocalDateTime.class),
                eq(List.of("OPEN", "ASSIGNED", "IN_PROGRESS"))
        )).thenReturn(List.of(workOrder));

        when(workOrderMapper.toResponse(workOrder))
                .thenReturn(response);

        List<WorkOrderResponse> result =
                workOrderService.getOverdueWorkOrders();

        assertEquals(1, result.size());
        assertEquals(3L, result.get(0).getId());
        assertEquals(
                "IN_PROGRESS",
                result.get(0).getStatus()
        );
    }

    @Test
    void returnsEmptyListWhenNoOverdueWorkOrdersExist() {
        when(workOrderRepository.findByDueDateBeforeAndStatusIn(
                any(LocalDateTime.class),
                eq(List.of("OPEN", "ASSIGNED", "IN_PROGRESS"))
        )).thenReturn(List.of());

        List<WorkOrderResponse> result =
                workOrderService.getOverdueWorkOrders();

        assertEquals(0, result.size());
    }

    @Test
    void overdueQueryOnlyUsesActiveStatuses() {
        when(workOrderRepository.findByDueDateBeforeAndStatusIn(
                any(LocalDateTime.class),
                eq(List.of("OPEN", "ASSIGNED", "IN_PROGRESS"))
        )).thenReturn(List.of());

        workOrderService.getOverdueWorkOrders();

        verify(workOrderRepository)
                .findByDueDateBeforeAndStatusIn(
                        any(LocalDateTime.class),
                        eq(List.of(
                                "OPEN",
                                "ASSIGNED",
                                "IN_PROGRESS"
                        ))
                );
    }
}