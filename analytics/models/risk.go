package models

type RiskResponse struct {
	EquipmentID            int      `json:"equipmentId"`
	RiskScore              int      `json:"riskScore"`
	RiskLevel              string   `json:"riskLevel"`
	HealthScore            int      `json:"healthScore"`
	MaintenanceDue         bool     `json:"maintenanceDue"`
	OpenWorkOrders         int      `json:"openWorkOrders"`
	OverdueWorkOrders      int      `json:"overdueWorkOrders"`
	HighPriorityWorkOrders int      `json:"highPriorityWorkOrders"`
	CompletedWorkOrders    int      `json:"completedWorkOrders"`
	Reasons                []string `json:"reasons"`
}
