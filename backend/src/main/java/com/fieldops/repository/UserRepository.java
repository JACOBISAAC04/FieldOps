package com.fieldops.repository;

import com.fieldops.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("""
        SELECT u
        FROM User u
        WHERE u.role = 'ENGINEER'
        AND u.status = 'ACTIVE'
        AND NOT EXISTS (
            SELECT e.id
            FROM Engineer e
            WHERE e.user.id = u.id
        )
        ORDER BY u.name
        """)
    List<User> findAvailableEngineers();
}