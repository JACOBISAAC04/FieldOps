package main

import (
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

	port := ":8081"

	fmt.Println(
		"FieldOps Analytics Service running on http://localhost" + port,
	)

	log.Fatal(http.ListenAndServe(port, nil))
}
