package services

import (
	"time"

	"fieldops-analytics/models"
)

func CalculateRisk(input models.EquipmentAnalyticsInput) models.RiskResponse {

	now := time.Now()

	maintenanceDue := false
	maintenanceSoon := false

	nextMaintenanceDate, validMaintenanceDate := parseDate(input.NextMaintenanceDate)

	if validMaintenanceDate {
		maintenanceDue = !nextMaintenanceDate.After(now)

		if !maintenanceDue {
			daysUntilMaintenance := int(time.Until(nextMaintenanceDate).Hours() / 24)
			maintenanceSoon = daysUntilMaintenance <= 30
		}
	}

	riskScore := 0
	reasons := []string{}

	if maintenanceDue {
		riskScore += 25
		reasons = append(reasons, "Maintenance is overdue")
	} else if maintenanceSoon {
		riskScore += 15
		reasons = append(reasons, "Maintenance is due within 30 days")
	}

	if input.OverdueWorkOrders > 0 {
		overdueScore := input.OverdueWorkOrders * 10

		if overdueScore > 25 {
			overdueScore = 25
		}

		riskScore += overdueScore
		reasons = append(
			reasons,
			"Equipment has overdue work orders",
		)
	}

	if input.OpenWorkOrders > 0 {
		activeScore := input.OpenWorkOrders * 5

		if activeScore > 15 {
			activeScore = 15
		}

		riskScore += activeScore
		reasons = append(
			reasons,
			"Equipment has active work orders",
		)
	}

	if input.HighPriorityWorkOrders > 0 {
		priorityScore := input.HighPriorityWorkOrders * 10

		if priorityScore > 20 {
			priorityScore = 20
		}

		riskScore += priorityScore
		reasons = append(
			reasons,
			"High-priority work orders are active",
		)
	}

	installationDate, validInstallationDate := parseDate(input.InstallationDate)

	if validInstallationDate {
		ageYears := int(time.Since(installationDate).Hours() / (24 * 365))

		if ageYears >= 10 {
			riskScore += 15
			reasons = append(
				reasons,
				"Equipment is 10 or more years old",
			)
		} else if ageYears >= 5 {
			riskScore += 10
			reasons = append(
				reasons,
				"Equipment is 5 or more years old",
			)
		} else if ageYears >= 3 {
			riskScore += 5
			reasons = append(
				reasons,
				"Equipment is 3 or more years old",
			)
		}
	}

	if riskScore > 100 {
		riskScore = 100
	}

	riskLevel := determineRiskLevel(riskScore)
	healthScore := 100 - riskScore

	if len(reasons) == 0 {
		reasons = append(
			reasons,
			"No significant risk factors detected",
		)
	}

	return models.RiskResponse{
		EquipmentID:            input.ID,
		RiskScore:              riskScore,
		RiskLevel:              riskLevel,
		HealthScore:            healthScore,
		MaintenanceDue:         maintenanceDue,
		OpenWorkOrders:         input.OpenWorkOrders,
		OverdueWorkOrders:      input.OverdueWorkOrders,
		HighPriorityWorkOrders: input.HighPriorityWorkOrders,
		CompletedWorkOrders:    input.CompletedWorkOrders,
		Reasons:                reasons,
	}
}

func determineRiskLevel(score int) string {

	switch {
	case score >= 80:
		return "CRITICAL"
	case score >= 60:
		return "HIGH"
	case score >= 30:
		return "MEDIUM"
	default:
		return "LOW"
	}
}

func parseDate(value string) (time.Time, bool) {
	parsed, err := time.Parse("2006-01-02", value)

	if err != nil {
		return time.Time{}, false
	}

	return parsed, true
}
