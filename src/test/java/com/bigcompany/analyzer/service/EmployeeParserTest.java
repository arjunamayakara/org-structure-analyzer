package com.bigcompany.analyzer.service;

import com.bigcompany.analyzer.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class EmployeeParserTest {
    private EmployeeParser parser;

    @BeforeEach
    void setUp() {
        parser = new EmployeeParser();
    }

    @Test
    void shouldParseValidCsvFile(@TempDir Path tempDir) throws IOException {
        String csvContent = """
            Id,firstName,lastName,salary,managerId
            123,Joe,Doe,60000,
            124,Martin,Chekov,45000,123
            125,Bob,Ronstad,47000,123
            """;

        Path csvFile = tempDir.resolve("test.csv");
        Files.writeString(csvFile, csvContent);

        Map<String, Employee> employees = parser.parseFromFile(csvFile);

        assertEquals(3, employees.size());

        Employee ceo = employees.get("123");
        assertNotNull(ceo);
        assertEquals("Joe", ceo.getFirstName());
        assertEquals("Doe", ceo.getLastName());
        assertEquals(60000.0, ceo.getSalary());
        assertTrue(ceo.isCeo());

        Employee emp1 = employees.get("124");
        assertNotNull(emp1);
        assertEquals("Martin", emp1.getFirstName());
        assertEquals("123", emp1.getManagerId());
        assertFalse(emp1.isCeo());
    }

    @Test
    void shouldThrowExceptionForInvalidHeader(@TempDir Path tempDir) throws IOException {
        String csvContent = """
            Name,Salary,Manager
            Joe,60000,
            """;

        Path csvFile = tempDir.resolve("test.csv");
        Files.writeString(csvFile, csvContent);

        assertThrows(IllegalArgumentException.class, () ->
                parser.parseFromFile(csvFile));
    }

    @Test
    void shouldThrowExceptionForDuplicateIds(@TempDir Path tempDir) throws IOException {
        String csvContent = """
            Id,firstName,lastName,salary,managerId
            123,Joe,Doe,60000,
            123,Jane,Smith,50000,
            """;

        Path csvFile = tempDir.resolve("test.csv");
        Files.writeString(csvFile, csvContent);

        assertThrows(IllegalArgumentException.class, () ->
                parser.parseFromFile(csvFile));
    }

    @Test
    void shouldThrowExceptionForInvalidSalary(@TempDir Path tempDir) throws IOException {
        String csvContent = """
            Id,firstName,lastName,salary,managerId
            123,Joe,Doe,invalid,
            """;

        Path csvFile = tempDir.resolve("test.csv");
        Files.writeString(csvFile, csvContent);

        assertThrows(IllegalArgumentException.class, () ->
                parser.parseFromFile(csvFile));
    }

    @Test
    void shouldThrowExceptionForEmptyFile(@TempDir Path tempDir) throws IOException {
        Path csvFile = tempDir.resolve("empty.csv");
        Files.writeString(csvFile, "");

        assertThrows(IllegalArgumentException.class, () ->
                parser.parseFromFile(csvFile));
    }
}
