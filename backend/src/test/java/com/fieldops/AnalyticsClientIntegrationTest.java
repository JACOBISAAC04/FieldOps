package com.fieldops;

import com.fieldops.client.AnalyticsClient;
import com.fieldops.dto.EquipmentAnalyticsInput;
import com.fieldops.dto.EquipmentRiskResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AnalyticsClientIntegrationTest {

    @Autowired
    private AnalyticsClient analyticsClient;

    @Test
    void analyticsClientCanCommunicateWithGoService() {
        EquipmentAnalyticsInput input = new EquipmentAnalyticsInput();
        input.setEquipmentId(1L);
        input.setInstallationDate(LocalDate.of(2026, 1, 1));
        input.setNextMaintenanceDate(LocalDate.of(2026, 12, 1));
        input.setStatus("ACTIVE");
        input.setOpenWorkOrders(2);
        input.setOverdueWorkOrders(0);
        input.setHighPriorityWorkOrders(1);
        input.setCompletedWorkOrders(5);

        EquipmentRiskResponse response =
                analyticsClient.getEquipmentRisk(input);

        assertNotNull(response);
        assertEquals(1L, response.getEquipmentId());
        assertEquals(20, response.getRiskScore());
        assertEquals("LOW", response.getRiskLevel());
        assertEquals(80, response.getHealthScore());
        assertFalse(response.isMaintenanceDue());
        assertEquals(2, response.getOpenWorkOrders());
        assertEquals(1, response.getHighPriorityWorkOrders());
        assertEquals(5, response.getCompletedWorkOrders());
        assertNotNull(response.getReasons());
        assertFalse(response.getReasons().isEmpty());
    }
}
