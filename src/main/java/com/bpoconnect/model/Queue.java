package com.bpoconnect.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ticket_queues")
@SuppressWarnings("unused")
public class Queue {
    @Id
    private String queueId;
    private String queueName;
    private String priority;

    public Queue() {}

    public Queue(String queueId, String queueName, String priority) {
        this.queueId = queueId;
        this.queueName = queueName;
        this.priority = priority;
    }
}


