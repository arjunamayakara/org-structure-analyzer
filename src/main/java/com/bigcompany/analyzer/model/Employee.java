package com.bigcompany.analyzer.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Employee {
    private final String id;
    private final String firstName;
    private final String lastName;
    private final double salary;
    private final String managerId;
    private final List<Employee> subordinates;

    public Employee(String id, String firstName, String lastName, double salary, String managerId) {
        this.id = Objects.requireNonNull(id, "Employee ID cannot be null");
        this.firstName = Objects.requireNonNull(firstName, "First name cannot be null");
        this.lastName = Objects.requireNonNull(lastName, "Last name cannot be null");
        this.salary = salary;
        this.managerId = managerId; // null for CEO
        this.subordinates = new ArrayList<>();

        if (salary < 0) {
            throw new IllegalArgumentException("Salary cannot be negative");
        }
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public double getSalary() {
        return salary;
    }

    public String getManagerId() {
        return managerId;
    }

    public List<Employee> getSubordinates() {
        return Collections.unmodifiableList(subordinates);
    }

    public void addSubordinate(Employee subordinate) {
        if (subordinate != null && !subordinates.contains(subordinate)) {
            subordinates.add(subordinate);
        }
    }

    public boolean isManager() {
        return !subordinates.isEmpty();
    }

    public boolean isCeo() {
        return managerId == null;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return getFullName() + " (" + id + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(id, employee.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
