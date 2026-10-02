package services

import (
	"time"

	"fieldops-analytics/models"
)

func CalculateRisk(equipment models.Equipment) models.RiskResponse {
	now := time.Now()
	maintenanceDue := false

	if !equipment.NextMaintenanceDate.IsZero() {
		maintenanceDue = !equipment.NextMaintenanceDate.After(now)
	}

	reasons := []string{}
	riskLevel := "LOW"

	if maintenanceDue {
		reasons = append(reasons, "Maintenance overdue")
		riskLevel = "HIGH"
	}

	if equipment.Status == "DEACTIVATED" {
		reasons = append(reasons, "Equipment status requires attention")
		riskLevel = "HIGH"
	}

	if equipment.HighPriorityWorkOrder {
		reasons = append(reasons, "High-priority work order is active")
		riskLevel = "HIGH"
	}

	if riskLevel == "LOW" && !equipment.NextMaintenanceDate.IsZero() {
		daysUntilMaintenance := int(time.Until(equipment.NextMaintenanceDate).Hours() / 24)

		if daysUntilMaintenance <= 30 {
			riskLevel = "MEDIUM"
			reasons = append(reasons, "Maintenance due soon")
		}
	}

	return models.RiskResponse{
		EquipmentID:    equipment.ID,
		RiskLevel:      riskLevel,
		MaintenanceDue: maintenanceDue,
		Reasons:        reasons,
	}
}
