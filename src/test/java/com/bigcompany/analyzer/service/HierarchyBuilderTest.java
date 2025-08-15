package com.bigcompany.analyzer.service;

import com.bigcompany.analyzer.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class HierarchyBuilderTest {
    private HierarchyBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new HierarchyBuilder();
    }

    @Test
    void shouldBuildValidHierarchy() {
        Map<String, Employee> employees = new HashMap<>();
        employees.put("1", new Employee("1", "Jane", "CEO", 100000, null));
        employees.put("2", new Employee("2", "John", "Manager", 60000, "1"));
        employees.put("3", new Employee("3", "Alice", "Worker", 45000, "2"));

        Employee ceo = builder.buildHierarchy(employees);

        assertEquals("1", ceo.getId());
        assertTrue(ceo.isCeo());
        assertEquals(1, ceo.getSubordinates().size());

        Employee manager = ceo.getSubordinates().getFirst();
        assertEquals("2", manager.getId());
        assertEquals(1, manager.getSubordinates().size());
    }

    @Test
    void shouldThrowExceptionForMultipleCeos() {
        Map<String, Employee> employees = new HashMap<>();
        employees.put("1", new Employee("1", "Jane", "CEO1", 100000, null));
        employees.put("2", new Employee("2", "John", "CEO2", 100000, null));

        assertThrows(IllegalStateException.class, () ->
                builder.buildHierarchy(employees));
    }

    @Test
    void shouldThrowExceptionForNoCeo() {
        Map<String, Employee> employees = new HashMap<>();
        employees.put("1", new Employee("1", "Jane", "Worker", 50000, "2"));

        assertThrows(IllegalArgumentException.class, () ->
                builder.buildHierarchy(employees));
    }

    @Test
    void shouldThrowExceptionForMissingManager() {
        Map<String, Employee> employees = new HashMap<>();
        employees.put("1", new Employee("1", "Jane", "CEO", 100000, null));
        employees.put("2", new Employee("2", "John", "Worker", 50000, "999")); // Manager 999 doesn't exist

        assertThrows(IllegalArgumentException.class, () ->
                builder.buildHierarchy(employees));
    }

    @Test
    void shouldThrowExceptionForEmptyEmployeeMap() {
        Map<String, Employee> employees = new HashMap<>();

        assertThrows(IllegalArgumentException.class, () ->
                builder.buildHierarchy(employees));
    }
}
