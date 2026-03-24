package com.bpoconnect.service;

import org.springframework.stereotype.Service;
@Service public class ReportService { 
    public Object generateDailyReport(String userId){return "Report content";}
    public Object generateDailyReport(){return null;} 
    public Object generateAgentPerformanceReport(){return null;} 
    public Object generateQAReport(){return null;} 
    public void exportToCSV(){} 
}
