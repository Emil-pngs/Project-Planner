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
            // We add this activity to the employee's  assigned activities 
            // to maintain the bidirectional relationship.
            employee.addAssignedActivity(this);
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

    public ActivityStatus getStatus() {
        return status;
    }

    public void setStatus(ActivityStatus status) {
        this.status = status;
    }
}
