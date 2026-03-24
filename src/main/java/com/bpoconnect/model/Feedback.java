package com.bpoconnect.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_feedback")
@SuppressWarnings("unused")
public class Feedback {
    @Id
    private String feedbackId;
    private String customerId;
    private String ticketId;
    private int rating; // 1-5
    private String comments;
    private LocalDateTime feedbackDate;

    public Feedback() {}

    public Feedback(String feedbackId, String customerId, String ticketId, int rating, String comments) {
        this.feedbackId = feedbackId;
        this.customerId = customerId;
        this.ticketId = ticketId;
        this.rating = rating;
        this.comments = comments;
        this.feedbackDate = LocalDateTime.now();
    }
}


