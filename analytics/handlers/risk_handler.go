package handlers

import (
	"encoding/json"
	"net/http"
	"strconv"
	"strings"
	"time"

	"fieldops-analytics/models"
	"fieldops-analytics/services"
)

func EquipmentRiskHandler(w http.ResponseWriter, r *http.Request) {
	parts := strings.Split(strings.Trim(r.URL.Path, "/"), "/")

	if len(parts) != 4 || parts[0] != "api" || parts[1] != "analytics" || parts[2] != "equipment" {
		http.NotFound(w, r)
		return
	}

	id, err := strconv.Atoi(parts[3])
	if err != nil || id <= 0 {
		http.Error(w, "Invalid equipment ID", http.StatusBadRequest)
		return
	}

	query := r.URL.Query()

	status := query.Get("status")
	if status == "" {
		http.Error(w, "Missing status", http.StatusBadRequest)
		return
	}

	var nextMaintenanceDate time.Time

	if value := query.Get("nextMaintenanceDate"); value != "" {
		nextMaintenanceDate, err = time.Parse("2006-01-02", value)

		if err != nil {
			http.Error(w, "Invalid next maintenance date", http.StatusBadRequest)
			return
		}
	}

	highPriorityWorkOrder := query.Get("highPriorityWorkOrder") == "true"

	equipment := models.Equipment{
		ID:                    id,
		Status:                status,
		NextMaintenanceDate:   nextMaintenanceDate,
		HighPriorityWorkOrder: highPriorityWorkOrder,
	}

	result := services.CalculateRisk(equipment)

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)

	json.NewEncoder(w).Encode(result)
}
