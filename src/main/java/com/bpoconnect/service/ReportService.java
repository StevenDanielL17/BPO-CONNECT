package com.bpoconnect.service;

import com.bpoconnect.patterns.template.DailyTicketReport;
import com.bpoconnect.patterns.template.SupportReport;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class ReportService {

    public void generateDailyReport(String userId) {
        String reportId = "REP-" + UUID.randomUUID().toString().substring(0, 8);
        SupportReport report = new DailyTicketReport(reportId, userId);
        report.generate();
    }
}
