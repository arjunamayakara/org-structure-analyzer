package com.bigcompany.analyzer.service;

import com.bigcompany.analyzer.model.Employee;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Parses CSV files containing employee data.
 * Assumptions:
 * - CSV header is always present and follows the specified format
 * - Empty managerId field indicates CEO
 * - Salary is always a valid number
 * - Employee IDs are unique
 */
public class EmployeeParser {
    private static final String DELIMITER = ",";
    private static final int EXPECTED_COLUMNS = 5;

    public Map<String, Employee> parseFromFile(Path filePath) throws IOException {
        Map<String, Employee> employees = new HashMap<>();

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IllegalArgumentException("File is empty");
            }

            validateHeader(headerLine);

            String line;
            int lineNumber = 2; // Start from line 2 (after header)

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue; // Skip empty lines
                }

                try {
                    Employee employee = parseLine(line);
                    if (employees.containsKey(employee.getId())) {
                        throw new IllegalArgumentException("Duplicate employee ID: " + employee.getId());
                    }
                    employees.put(employee.getId(), employee);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Error parsing line " + lineNumber + ": " + e.getMessage(), e);
                }
                lineNumber++;
            }
        }

        if (employees.isEmpty()) {
            throw new IllegalArgumentException("No valid employee records found");
        }

        return employees;
    }

    private void validateHeader(String headerLine) {
        String expectedHeader = "Id,firstName,lastName,salary,managerId";
        if (!expectedHeader.equals(headerLine.trim())) {
            throw new IllegalArgumentException("Invalid CSV header. Expected: " + expectedHeader);
        }
    }

    private Employee parseLine(String line) {
        String[] fields = line.split(DELIMITER, -1); // -1 to keep empty trailing fields

        if (fields.length != EXPECTED_COLUMNS) {
            throw new IllegalArgumentException("Expected " + EXPECTED_COLUMNS + " fields, got " + fields.length);
        }

        String id = fields[0].trim();
        String firstName = fields[1].trim();
        String lastName = fields[2].trim();
        String salaryStr = fields[3].trim();
        String managerId = fields[4].trim();

        if (id.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || salaryStr.isEmpty()) {
            throw new IllegalArgumentException("Required fields cannot be empty");
        }

        double salary;
        try {
            salary = Double.parseDouble(salaryStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid salary format: " + salaryStr);
        }

        // Empty managerId indicates CEO
        String managerIdOrNull = managerId.isEmpty() ? null : managerId;

        return new Employee(id, firstName, lastName, salary, managerIdOrNull);
    }
}
