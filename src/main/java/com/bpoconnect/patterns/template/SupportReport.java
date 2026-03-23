package com.bpoconnect.patterns.template;

public abstract class SupportReport {
    protected String reportId;
    protected String generatedBy;

    public SupportReport(String reportId, String generatedBy) {
        this.reportId = reportId;
        this.generatedBy = generatedBy;
    }

    // Template Method
    public final void generate() {
        fetchData();
        processMetrics();
        formatOutput();
        exportToCSV();
    }

    protected abstract void fetchData();
    protected abstract void processMetrics();
    protected abstract void formatOutput();

    private void exportToCSV() {
        System.out.println("[SupportReport] Exporting report " + reportId + " to CSV...");
    }
}
