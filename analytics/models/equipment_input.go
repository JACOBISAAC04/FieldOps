package models

type EquipmentAnalyticsInput struct {
	ID                     int    `json:"equipmentId"`
	InstallationDate       string `json:"installationDate"`
	NextMaintenanceDate    string `json:"nextMaintenanceDate"`
	Status                 string `json:"status"`
	OpenWorkOrders         int    `json:"openWorkOrders"`
	OverdueWorkOrders      int    `json:"overdueWorkOrders"`
	HighPriorityWorkOrders int    `json:"highPriorityWorkOrders"`
	CompletedWorkOrders    int    `json:"completedWorkOrders"`
}
