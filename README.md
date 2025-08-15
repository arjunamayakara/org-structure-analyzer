# org-structure-analyzer
```markdown
# Organizational Structure Analyzer

A Java application that analyzes company organizational structures to identify compliance issues with salary ranges and reporting line lengths.

## Features

- **Salary Compliance Analysis**: Identifies managers who earn outside the acceptable range (20-50% more than their direct subordinates' average salary)
- **Hierarchy Depth Analysis**: Finds employees with reporting lines longer than 4 levels from CEO
- **Comprehensive Reporting**: Generates detailed reports with specific recommendations
- **Robust CSV Parsing**: Handles various edge cases and provides clear error messages

## Business Rules

1. **Manager Salary Range**: Managers should earn 20-50% more than the average salary of their direct subordinates
2. **Maximum Reporting Levels**: No employee should have more than 4 managers between them and the CEO
3. **Organizational Structure**: Every organization must have exactly one CEO (employee with no manager)

## Requirements

- Java 24
- Maven 3.6 or higher

## Building the Project

```bash
mvn clean install


## Running Tests

```bash
mvn test
```

## Running the Application

### Building and running JAR
```bash
mvn clean package
java -jar target/org-structure-analyzer-1.0-SNAPSHOT.jar employees_1000.csv
```

## CSV File Format

The input CSV file must follow this exact format:

```csv
Id,firstName,lastName,salary,managerId
123,Joe,Doe,60000,
124,Martin,Chekov,45000,123
125,Bob,Ronstad,47000,123
300,Alice,Hasacat,50000,124
305,Brett,Hardleaf,34000,300
```

### Format Rules:
- Header row must be exactly: `Id,firstName,lastName,salary,managerId`
- CEO is identified by empty `managerId` field
- All fields except `managerId` are required
- Salary must be a valid number (can include decimals)
- Employee IDs must be unique

## Example Output

```
=== ORGANIZATIONAL STRUCTURE ANALYSIS REPORT ===

MANAGERS EARNING LESS THAN THEY SHOULD:
(Should earn at least 20% more than average of direct subordinates)

• John Manager (124) earns $5200.00 less than minimum allowed
  Current: $50000.00, Minimum allowed: $55200.00

No overpaid managers found.

EMPLOYEES WITH REPORTING LINES TOO LONG:
(Should have at most 4 managers between them and CEO)

• Deep Employee (999) has reporting line too long by 1 level(s)
  Current level: 5, Maximum allowed: 4
```
