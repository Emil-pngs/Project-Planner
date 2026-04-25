package dtu.projectplanner.repository;

import dtu.projectplanner.domain.Employee;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepository {
    // The database of employees
    private List<Employee> employees = new ArrayList<>();

    public void save(Employee employee) {
        if (!employees.contains(employee)) {
            employees.add(employee);
        }
    }

    public Employee findByInitials(String initials) {
        for (Employee employee : employees) {
            if (employee.getInitials().equals(initials)) {
                return employee;
            }
        }
        // Return null if no employee with the given initials is found
        return null;
    }

    public List<Employee> findAll() {
        return employees;
    }
    
}
