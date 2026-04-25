package dtu.projectplanner.domain;

import java.util.ArrayList;
import java.util.List;

public class Employee {
    private String name;
    private String initials;
    private List<Activity> assignedActivities = new ArrayList<>();

    public Employee(String name, String initials) {
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

    public String getName() {
        return name;
    }

    public String getInitials() {
        return initials;
    }
}
