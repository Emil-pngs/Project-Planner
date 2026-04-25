package dtu.projectplanner.domain;

import java.util.ArrayList;
import java.util.List;

public class Project {
    private int projectID;
    private String name;
    private Employee leader;
    private List<Activity> activities = new ArrayList<>();

    public Project(int projectID, String name) {
        this.projectID = projectID;
        this.name = name;
    }

    public void setProjectLeader(Employee leader) {
        this.leader = leader;
    }

    public void addActivity(Activity activity) {
        if (!activities.contains(activity)) {
            activities.add(activity);
        }
    }

    public Activity findActivityByID(int activityID) {
        for (Activity activity : activities) {
            if (activity.getActivityID() == activityID) {
                return activity;
            }
        }
        // Returns null if no activity with the given ID is found
        return null; 
    }

    public void registerTime(int activityID, TimeEntry entry) {
        Activity activity = findActivityByID(activityID);
        if (activity != null) {
            activity.registerTimeEntry(entry);
        }
    }

    public int totalBudgetedHours() {
        int total = 0;
        for (Activity activity : activities) {
            total += activity.getBudgetedHours();
        }
        return total;
    }

    public int totalRegisteredHours() {
        int total = 0;
        for (Activity activity : activities) {
            total += activity.getBudgetedHours() - activity.remainingHours();
        }
        return total;
    }

    // In the project class as opposed to the planning service
    public int getRemainingWork() {
        return totalBudgetedHours() - totalRegisteredHours();
    }

    public int getProjectID() {
        return projectID;
    }

    public String getName() {
        return name;
    }
}
