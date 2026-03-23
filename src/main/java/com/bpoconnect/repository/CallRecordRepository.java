package com.bpoconnect.repository;

import com.bpoconnect.model.CallRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CallRecordRepository extends JpaRepository<CallRecord, String> {
}
