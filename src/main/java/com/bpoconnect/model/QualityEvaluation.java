package com.bpoconnect.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "quality_evaluations")
@SuppressWarnings("unused")
public class QualityEvaluation {
    @Id
    private String evalId;
    private String ticketId;
    private String agentId;
    private String evaluatorId;
    private double score;
    private String feedback;
    private LocalDateTime evaluationDate;
    private boolean flaggedForCoaching;
    private String coachingNotes;

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

    public String getEvalId() {
        return evalId;
    }

    public void setEvalId(String evalId) {
        this.evalId = evalId;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getEvaluatorId() {
        return evaluatorId;
    }

    public void setEvaluatorId(String evaluatorId) {
        this.evaluatorId = evaluatorId;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public LocalDateTime getEvaluationDate() {
        return evaluationDate;
    }

    public void setEvaluationDate(LocalDateTime evaluationDate) {
        this.evaluationDate = evaluationDate;
    }

    public boolean isFlaggedForCoaching() {
        return flaggedForCoaching;
    }

    public void setFlaggedForCoaching(boolean flaggedForCoaching) {
        this.flaggedForCoaching = flaggedForCoaching;
    }

    public String getCoachingNotes() {
        return coachingNotes;
    }

    public void setCoachingNotes(String coachingNotes) {
        this.coachingNotes = coachingNotes;
    }
}


