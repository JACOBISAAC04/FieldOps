package com.fieldops.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "equipment")
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 100)
    private String type;

    @Column(nullable = false, length = 150)
    private String location;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(name = "installation_date", nullable = false)
    private LocalDate installationDate;

    @Column(name = "next_maintenance_date")
    private LocalDate nextMaintenanceDate;

    public Equipment() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getLocation() {
        return location;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getInstallationDate() {
        return installationDate;
    }

    public LocalDate getNextMaintenanceDate() {
        return nextMaintenanceDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setInstallationDate(LocalDate installationDate) {
        this.installationDate = installationDate;
    }

    public void setNextMaintenanceDate(LocalDate nextMaintenanceDate) {
        this.nextMaintenanceDate = nextMaintenanceDate;
    }
}