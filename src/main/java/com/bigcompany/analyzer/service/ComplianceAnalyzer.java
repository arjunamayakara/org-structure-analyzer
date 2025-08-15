package com.bigcompany.analyzer.service;

import com.bigcompany.analyzer.model.Employee;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Analyzes organizational compliance for salary and hierarchy rules.
 * Business Rules:
 * - Managers should earn 20-50% more than average of direct subordinates
 * - Reporting line should not exceed 4 levels from CEO
 */
public class ComplianceAnalyzer {
    private static final double MIN_SALARY_MULTIPLIER = 1.20; // 20% more
    private static final double MAX_SALARY_MULTIPLIER = 1.50; // 50% more
    private static final int MAX_REPORTING_LEVELS = 4;

    public AnalysisResult analyze(Employee ceo) {
        if (ceo == null) {
            throw new IllegalArgumentException("CEO cannot be null");
        }

        ResultCollector collector = new ResultCollector();
        analyzeEmployee(ceo, 0, collector);
        return collector.buildResult();
    }

    private void analyzeEmployee(Employee employee, int level, ResultCollector collector) {
        // Check reporting line length (CEO is at level 0)
        if (level > MAX_REPORTING_LEVELS) {
            int excessLevels = level - MAX_REPORTING_LEVELS;
            collector.addReportingLineIssue(new ReportingLineIssue(
                    employee, level, excessLevels));
        }

        // Check salary compliance for managers only
        if (employee.isManager()) {
            analyzeSalaryCompliance(employee, collector);
        }

        // Recursively analyze subordinates
        for (Employee subordinate : employee.getSubordinates()) {
            analyzeEmployee(subordinate, level + 1, collector);
        }
    }

    private void analyzeSalaryCompliance(Employee manager, ResultCollector collector) {
        List<Employee> subordinates = manager.getSubordinates();
        double avgSubordinateSalary = subordinates.stream()
                .mapToDouble(Employee::getSalary)
                .average()
                .orElse(0.0);

        double minAllowedSalary = avgSubordinateSalary * MIN_SALARY_MULTIPLIER;
        double maxAllowedSalary = avgSubordinateSalary * MAX_SALARY_MULTIPLIER;
        double managerSalary = manager.getSalary();

        if (managerSalary < minAllowedSalary) {
            double shortfall = minAllowedSalary - managerSalary;
            collector.addUnderpaidManager(new SalaryIssue(
                    manager, managerSalary, minAllowedSalary, shortfall));
        } else if (managerSalary > maxAllowedSalary) {
            double excess = managerSalary - maxAllowedSalary;
            collector.addOverpaidManager(new SalaryIssue(
                    manager, managerSalary, maxAllowedSalary, excess));
        }
    }

    // Result classes
    public static class AnalysisResult {
        private final List<SalaryIssue> underpaidManagers;
        private final List<SalaryIssue> overpaidManagers;
        private final List<ReportingLineIssue> reportingLineIssues;

        public AnalysisResult(List<SalaryIssue> underpaidManagers,
                              List<SalaryIssue> overpaidManagers,
                              List<ReportingLineIssue> reportingLineIssues) {
            this.underpaidManagers = Collections.unmodifiableList(new ArrayList<>(underpaidManagers));
            this.overpaidManagers = Collections.unmodifiableList(new ArrayList<>(overpaidManagers));
            this.reportingLineIssues = Collections.unmodifiableList(new ArrayList<>(reportingLineIssues));
        }

        public List<SalaryIssue> getUnderpaidManagers() { return underpaidManagers; }
        public List<SalaryIssue> getOverpaidManagers() { return overpaidManagers; }
        public List<ReportingLineIssue> getReportingLineIssues() { return reportingLineIssues; }

        public boolean hasIssues() {
            return !underpaidManagers.isEmpty() || !overpaidManagers.isEmpty() || !reportingLineIssues.isEmpty();
        }
    }

    public static class SalaryIssue {
        private final Employee employee;
        private final double actualSalary;
        private final double expectedSalary;
        private final double difference;

        public SalaryIssue(Employee employee, double actualSalary, double expectedSalary, double difference) {
            this.employee = employee;
            this.actualSalary = actualSalary;
            this.expectedSalary = expectedSalary;
            this.difference = difference;
        }

        public Employee getEmployee() { return employee; }
        public double getActualSalary() { return actualSalary; }
        public double getExpectedSalary() { return expectedSalary; }
        public double getDifference() { return difference; }
    }

    public static class ReportingLineIssue {
        private final Employee employee;
        private final int actualLevel;
        private final int excessLevels;

        public ReportingLineIssue(Employee employee, int actualLevel, int excessLevels) {
            this.employee = employee;
            this.actualLevel = actualLevel;
            this.excessLevels = excessLevels;
        }

        public Employee getEmployee() { return employee; }
        public int getActualLevel() { return actualLevel; }
        public int getExcessLevels() { return excessLevels; }
    }

    private static class ResultCollector {
        private final List<SalaryIssue> underpaidManagers = new ArrayList<>();
        private final List<SalaryIssue> overpaidManagers = new ArrayList<>();
        private final List<ReportingLineIssue> reportingLineIssues = new ArrayList<>();

        public void addUnderpaidManager(SalaryIssue issue) { underpaidManagers.add(issue); }
        public void addOverpaidManager(SalaryIssue issue) { overpaidManagers.add(issue); }
        public void addReportingLineIssue(ReportingLineIssue issue) { reportingLineIssues.add(issue); }

        public AnalysisResult buildResult() {
            return new AnalysisResult(underpaidManagers, overpaidManagers, reportingLineIssues);
        }
    }
}

