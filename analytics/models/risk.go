package models

type RiskResponse struct {
	EquipmentID    int      `json:"equipmentId"`
	RiskLevel      string   `json:"riskLevel"`
	MaintenanceDue bool     `json:"maintenanceDue"`
	Reasons        []string `json:"reasons"`
}