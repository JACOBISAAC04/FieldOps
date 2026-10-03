package tests

import (
	"testing"
	"time"

	"fieldops-analytics/models"
	"fieldops-analytics/services"
)

func TestMaintenanceOverdue(t *testing.T) {
	input := models.EquipmentAnalyticsInput{
		ID:                  1,
		Status:              "ACTIVE",
		NextMaintenanceDate: dateString(time.Now().AddDate(0, 0, -5)),
	}

	result := services.CalculateRisk(input)

	if result.RiskScore != 25 {
		t.Fatalf("expected risk score 25, got %d", result.RiskScore)
	}

	if result.RiskLevel != "LOW" {
		t.Fatalf("expected LOW risk, got %s", result.RiskLevel)
	}

	if !result.MaintenanceDue {
		t.Fatal("expected maintenance to be due")
	}

	if result.HealthScore != 75 {
		t.Fatalf("expected health score 75, got %d", result.HealthScore)
	}
}

func TestMaintenanceDueSoon(t *testing.T) {
	input := models.EquipmentAnalyticsInput{
		ID:                  2,
		Status:              "ACTIVE",
		NextMaintenanceDate: dateString(time.Now().AddDate(0, 0, 15)),
	}

	result := services.CalculateRisk(input)

	if result.RiskScore != 15 {
		t.Fatalf("expected risk score 15, got %d", result.RiskScore)
	}

	if result.RiskLevel != "LOW" {
		t.Fatalf("expected LOW risk, got %s", result.RiskLevel)
	}

	if result.HealthScore != 85 {
		t.Fatalf("expected health score 85, got %d", result.HealthScore)
	}
}

func TestLowRiskEquipment(t *testing.T) {
	input := models.EquipmentAnalyticsInput{
		ID:                  3,
		Status:              "ACTIVE",
		NextMaintenanceDate: dateString(time.Now().AddDate(0, 3, 0)),
	}

	result := services.CalculateRisk(input)

	if result.RiskScore != 0 {
		t.Fatalf("expected risk score 0, got %d", result.RiskScore)
	}

	if result.RiskLevel != "LOW" {
		t.Fatalf("expected LOW risk, got %s", result.RiskLevel)
	}

	if result.HealthScore != 100 {
		t.Fatalf("expected health score 100, got %d", result.HealthScore)
	}
}

func TestOverdueWorkOrders(t *testing.T) {
	input := models.EquipmentAnalyticsInput{
		ID:                4,
		Status:            "ACTIVE",
		OverdueWorkOrders: 2,
	}

	result := services.CalculateRisk(input)

	if result.RiskScore != 20 {
		t.Fatalf("expected risk score 20, got %d", result.RiskScore)
	}

	if result.RiskLevel != "LOW" {
		t.Fatalf("expected LOW risk, got %s", result.RiskLevel)
	}

	if result.OverdueWorkOrders != 2 {
		t.Fatalf("expected 2 overdue work orders, got %d", result.OverdueWorkOrders)
	}
}

func TestActiveWorkOrders(t *testing.T) {
	input := models.EquipmentAnalyticsInput{
		ID:             5,
		Status:         "ACTIVE",
		OpenWorkOrders: 3,
	}

	result := services.CalculateRisk(input)

	if result.RiskScore != 15 {
		t.Fatalf("expected risk score 15, got %d", result.RiskScore)
	}

	if result.OpenWorkOrders != 3 {
		t.Fatalf("expected 3 open work orders, got %d", result.OpenWorkOrders)
	}
}

func TestHighPriorityWorkOrders(t *testing.T) {
	input := models.EquipmentAnalyticsInput{
		ID:                     6,
		Status:                 "ACTIVE",
		HighPriorityWorkOrders: 2,
	}

	result := services.CalculateRisk(input)

	if result.RiskScore != 20 {
		t.Fatalf("expected risk score 20, got %d", result.RiskScore)
	}

	if result.RiskLevel != "LOW" {
		t.Fatalf("expected LOW risk, got %s", result.RiskLevel)
	}

	if len(result.Reasons) != 1 {
		t.Fatalf("expected 1 reason, got %d", len(result.Reasons))
	}

	if result.Reasons[0] != "High-priority work orders are active" {
		t.Fatalf("unexpected reason: %s", result.Reasons[0])
	}
}

