package com.bpoconnect.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "quality_evaluations")
public class QualityEvaluation {
    @Id
    private String evalId;
    private String ticketId;
    private String agentId;
    private String evaluatorId;
    private double score;
    private String feedback;
    private LocalDateTime evaluationDate;

    public QualityEvaluation() {}

    public QualityEvaluation(String evalId, String ticketId, String agentId, String evaluatorId, double score, String feedback) {
        this.evalId = evalId;
        this.ticketId = ticketId;
        this.agentId = agentId;
        this.evaluatorId = evaluatorId;
        this.score = score;
        this.feedback = feedback;
        this.evaluationDate = LocalDateTime.now();
    }
}
