package com.bpoconnect.service;

import com.bpoconnect.model.QualityEvaluation;
import com.bpoconnect.repository.QualityEvaluationRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.Objects;

@Service
public class QualityService {

    private final QualityEvaluationRepository qualityEvaluationRepository;
    private final AuditService auditService;

    public QualityService(QualityEvaluationRepository qualityEvaluationRepository, AuditService auditService) {
        this.qualityEvaluationRepository = qualityEvaluationRepository;
        this.auditService = auditService;
    }

    public QualityEvaluation submitEvaluation(String ticketId, String agentId, String evaluatorId, double score, String feedback) {
        QualityEvaluation evaluation = new QualityEvaluation(
                "QE-" + UUID.randomUUID().toString().substring(0, 8),
                ticketId,
                agentId,
                evaluatorId,
                score,
                feedback
        );
        if (score < 80.0) {
            evaluation.setFlaggedForCoaching(true);
            evaluation.setCoachingNotes("Auto-flagged due to low score.");
        }
        QualityEvaluation savedEvaluation = qualityEvaluationRepository.save(evaluation);
        auditService.log(evaluatorId, ticketId, "QA_SUBMITTED", "Score=" + score);
        return savedEvaluation;
    }

    public QualityEvaluation flagForCoaching(String evaluationId, String coachingNotes) {
        QualityEvaluation evaluation = qualityEvaluationRepository.findById(Objects.requireNonNull(evaluationId, "evaluationId"))
                .orElseThrow(() -> new IllegalArgumentException("Evaluation not found."));
        evaluation.setFlaggedForCoaching(true);
        evaluation.setCoachingNotes(coachingNotes);
        QualityEvaluation savedEvaluation = qualityEvaluationRepository.save(evaluation);
        auditService.log(savedEvaluation.getEvaluatorId(), savedEvaluation.getTicketId(), "QA_FLAGGED", coachingNotes);
        return savedEvaluation;
    }

    public int getAgentQAScore(String agentId) {
        List<QualityEvaluation> evaluations = getAgentEvaluations(agentId);
        if (evaluations.isEmpty()) {
            return 0;
        }

        double average = evaluations.stream().mapToDouble(QualityEvaluation::getScore).average().orElse(0.0);
        return (int) Math.round(average);
    }

    public void notifyAgentAndSupervisor() {
    }

    public List<QualityEvaluation> getAgentEvaluations(String agentId) {
        if (agentId == null || agentId.isBlank()) {
            return Collections.emptyList();
        }
        return qualityEvaluationRepository.findByAgentId(agentId);
    }
}
