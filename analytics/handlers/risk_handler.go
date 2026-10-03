package handlers

import (
	"encoding/json"
	"net/http"

	"fieldops-analytics/models"
	"fieldops-analytics/services"
)

func EquipmentRiskHandler(w http.ResponseWriter, r *http.Request) {

	if r.Method != http.MethodPost {
		http.Error(w, "Method not allowed", http.StatusMethodNotAllowed)
		return
	}

	var input models.EquipmentAnalyticsInput

	if err := json.NewDecoder(r.Body).Decode(&input); err != nil {
		http.Error(w, "Invalid request body", http.StatusBadRequest)
		return
	}

	if input.ID <= 0 {
		http.Error(w, "Invalid equipment ID", http.StatusBadRequest)
		return
	}

	if input.Status == "" {
		http.Error(w, "Missing equipment status", http.StatusBadRequest)
		return
	}

	if input.OpenWorkOrders < 0 ||
		input.OverdueWorkOrders < 0 ||
		input.HighPriorityWorkOrders < 0 ||
		input.CompletedWorkOrders < 0 {

		http.Error(w, "Work order counts cannot be negative", http.StatusBadRequest)
		return
	}

	result := services.CalculateRisk(input)

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)

	json.NewEncoder(w).Encode(result)
}
