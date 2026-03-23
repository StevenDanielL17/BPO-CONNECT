package com.bpoconnect.patterns.template;

public class DailyTicketReport extends SupportReport {

    public DailyTicketReport(String reportId, String generatedBy) {
        super(reportId, generatedBy);
    }

    @Override
    protected void fetchData() {
        System.out.println("[DailyTicketReport] Fetching daily ticket counts...");
    }

    @Override
    protected void processMetrics() {
        System.out.println("[DailyTicketReport] Processing totalTickets and resolvedTickets metrics...");
    }

    @Override
    protected void formatOutput() {
        System.out.println("[DailyTicketReport] Formatting output columns...");
    }
}
