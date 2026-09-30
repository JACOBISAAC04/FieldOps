package com.fieldops.repository;

import com.fieldops.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    List<Equipment> findByStatusIgnoreCase(String status);

    List<Equipment> findByLocationIgnoreCase(String location);

    List<Equipment> findByTypeIgnoreCase(String type);

    List<Equipment> findByNameContainingIgnoreCase(String name);
}