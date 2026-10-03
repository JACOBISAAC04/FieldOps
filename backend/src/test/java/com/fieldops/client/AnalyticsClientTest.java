package com.fieldops.client;

import com.fieldops.dto.EquipmentAnalyticsInput;
import com.fieldops.dto.EquipmentRiskResponse;
import com.fieldops.exception.AnalyticsServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class AnalyticsClientTest {

    private MockRestServiceServer server;
    private AnalyticsClient analyticsClient;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();

        server = MockRestServiceServer.bindTo(builder).build();

        analyticsClient =
                new AnalyticsClient(
                        builder,
                        "http://localhost:8081"
                );
    }

    @Test
    void getEquipmentRiskReturnsResponseWhenAnalyticsServiceSucceeds() {

        EquipmentAnalyticsInput input = createInput();

        server.expect(
                requestTo(
                        "http://localhost:8081/api/analytics/equipment/risk"
                )
        )
        .andExpect(method(org.springframework.http.HttpMethod.POST))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(content().json(
                """
                {
                    "equipmentId": 1,
                    "installationDate": [2024, 1, 15],
                    "nextMaintenanceDate": [2026, 10, 10],
                    "status": "ACTIVE",
                    "openWorkOrders": 4,
                    "overdueWorkOrders": 2,
                    "highPriorityWorkOrders": 1,
                    "completedWorkOrders": 12
                }
                """
        ))
        .andRespond(
                withSuccess(
                        """
                        {
                            "equipmentId": 1,
                            "riskLevel": "HIGH",
                            "riskScore": 85.0,
                            "maintenanceDue": true,
                            "healthScore": 15.0,
                            "openWorkOrders": 4,
                            "overdueWorkOrders": 2,
                            "highPriorityWorkOrders": 1,
                            "completedWorkOrders": 12,
                            "reasons": ["Overdue work orders", "High priority work order"]
                        }
                        """,
                        MediaType.APPLICATION_JSON
                )
        );

        EquipmentRiskResponse result =
                analyticsClient.getEquipmentRisk(input);

        assertNotNull(result);
        assertEquals(1L, result.getEquipmentId());
        assertEquals("HIGH", result.getRiskLevel());
        assertTrue(result.isMaintenanceDue());
        assertNotNull(result.getReasons());

        server.verify();
    }

    @Test
    void getEquipmentRiskThrowsAnalyticsServiceExceptionWhenServiceFails() {

        EquipmentAnalyticsInput input = createInput();

        server.expect(
                requestTo(
                        "http://localhost:8081/api/analytics/equipment/risk"
                )
        )
        .andExpect(method(org.springframework.http.HttpMethod.POST))
        .andRespond(
                withServerError()
        );

        AnalyticsServiceException exception =
                assertThrows(
                        AnalyticsServiceException.class,
                        () -> analyticsClient.getEquipmentRisk(input)
                );

        assertEquals(
                "Analytics service is unavailable",
                exception.getMessage()
        );

        assertNotNull(exception.getCause());

        server.verify();
    }

    @Test
    void getEquipmentRiskSendsCompleteAnalyticsInput() {

        EquipmentAnalyticsInput input = createInput();

        server.expect(
                requestTo(
                        "http://localhost:8081/api/analytics/equipment/risk"
                )
        )
        .andExpect(method(org.springframework.http.HttpMethod.POST))
        .andExpect(content().json(
                """
                {
                    "equipmentId": 1,
                    "installationDate": [2024, 1, 15],
                    "nextMaintenanceDate": [2026, 10, 10],
                    "status": "ACTIVE",
                    "openWorkOrders": 4,
                    "overdueWorkOrders": 2,
                    "highPriorityWorkOrders": 1,
                    "completedWorkOrders": 12
                }
                """
        ))
        .andRespond(
                withSuccess(
                        """
                        {
                            "equipmentId": 1,
                            "riskLevel": "HIGH",
                            "riskScore": 85.0,
                            "maintenanceDue": true,
                            "reasons": ["Overdue work orders"]
                        }
                        """,
                        MediaType.APPLICATION_JSON
                )
        );

        EquipmentRiskResponse result =
                analyticsClient.getEquipmentRisk(input);

        assertNotNull(result);
        assertEquals(1L, result.getEquipmentId());
        assertEquals("HIGH", result.getRiskLevel());
        assertTrue(result.isMaintenanceDue());
        assertNotNull(result.getReasons());

        server.verify();
    }

    private EquipmentAnalyticsInput createInput() {
        EquipmentAnalyticsInput input = new EquipmentAnalyticsInput();

        input.setEquipmentId(1L);
        input.setInstallationDate(
                java.time.LocalDate.of(2024, 1, 15)
        );
        input.setNextMaintenanceDate(
                java.time.LocalDate.of(2026, 10, 10)
        );
        input.setStatus("ACTIVE");
        input.setOpenWorkOrders(4);
        input.setOverdueWorkOrders(2);
        input.setHighPriorityWorkOrders(1);
        input.setCompletedWorkOrders(12);

        return input;
    }
}