package dtu.projectplanner.domain;

import java.util.ArrayList;
import java.util.List;

public class Employee {
    private String name;
    private String initials;
    private List<Activity> assignedActivities = new ArrayList<>();

    public Employee(String name, String initials) {
        if (initials == null || !initials.matches("[a-zA-Z]{1,4}")) {
            throw new IllegalArgumentException("Employee initials must be 1 to 4 letters");
        }
        this.name = name;
        this.initials = initials;
    }

    public boolean isAssignedTo(Activity activity) {
        return assignedActivities.contains(activity);
    }

    // We add an activity to the employee's assigned activities. 
    // Checks first to avoid duplicates.
    void addAssignedActivity(Activity activity) {
        if (!assignedActivities.contains(activity)) {
            assignedActivities.add(activity);
        }
    }

    void removeAssignedActivity(Activity activity) {
        assignedActivities.remove(activity);
    }

    // We keep the entity specific business rules here (Rich Domain Model)
    public boolean isAvailableFor(Activity activity) {
        if (isAssignedTo(activity)) return false;

        // We can add more requirements here

        return true;
    }

    public String getName() {
        return name;
    }

    public String getInitials() {
        return initials;
    }

    public List<Activity> getAssignedActivities() {
        return assignedActivities;
    }
}
