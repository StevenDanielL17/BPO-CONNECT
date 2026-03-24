package com.bpoconnect.repository;

import com.bpoconnect.model.SLA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SLARepository extends JpaRepository<SLA, String> {
    Optional<SLA> findFirstByPriorityLevel(String priorityLevel);
}
