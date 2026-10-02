package com.fieldops.client;

import com.fieldops.dto.EquipmentResponse;
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

        EquipmentResponse equipment = new EquipmentResponse();
        equipment.setId(1L);
        equipment.setStatus("ACTIVE");
        equipment.setNextMaintenanceDate(null);

        server.expect(
                requestTo(
                        "http://localhost:8081/api/analytics/equipment/1" +
                        "?status=ACTIVE" +
                        "&nextMaintenanceDate=" +
                        "&highPriorityWorkOrder=true"
                )
        )
        .andExpect(method(org.springframework.http.HttpMethod.GET))
        .andRespond(
                withSuccess(
                        """
                        {
                            "equipmentId": 1,
                            "riskLevel": "HIGH",
                            "riskScore": 85.0,
                            "reasons": ["High priority work order"]
                        }
                        """,
                        MediaType.APPLICATION_JSON
                )
        );

        EquipmentRiskResponse result =
                analyticsClient.getEquipmentRisk(
                        equipment,
                        true
                );

        assertNotNull(result);

        server.verify();
    }

    @Test
    void getEquipmentRiskThrowsAnalyticsServiceExceptionWhenServiceFails() {

        EquipmentResponse equipment = new EquipmentResponse();
        equipment.setId(1L);
        equipment.setStatus("ACTIVE");
        equipment.setNextMaintenanceDate(null);

        server.expect(
                requestTo(
                        "http://localhost:8081/api/analytics/equipment/1" +
                        "?status=ACTIVE" +
                        "&nextMaintenanceDate=" +
                        "&highPriorityWorkOrder=false"
                )
        )
        .andExpect(method(org.springframework.http.HttpMethod.GET))
        .andRespond(
                withServerError()
        );

        AnalyticsServiceException exception =
                assertThrows(
                        AnalyticsServiceException.class,
                        () -> analyticsClient.getEquipmentRisk(
                                equipment,
                                false
                        )
                );

        assertEquals(
                "Analytics service is unavailable",
                exception.getMessage()
        );

        assertNotNull(exception.getCause());

        server.verify();
    }

    @Test
    void getEquipmentRiskSendsHighPriorityWorkOrderFlag() {

        EquipmentResponse equipment = new EquipmentResponse();
        equipment.setId(1L);
        equipment.setStatus("ACTIVE");
        equipment.setNextMaintenanceDate(null);

        server.expect(
                requestTo(
                        "http://localhost:8081/api/analytics/equipment/1" +
                        "?status=ACTIVE" +
                        "&nextMaintenanceDate=" +
                        "&highPriorityWorkOrder=true"
                )
        )
        .andExpect(method(org.springframework.http.HttpMethod.GET))
        .andRespond(
                withSuccess(
                        """
                        {
                            "equipmentId": 1,
                            "riskLevel": "HIGH",
                            "riskScore": 85.0,
                            "reasons": ["High priority work order"]
                        }
                        """,
                        MediaType.APPLICATION_JSON
                )
        );

        EquipmentRiskResponse result =
                analyticsClient.getEquipmentRisk(
                        equipment,
                        true
                );

        assertNotNull(result);
        assertEquals(1L, result.getEquipmentId());
        assertEquals("HIGH", result.getRiskLevel());
        assertNotNull(result.getReasons());
        assertTrue(
                result.getReasons()
                        .contains("High priority work order")
        );

        server.verify();
    }
}