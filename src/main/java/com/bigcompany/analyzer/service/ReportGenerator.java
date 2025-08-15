package com.bigcompany.analyzer.service;

import com.bigcompany.analyzer.service.ComplianceAnalyzer.AnalysisResult;
import com.bigcompany.analyzer.service.ComplianceAnalyzer.SalaryIssue;
import com.bigcompany.analyzer.service.ComplianceAnalyzer.ReportingLineIssue;

/**
 * Generates human-readable reports from analysis results.
 */
public class ReportGenerator {

    public String generateReport(AnalysisResult result) {
        StringBuilder report = new StringBuilder();

        report.append("=== ORGANIZATIONAL STRUCTURE ANALYSIS REPORT ===\n\n");

        generateUnderpaidManagersReport(result, report);
        generateOverpaidManagersReport(result, report);
        generateReportingLineReport(result, report);

        if (!result.hasIssues()) {
            report.append("No compliance issues found. Organization structure is compliant.\n");
        }

        return report.toString();
    }

    private void generateUnderpaidManagersReport(AnalysisResult result, StringBuilder report) {
        if (result.getUnderpaidManagers().isEmpty()) {
            report.append("No underpaid managers found.\n\n");
            return;
        }

        report.append("MANAGERS EARNING LESS THAN THEY SHOULD:\n");
        report.append("(Should earn at least 20% more than average of direct subordinates)\n\n");

        for (SalaryIssue issue : result.getUnderpaidManagers()) {
            report.append(String.format("• %s earns $%.2f less than minimum allowed\n",
                    issue.getEmployee(), issue.getDifference()));
            report.append(String.format("  Current: $%.2f, Minimum allowed: $%.2f\n\n",
                    issue.getActualSalary(), issue.getExpectedSalary()));
        }
    }

    private void generateOverpaidManagersReport(AnalysisResult result, StringBuilder report) {
        if (result.getOverpaidManagers().isEmpty()) {
            report.append("No overpaid managers found.\n\n");
            return;
        }

        report.append("MANAGERS EARNING MORE THAN THEY SHOULD:\n");
        report.append("(Should earn at most 50% more than average of direct subordinates)\n\n");

        for (SalaryIssue issue : result.getOverpaidManagers()) {
            report.append(String.format("• %s earns $%.2f more than maximum allowed\n",
                    issue.getEmployee(), issue.getDifference()));
            report.append(String.format("  Current: $%.2f, Maximum allowed: $%.2f\n\n",
                    issue.getActualSalary(), issue.getExpectedSalary()));
        }
    }

    private void generateReportingLineReport(AnalysisResult result, StringBuilder report) {
        if (result.getReportingLineIssues().isEmpty()) {
            report.append("No excessive reporting line issues found.\n\n");
            return;
        }

        report.append("EMPLOYEES WITH REPORTING LINES TOO LONG:\n");
        report.append("(Should have at most 4 managers between them and CEO)\n\n");

        for (ReportingLineIssue issue : result.getReportingLineIssues()) {
            report.append(String.format("• %s has reporting line too long by %d level(s)\n",
                    issue.getEmployee(), issue.getExcessLevels()));
            report.append(String.format("  Current level: %d, Maximum allowed: 4\n\n",
                    issue.getActualLevel()));
        }
    }
}