package com.bpoconnect.service;

import com.bpoconnect.model.QualityEvaluation;
import com.bpoconnect.repository.QualityEvaluationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class QualityService {
    private final QualityEvaluationRepository qualityRepository;

    @Autowired
    public QualityService(QualityEvaluationRepository qualityRepository) {
        this.qualityRepository = qualityRepository;
    }

    public QualityEvaluation evaluateAgent(String ticketId, String agentId, String evaluatorId, double score, String feedback) {
        String evalId = "EV-" + UUID.randomUUID().toString().substring(0, 8);
        QualityEvaluation eval = new QualityEvaluation(evalId, ticketId, agentId, evaluatorId, score, feedback);
        return qualityRepository.save(eval);
    }

    public List<QualityEvaluation> getAgentEvaluations(String agentId) {
        return qualityRepository.findByAgentId(agentId);
    }
}
