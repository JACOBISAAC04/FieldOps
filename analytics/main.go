package main

import (
	"encoding/json"
	"fmt"
	"log"
	"net/http"

	"fieldops-analytics/handlers"
)

func main() {

	http.HandleFunc(
		"/api/analytics/equipment/risk",
		handlers.EquipmentRiskHandler,
	)

	http.HandleFunc("/health", healthHandler)

	port := ":8081"

	fmt.Println(
		"FieldOps Analytics Service running on http://localhost" + port,
	)

	log.Fatal(http.ListenAndServe(port, nil))
}

func healthHandler(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(map[string]string{
		"status": "UP",
	})
}
