package tests

import (
	"testing"
	"time"

	"fieldops-analytics/models"
	"fieldops-analytics/services"
)

func TestMaintenanceOverdue(t *testing.T) {
	equipment := models.Equipment{
		ID:                  1,
		Name:                "Pump A",
		Status:              "ACTIVE",
		NextMaintenanceDate: time.Now().AddDate(0, 0, -5),
	}

	result := services.CalculateRisk(equipment)

	if result.RiskLevel != "HIGH" {
		t.Fatalf("expected HIGH risk, got %s", result.RiskLevel)
	}

	if !result.MaintenanceDue {
		t.Fatal("expected maintenance to be due")
	}
}

func TestInactiveEquipment(t *testing.T) {
	equipment := models.Equipment{
		ID:                  2,
		Name:                "Generator B",
		Status:              "DEACTIVATED",
		NextMaintenanceDate: time.Now().AddDate(0, 2, 0),
	}

	result := services.CalculateRisk(equipment)

	if result.RiskLevel != "HIGH" {
		t.Fatalf("expected HIGH risk, got %s", result.RiskLevel)
	}
}

func TestMaintenanceDueSoon(t *testing.T) {
	equipment := models.Equipment{
		ID:                  3,
		Name:                "Compressor C",
		Status:              "ACTIVE",
		NextMaintenanceDate: time.Now().AddDate(0, 0, 15),
	}

	result := services.CalculateRisk(equipment)

	if result.RiskLevel != "MEDIUM" {
		t.Fatalf("expected MEDIUM risk, got %s", result.RiskLevel)
	}
}

func TestLowRiskEquipment(t *testing.T) {
	equipment := models.Equipment{
		ID:                  4,
		Name:                "Pump D",
		Status:              "ACTIVE",
		NextMaintenanceDate: time.Now().AddDate(0, 3, 0),
	}

	result := services.CalculateRisk(equipment)

	if result.RiskLevel != "LOW" {
		t.Fatalf("expected LOW risk, got %s", result.RiskLevel)
	}
}
func TestHighPriorityWorkOrder(t *testing.T) {
	equipment := models.Equipment{
		ID:                    1,
		Status:                "OPERATIONAL",
		HighPriorityWorkOrder: true,
	}

	result := services.CalculateRisk(equipment)

	if result.RiskLevel != "HIGH" {
		t.Fatalf("expected HIGH risk, got %s", result.RiskLevel)
	}

	if len(result.Reasons) != 1 {
		t.Fatalf("expected 1 reason, got %d", len(result.Reasons))
	}

	if result.Reasons[0] != "High-priority work order is active" {
		t.Fatalf("unexpected reason: %s", result.Reasons[0])
	}
}
