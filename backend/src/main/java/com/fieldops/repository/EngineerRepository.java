package com.fieldops.repository;

import com.fieldops.entity.Engineer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EngineerRepository extends JpaRepository<Engineer, Long> {

    @Query("SELECT e FROM Engineer e JOIN FETCH e.user")
    List<Engineer> findAllWithUser();

    @Query("SELECT e FROM Engineer e JOIN FETCH e.user WHERE e.id = :id")
    Optional<Engineer> findByIdWithUser(@Param("id") Long id);

    @Query("SELECT e FROM Engineer e JOIN FETCH e.user WHERE LOWER(e.specialization) = LOWER(:specialization)")
    List<Engineer> findBySpecializationWithUser(@Param("specialization") String specialization);

    @Query("SELECT e FROM Engineer e JOIN FETCH e.user WHERE LOWER(e.location) = LOWER(:location)")
    List<Engineer> findByLocationWithUser(@Param("location") String location);

    @Query("SELECT e FROM Engineer e JOIN FETCH e.user WHERE LOWER(e.availability) = LOWER(:availability)")
    List<Engineer> findByAvailabilityWithUser(@Param("availability") String availability);
}