package com.bpoconnect.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "call_records")
@SuppressWarnings("unused")
public class CallRecord {
    @Id
    private String callId;
    private String customerId;
    private String agentId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String recordingUrl;
    private String transcription;

    public CallRecord() {}

    public CallRecord(String callId, String customerId, String agentId) {
        this.callId = callId;
        this.customerId = customerId;
        this.agentId = agentId;
        this.startTime = LocalDateTime.now();
    }

    public void endCall() {
        this.endTime = LocalDateTime.now();
    }

    public String getCallId() { return callId; }
}


