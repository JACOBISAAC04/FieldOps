package tests

import (
	"encoding/json"
	"net/http"
	"net/http/httptest"
	"testing"

	"fieldops-analytics/handlers"
	"fieldops-analytics/models"
)

func TestEquipmentRiskHandlerValidID(t *testing.T) {
	req := httptest.NewRequest(
		http.MethodGet,
		"/api/analytics/equipment/1?status=OPERATIONAL&nextMaintenanceDate=2026-10-17",
		nil,
	)
	rec := httptest.NewRecorder()

	handlers.EquipmentRiskHandler(rec, req)

	if rec.Code != http.StatusOK {
		t.Fatalf("expected status 200, got %d", rec.Code)
	}

	if rec.Header().Get("Content-Type") != "application/json" {
		t.Fatalf("expected application/json content type")
	}
}

func TestEquipmentRiskHandlerInvalidID(t *testing.T) {
	req := httptest.NewRequest(
		http.MethodGet,
		"/api/analytics/equipment/abc?status=OPERATIONAL",
		nil,
	)
	rec := httptest.NewRecorder()

	handlers.EquipmentRiskHandler(rec, req)

	if rec.Code != http.StatusBadRequest {
		t.Fatalf("expected status 400, got %d", rec.Code)
	}
}

func TestEquipmentRiskHandlerInvalidPath(t *testing.T) {
	req := httptest.NewRequest(
		http.MethodGet,
		"/api/wrong/path",
		nil,
	)
	rec := httptest.NewRecorder()

	handlers.EquipmentRiskHandler(rec, req)

	if rec.Code != http.StatusNotFound {
		t.Fatalf("expected status 404, got %d", rec.Code)
	}
}

func TestEquipmentRiskHandlerMissingStatus(t *testing.T) {
	req := httptest.NewRequest(
		http.MethodGet,
		"/api/analytics/equipment/1",
		nil,
	)
	rec := httptest.NewRecorder()

	handlers.EquipmentRiskHandler(rec, req)

	if rec.Code != http.StatusBadRequest {
		t.Fatalf("expected status 400, got %d", rec.Code)
	}
}
func TestEquipmentRiskHandlerHighPriorityWorkOrder(t *testing.T) {
	req := httptest.NewRequest(
		http.MethodGet,
		"/api/analytics/equipment/1?status=OPERATIONAL&nextMaintenanceDate=2026-10-17&highPriorityWorkOrder=true",
		nil,
	)

	rec := httptest.NewRecorder()

	handlers.EquipmentRiskHandler(rec, req)

	if rec.Code != http.StatusOK {
		t.Fatalf("expected status 200, got %d", rec.Code)
	}

	var result models.RiskResponse

	if err := json.NewDecoder(rec.Body).Decode(&result); err != nil {
		t.Fatalf("failed to decode response: %v", err)
	}

	if result.RiskLevel != "HIGH" {
		t.Fatalf("expected HIGH risk, got %s", result.RiskLevel)
	}

	if result.MaintenanceDue {
		t.Fatalf("expected maintenanceDue to be false")
	}

	if len(result.Reasons) != 1 {
		t.Fatalf("expected 1 reason, got %d", len(result.Reasons))
	}

	if result.Reasons[0] != "High-priority work order is active" {
		t.Fatalf("unexpected reason: %s", result.Reasons[0])
	}
}
func TestEquipmentRiskHandlerInvalidMaintenanceDate(t *testing.T) {
	req := httptest.NewRequest(
		http.MethodGet,
		"/api/analytics/equipment/1?status=OPERATIONAL&nextMaintenanceDate=invalid-date",
		nil,
	)

	rec := httptest.NewRecorder()

	handlers.EquipmentRiskHandler(rec, req)

	if rec.Code != http.StatusBadRequest {
		t.Fatalf("expected status 400, got %d", rec.Code)
	}
}
