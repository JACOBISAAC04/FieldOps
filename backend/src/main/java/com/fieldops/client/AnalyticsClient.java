package com.fieldops.client;

import com.fieldops.dto.EquipmentResponse;
import com.fieldops.dto.EquipmentRiskResponse;
import com.fieldops.exception.AnalyticsServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class AnalyticsClient {

    private final RestClient restClient;

    public AnalyticsClient(
            RestClient.Builder restClientBuilder,
            @Value("${analytics.service.url:http://localhost:8081}") String analyticsServiceUrl) {

        this.restClient = restClientBuilder
                .baseUrl(analyticsServiceUrl)
                .build();
    }

    public EquipmentRiskResponse getEquipmentRisk(
            EquipmentResponse equipment,
            boolean highPriorityWorkOrder) {

        try {
            String uri =
                    "/api/analytics/equipment/{id}" +
                    "?status={status}" +
                    "&nextMaintenanceDate={nextMaintenanceDate}" +
                    "&highPriorityWorkOrder={highPriorityWorkOrder}";

            return restClient.get()
                    .uri(uri,
                            equipment.getId(),
                            equipment.getStatus(),
                            equipment.getNextMaintenanceDate(),
                            highPriorityWorkOrder)
                    .retrieve()
                    .body(EquipmentRiskResponse.class);

        } catch (RestClientException exception) {
            throw new AnalyticsServiceException(
                    "Analytics service is unavailable",
                    exception
            );
        }
    }
}