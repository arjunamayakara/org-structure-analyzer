package com.bigcompany.analyzer;

import com.bigcompany.analyzer.model.Employee;
import com.bigcompany.analyzer.service.ComplianceAnalyzer;
import com.bigcompany.analyzer.service.EmployeeParser;
import com.bigcompany.analyzer.service.HierarchyBuilder;
import com.bigcompany.analyzer.service.ReportGenerator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class OrgAnalyzerApp {
    private final EmployeeParser parser;
    private final HierarchyBuilder hierarchyBuilder;
    private final ComplianceAnalyzer analyzer;
    private final ReportGenerator reportGenerator;

    public OrgAnalyzerApp() {
        this.parser = new EmployeeParser();
        this.hierarchyBuilder = new HierarchyBuilder();
        this.analyzer = new ComplianceAnalyzer();
        this.reportGenerator = new ReportGenerator();
    }

    public static void main(String[] args) {
        String filePath = args.length > 0 ? args[0] : "employees.csv";

        OrgAnalyzerApp app = new OrgAnalyzerApp();
        try {
            app.analyzeAndReport(filePath);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    public void analyzeAndReport(String filePath) throws Exception {
        System.out.println("Analyzing organizational structure from: " + filePath);
        System.out.println();

        // Parse employee data
        Path path = Paths.get(filePath);
        Map<String, Employee> employees = parser.parseFromFile(path);
        System.out.println("Loaded " + employees.size() + " employees");

        // Build hierarchy
        Employee ceo = hierarchyBuilder.buildHierarchy(employees);
        System.out.println("CEO: " + ceo);
        System.out.println();

        // Analyze compliance
        ComplianceAnalyzer.AnalysisResult result = analyzer.analyze(ceo);

        // Generate and print report
        String report = reportGenerator.generateReport(result);
        System.out.println(report);
    }
}