func TestWorkOrderScoresAreCapped(t *testing.T) {
	input := models.EquipmentAnalyticsInput{
		ID:                     7,
		Status:                 "ACTIVE",
		OpenWorkOrders:         10,
		OverdueWorkOrders:      10,
		HighPriorityWorkOrders: 10,
	}

	result := services.CalculateRisk(input)

	expected := 15 + 25 + 20

	if result.RiskScore != expected {
		t.Fatalf("expected risk score %d, got %d", expected, result.RiskScore)
	}
}

func TestEquipmentAge(t *testing.T) {
	input := models.EquipmentAnalyticsInput{
		ID:               8,
		Status:           "ACTIVE",
		InstallationDate: dateString(time.Now().AddDate(-10, 0, 0)),
	}

	result := services.CalculateRisk(input)

	if result.RiskScore != 15 {
		t.Fatalf("expected risk score 15, got %d", result.RiskScore)
	}
}

func TestRiskLevelThresholds(t *testing.T) {
	tests := []struct {
		name               string
		openWorkOrders     int
		overdueWorkOrders  int
		priorityWorkOrders int
		maintenanceDaysAgo int
		expectedScore      int
		expectedLevel      string
	}{
		{
			name:               "medium",
			openWorkOrders:     0,
			overdueWorkOrders:  2,
			priorityWorkOrders: 1,
			maintenanceDaysAgo: 0,
			expectedScore:      30,
			expectedLevel:      "MEDIUM",
		},
		{
			name:               "high",
			openWorkOrders:     3,
			overdueWorkOrders:  3,
			priorityWorkOrders: 2,
			maintenanceDaysAgo: 0,
			expectedScore:      60,
			expectedLevel:      "HIGH",
		},
		{
			name:               "critical",
			openWorkOrders:     3,
			overdueWorkOrders:  3,
			priorityWorkOrders: 2,
			maintenanceDaysAgo: 10,
			expectedScore:      85,
			expectedLevel:      "CRITICAL",
		},
	}

	for _, test := range tests {
		t.Run(test.name, func(t *testing.T) {
			input := models.EquipmentAnalyticsInput{
				ID:                     9,
				Status:                 "ACTIVE",
				OpenWorkOrders:         test.openWorkOrders,
				OverdueWorkOrders:      test.overdueWorkOrders,
				HighPriorityWorkOrders: test.priorityWorkOrders,
			}

			if test.maintenanceDaysAgo > 0 {
				input.NextMaintenanceDate = dateString(
					time.Now().AddDate(
						0,
						0,
						-test.maintenanceDaysAgo,
					),
				)
			}

			result := services.CalculateRisk(input)

			if result.RiskScore != test.expectedScore {
				t.Fatalf(
					"expected score %d, got %d",
					test.expectedScore,
					result.RiskScore,
				)
			}

			if result.RiskLevel != test.expectedLevel {
				t.Fatalf(
					"expected level %s, got %s",
					test.expectedLevel,
					result.RiskLevel,
				)
			}
		})
	}
}

func TestRiskScoreCannotExceed100(t *testing.T) {
	input := models.EquipmentAnalyticsInput{
		ID:                     10,
		Status:                 "DEACTIVATED",
		InstallationDate:       dateString(time.Now().AddDate(-20, 0, 0)),
		NextMaintenanceDate:    dateString(time.Now().AddDate(0, 0, -10)),
		OpenWorkOrders:         10,
		OverdueWorkOrders:      10,
		HighPriorityWorkOrders: 10,
	}

	result := services.CalculateRisk(input)

	if result.RiskScore > 100 {
		t.Fatalf("risk score exceeded 100: %d", result.RiskScore)
	}

	if result.HealthScore < 0 {
		t.Fatalf("health score went below 0: %d", result.HealthScore)
	}
}

func dateString(value time.Time) string {
	return value.Format("2006-01-02")
}
