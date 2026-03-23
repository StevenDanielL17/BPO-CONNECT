package com.bpoconnect.service;

import com.bpoconnect.model.CallRecord;
import com.bpoconnect.repository.CallRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class CallService {
    private final CallRecordRepository callRecordRepository;

    @Autowired
    public CallService(CallRecordRepository callRecordRepository) {
        this.callRecordRepository = callRecordRepository;
    }

    public CallRecord startCall(String customerId, String agentId) {
        String callId = "CALL-" + UUID.randomUUID().toString().substring(0, 8);
        CallRecord record = new CallRecord(callId, customerId, agentId);
        return callRecordRepository.save(record);
    }

    public void endCall(String callId) {
        callRecordRepository.findById(callId).ifPresent(record -> {
            record.endCall();
            callRecordRepository.save(record);
        });
    }
}
