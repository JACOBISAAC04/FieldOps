package tests

import (
	"bytes"
	"encoding/json"
	"net/http"
	"net/http/httptest"
	"testing"

	"fieldops-analytics/handlers"
	"fieldops-analytics/models"
)

func TestEquipmentRiskHandlerValidRequest(t *testing.T) {

	body := `{
		"equipmentId": 1,
		"installationDate": "2024-01-15",
		"nextMaintenanceDate": "2026-10-10",
		"status": "ACTIVE",
		"openWorkOrders": 2,
		"overdueWorkOrders": 1,
		"highPriorityWorkOrders": 1,
		"completedWorkOrders": 10
	}`

	req := httptest.NewRequest(
		http.MethodPost,
		"/api/analytics/equipment/risk",
		bytes.NewBufferString(body),
	)

	req.Header.Set("Content-Type", "application/json")

	rec := httptest.NewRecorder()

	handlers.EquipmentRiskHandler(rec, req)

	if rec.Code != http.StatusOK {
		t.Fatalf("expected status 200, got %d", rec.Code)
	}

	if rec.Header().Get("Content-Type") != "application/json" {
		t.Fatalf("expected application/json content type")
	}

	var result models.RiskResponse

	if err := json.NewDecoder(rec.Body).Decode(&result); err != nil {
		t.Fatalf("failed to decode response: %v", err)
	}

	if result.EquipmentID != 1 {
		t.Fatalf("expected equipment ID 1, got %d", result.EquipmentID)
	}

	if result.OpenWorkOrders != 2 {
		t.Fatalf("expected 2 open work orders, got %d", result.OpenWorkOrders)
	}

	if result.OverdueWorkOrders != 1 {
		t.Fatalf("expected 1 overdue work order, got %d", result.OverdueWorkOrders)
	}

	if result.HighPriorityWorkOrders != 1 {
		t.Fatalf(
			"expected 1 high-priority work order, got %d",
			result.HighPriorityWorkOrders,
		)
	}
}

func TestEquipmentRiskHandlerInvalidJSON(t *testing.T) {

	req := httptest.NewRequest(
		http.MethodPost,
		"/api/analytics/equipment/risk",
		bytes.NewBufferString(`invalid-json`),
	)

	req.Header.Set("Content-Type", "application/json")

	rec := httptest.NewRecorder()

	handlers.EquipmentRiskHandler(rec, req)

	if rec.Code != http.StatusBadRequest {
		t.Fatalf("expected status 400, got %d", rec.Code)
	}
}

func TestEquipmentRiskHandlerInvalidEquipmentID(t *testing.T) {

	body := `{
		"equipmentId": 0,
		"status": "ACTIVE"
	}`

	req := httptest.NewRequest(
		http.MethodPost,
		"/api/analytics/equipment/risk",
		bytes.NewBufferString(body),
	)

	req.Header.Set("Content-Type", "application/json")

	rec := httptest.NewRecorder()

	handlers.EquipmentRiskHandler(rec, req)

	if rec.Code != http.StatusBadRequest {
		t.Fatalf("expected status 400, got %d", rec.Code)
	}
}

func TestEquipmentRiskHandlerMissingStatus(t *testing.T) {

	body := `{
		"equipmentId": 1,
		"openWorkOrders": 2
	}`

	req := httptest.NewRequest(
		http.MethodPost,
		"/api/analytics/equipment/risk",
		bytes.NewBufferString(body),
	)

	req.Header.Set("Content-Type", "application/json")

	rec := httptest.NewRecorder()

	handlers.EquipmentRiskHandler(rec, req)

	if rec.Code != http.StatusBadRequest {
		t.Fatalf("expected status 400, got %d", rec.Code)
	}
}

func TestEquipmentRiskHandlerNegativeWorkOrderCount(t *testing.T) {

	body := `{
		"equipmentId": 1,
		"status": "ACTIVE",
		"openWorkOrders": -1
	}`

	req := httptest.NewRequest(
		http.MethodPost,
		"/api/analytics/equipment/risk",
		bytes.NewBufferString(body),
	)

	req.Header.Set("Content-Type", "application/json")

	rec := httptest.NewRecorder()

	handlers.EquipmentRiskHandler(rec, req)

	if rec.Code != http.StatusBadRequest {
		t.Fatalf("expected status 400, got %d", rec.Code)
	}
}

func TestEquipmentRiskHandlerMethodNotAllowed(t *testing.T) {

	req := httptest.NewRequest(
		http.MethodGet,
		"/api/analytics/equipment/risk",
		nil,
	)

	rec := httptest.NewRecorder()

	handlers.EquipmentRiskHandler(rec, req)

	if rec.Code != http.StatusMethodNotAllowed {
		t.Fatalf("expected status 405, got %d", rec.Code)
	}
}
