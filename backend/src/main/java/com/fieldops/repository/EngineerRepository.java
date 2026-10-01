package com.fieldops.repository;

import com.fieldops.entity.Engineer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EngineerRepository extends JpaRepository<Engineer, Long> {

    List<Engineer> findByLocationIgnoreCase(String location);

    List<Engineer> findByAvailabilityIgnoreCase(String availability);

    List<Engineer> findBySpecializationIgnoreCase(String specialization);
}