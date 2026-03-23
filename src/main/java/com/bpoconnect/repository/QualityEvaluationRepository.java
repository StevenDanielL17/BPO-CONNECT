package com.bpoconnect.repository;

import com.bpoconnect.model.QualityEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QualityEvaluationRepository extends JpaRepository<QualityEvaluation, String> {
    List<QualityEvaluation> findByAgentId(String agentId);
}
