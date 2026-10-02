package models

type EquipmentInput struct {
	ID                    int    `json:"id"`
	Name                  string `json:"name"`
	Status                string `json:"status"`
	NextMaintenanceDate   string `json:"nextMaintenanceDate"`
	HighPriorityWorkOrder bool   `json:"highPriorityWorkOrder"`
}
