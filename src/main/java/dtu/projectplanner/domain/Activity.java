package dtu.projectplanner.domain;

import java.util.List;
import java.util.ArrayList;

public class Activity {
    private int activityID;
    private String name;
    private int budgetedHours;
    private int startWeek;
    private int endWeek;
    
    private ActivityStatus status = ActivityStatus.PLANNED;
    private List<Employee> assignedEmployees = new ArrayList<>();
    private List<TimeEntry> timeEntries = new ArrayList<>();
    
    public Activity(int activityID, String name, int budgetedHours, int startWeek, int endWeek) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Activity name is required");
        }
        if (budgetedHours < 0) {
            throw new IllegalArgumentException("Budgeted hours cannot be negative");
        }
        if (startWeek < 1 || startWeek > 53) {
            throw new IllegalArgumentException("Start week must be between 1 and 53");
        }
        if (endWeek < 1 || endWeek > 53) {
            throw new IllegalArgumentException("End week must be between 1 and 53");
        }
        if (endWeek < startWeek) {
            throw new IllegalArgumentException("End week cannot be before start week");
        }
        this.activityID = activityID;
        this.name = name;
        this.budgetedHours = budgetedHours;
        this.startWeek = startWeek;
        this.endWeek = endWeek;
    }

    public void assignEmployee(Employee employee) {
        // We ensure the relationship is maintained in both directions: 
        // the activity knows about the employee, and the employee knows about the activity.
        if (!assignedEmployees.contains(employee)) {
            assignedEmployees.add(employee);
            // We add this activity to the employee's assigned activities 
            // to maintain the bidirectional relationship.
            employee.addAssignedActivity(this);
        }
    }

    public void unassignEmployee(Employee employee) {
        if (assignedEmployees.remove(employee)) {
            employee.removeAssignedActivity(this);
        }
    }

    public void registerTimeEntry(TimeEntry timeEntry) {
        timeEntries.add(timeEntry);
    }

    public int getBudgetedHours() {
        return budgetedHours;
    }

    public int remainingHours() {
        int registeredHours = 0;

        // We sum up all the hours from the time entries to calculate the total registered hours.
        for (TimeEntry entry : timeEntries) {
            registeredHours += entry.getHours();
        }

        return budgetedHours - registeredHours;
    }

    public int getActivityID() {
        return activityID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Activity name is required");
        }
        this.name = name;
    }

    public int getStartWeek() {
        return startWeek;
    }

    public void setStartWeek(int startWeek) {
        if (startWeek < 1 || startWeek > 53) {
            throw new IllegalArgumentException("Start week must be between 1 and 53");
        }
        if (endWeek < startWeek) {
            throw new IllegalArgumentException("Start week cannot be after end week");
        }
        this.startWeek = startWeek;
    }

    public int getEndWeek() {
        return endWeek;
    }

    public void setEndWeek(int endWeek) {
        if (endWeek < 1 || endWeek > 53) {
            throw new IllegalArgumentException("End week must be between 1 and 53");
        }
        if (endWeek < startWeek) {
            throw new IllegalArgumentException("End week cannot be before start week");
        }
        this.endWeek = endWeek;
    }

    public void setBudgetedHours(int budgetedHours) {
        if (budgetedHours < 0) {
            throw new IllegalArgumentException("Budgeted hours cannot be negative");
        }
        this.budgetedHours = budgetedHours;
    }

    public ActivityStatus getStatus() {
        return status;
    }

    public void setStatus(ActivityStatus status) {
        this.status = status;
    }
}
