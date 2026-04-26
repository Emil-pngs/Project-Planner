package dtu.projectplanner.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Project {
    private int projectID;
    private String name;
    private Employee leader;
    private LocalDate createdOn;
    private List<Employee> viewers = new ArrayList<>();
    private List<Activity> activities = new ArrayList<>();

    public Project(int projectID, String name) {
        this.projectID = projectID;
        this.name = name;
        this.createdOn = LocalDate.now();
    }

    public void setProjectLeader(Employee leader) {
        this.leader = leader;
        grantViewAccess(leader);
    }

    public void grantViewAccess(Employee employee) {
        if (employee == null) {
            return;
        }
        if (!containsEmployeeByInitials(viewers, employee.getInitials())) {
            viewers.add(employee);
        }
    }

    public void revokeViewAccess(Employee employee) {
        if (employee == null) {
            return;
        }
        viewers.removeIf(e -> e.getInitials().equals(employee.getInitials()));
    }

    public boolean canBeViewedBy(Employee employee) {
        if (employee == null) {
            return false;
        }
        boolean isLeader = leader != null && leader.getInitials().equals(employee.getInitials());
        return isLeader || containsEmployeeByInitials(viewers, employee.getInitials());
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

    public List<Activity> getActivities() {
        return activities;
    }

    public int getProjectID() {
        return projectID;
    }

    public String getName() {
        return name;
    }

    public Employee getProjectLeader() {
        return leader;
    }

    public LocalDate getCreatedOn() {
        return createdOn;
    }

    public List<Employee> getViewers() {
        return new ArrayList<>(viewers);
    }

    private boolean containsEmployeeByInitials(List<Employee> employees, String initials) {
        for (Employee employee : employees) {
            if (employee.getInitials().equals(initials)) {
                return true;
            }
        }
        return false;
    }
}
