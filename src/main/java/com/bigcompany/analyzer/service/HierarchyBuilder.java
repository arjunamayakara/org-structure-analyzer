package com.bigcompany.analyzer.service;

import com.bigcompany.analyzer.model.Employee;

import java.util.Map;

/**
 * Builds organizational hierarchy from employee data.
 */
public class HierarchyBuilder {

    public Employee buildHierarchy(Map<String, Employee> employees) {
        if (employees.isEmpty()) {
            throw new IllegalArgumentException("Cannot build hierarchy from empty employee map");
        }

        Employee ceo = null;

        // Build relationships and find CEO
        for (Employee employee : employees.values()) {
            if (employee.isCeo()) {
                if (ceo != null) {
                    throw new IllegalStateException("Multiple CEOs found: " + ceo + " and " + employee);
                }
                ceo = employee;
            } else {
                Employee manager = employees.get(employee.getManagerId());
                if (manager == null) {
                    throw new IllegalArgumentException("Manager not found for employee " + employee +
                            ". Manager ID: " + employee.getManagerId());
                }
                manager.addSubordinate(employee);
            }
        }

        if (ceo == null) {
            throw new IllegalStateException("No CEO found in organization");
        }

        return ceo;
    }
}
