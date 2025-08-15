package com.bigcompany.analyzer.service;

import com.bigcompany.analyzer.model.Employee;
import com.bigcompany.analyzer.service.ComplianceAnalyzer.AnalysisResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ComplianceAnalyzerTest {
    private ComplianceAnalyzer analyzer;

    @BeforeEach
    void setUp() {
        analyzer = new ComplianceAnalyzer();
    }

    @Test
    void shouldFindNoIssuesInCompliantOrganization() {
        // CEO earns 100k
        Employee ceo = new Employee("1", "Jane", "CEO", 80000, null);

        // Manager earns 60k (30% more than avg subordinate salary of ~46k)
        Employee manager = new Employee("2", "John", "Manager", 60000, "1");
        ceo.addSubordinate(manager);

        // Subordinates earn 45k and 47k (avg = 46k)
        Employee emp1 = new Employee("3", "Alice", "Worker", 45000, "2");
        Employee emp2 = new Employee("4", "Bob", "Worker", 47000, "2");
        manager.addSubordinate(emp1);
        manager.addSubordinate(emp2);

        AnalysisResult result = analyzer.analyze(ceo);

        assertFalse(result.hasIssues());
        assertTrue(result.getUnderpaidManagers().isEmpty());
        assertTrue(result.getOverpaidManagers().isEmpty());
        assertTrue(result.getReportingLineIssues().isEmpty());
    }

    @Test
    void shouldDetectUnderpaidManager() {
        Employee ceo = new Employee("1", "Jane", "CEO", 100000, null);
        // Manager earns only 50k but should earn at least 55.2k (20% more than 46k avg)
        Employee manager = new Employee("2", "John", "Manager", 50000, "1");
        ceo.addSubordinate(manager);

        Employee emp1 = new Employee("3", "Alice", "Worker", 45000, "2");
        Employee emp2 = new Employee("4", "Bob", "Worker", 47000, "2");
        manager.addSubordinate(emp1);
        manager.addSubordinate(emp2);

        AnalysisResult result = analyzer.analyze(ceo);

        assertEquals(1, result.getUnderpaidManagers().size());
        assertEquals(manager, result.getUnderpaidManagers().getFirst().getEmployee());
        assertTrue(result.getUnderpaidManagers().getFirst().getDifference() > 5000);
    }

    @Test
    void shouldDetectOverpaidManager() {
        Employee ceo = new Employee("1", "Jane", "CEO", 100000, null);
        // Manager earns 80k but should earn at most 69k (50% more than 46k avg)
        Employee manager = new Employee("2", "John", "Manager", 80000, "1");
        ceo.addSubordinate(manager);

        Employee emp1 = new Employee("3", "Alice", "Worker", 45000, "2");
        Employee emp2 = new Employee("4", "Bob", "Worker", 47000, "2");
        manager.addSubordinate(emp1);
        manager.addSubordinate(emp2);

        AnalysisResult result = analyzer.analyze(ceo);

        assertEquals(1, result.getOverpaidManagers().size());
        assertEquals(manager, result.getOverpaidManagers().getFirst().getEmployee());
        assertTrue(result.getOverpaidManagers().getFirst().getDifference() > 10000);
    }

    @Test
    void shouldDetectLongReportingLine() {
        // Create hierarchy with 6 levels (exceeds limit of 4)
        Employee ceo = new Employee("1", "CEO", "Person", 100000, null);
        Employee l1 = new Employee("2", "L1", "Manager", 90000, "1");
        Employee l2 = new Employee("3", "L2", "Manager", 80000, "2");
        Employee l3 = new Employee("4", "L3", "Manager", 70000, "3");
        Employee l4 = new Employee("5", "L4", "Manager", 60000, "4");
        Employee l5 = new Employee("6", "L5", "Worker", 50000, "5");

        ceo.addSubordinate(l1);
        l1.addSubordinate(l2);
        l2.addSubordinate(l3);
        l3.addSubordinate(l4);
        l4.addSubordinate(l5);

        AnalysisResult result = analyzer.analyze(ceo);

        assertEquals(1, result.getReportingLineIssues().size());
        assertEquals(l5, result.getReportingLineIssues().getFirst().getEmployee());
        assertEquals(1, result.getReportingLineIssues().getFirst().getExcessLevels());
    }
}
