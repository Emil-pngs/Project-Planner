package dtu.projectplanner.app;

import dtu.projectplanner.domain.*;
import dtu.projectplanner.repository.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProjectPlanningService {

    private ProjectRepository projectRepo;
    private EmployeeRepository employeeRepo;

    private int nextProjectID = 260001;
    private int nextActivityID = 1;

    public ProjectPlanningService(ProjectRepository projectRepo, EmployeeRepository employeeRepo) {
        this.projectRepo = projectRepo;
        this.employeeRepo = employeeRepo;
    }

    public Project createProject(String name, String requesterInitials) throws Exception {
        Employee requester = getEmployeeOrFail(normalizeInitials(requesterInitials));
        Project newProject = new Project(nextProjectID++, name);
        newProject.setProjectLeader(requester);
        newProject.grantViewAccess(requester);
        projectRepo.save(newProject);
        return newProject;
    }

    public void setProjectLeader(int projectID, String initials, String requesterInitials) throws Exception {
        Project project = getProjectOrFail(projectID);
        Employee requester = getEmployeeOrFail(normalizeInitials(requesterInitials));
        requireCanEditProject(project, requester);

        Employee employee = getEmployeeOrFail(initials);
        project.setProjectLeader(employee);
        project.grantViewAccess(employee);
    }

    public Activity addActivity(int projectID, String name, int budgetedHours, int startWeek, int endWeek, String requesterInitials) throws Exception {
        Project project = getProjectOrFail(projectID);
        Employee requester = getEmployeeOrFail(normalizeInitials(requesterInitials));
        if (!canViewProject(project, requester)) {
            throw new Exception("Not allowed to edit project " + project.getProjectID());
        }

        Activity newActivity = new Activity(nextActivityID++, name, budgetedHours, startWeek, endWeek);
        project.addActivity(newActivity);
        return newActivity;
    }

    public Activity editActivity(
        int projectID,
        int activityID,
        String name,
        int budgetedHours,
        int startWeek,
        int endWeek,
        ActivityStatus status,
        String requesterInitials
    ) throws Exception {
        Project project = getProjectOrFail(projectID);
        Employee requester = getEmployeeOrFail(normalizeInitials(requesterInitials));
        requireCanEditProject(project, requester);

        Activity activity = project.findActivityByID(activityID);
        if (activity == null) {
            throw new Exception("Activity with ID " + activityID + " not found in project " + projectID);
        }

        activity.setName(name);
        activity.setBudgetedHours(budgetedHours);
        activity.setStartWeek(startWeek);
        activity.setEndWeek(endWeek);
        activity.setStatus(status);
        return activity;
    }

    public void assignEmployee(int projectID, int activityID, String initials, String requesterInitials) throws Exception {
        Project project = getProjectOrFail(projectID);
        Employee requester = getEmployeeOrFail(normalizeInitials(requesterInitials));
        requireCanEditProject(project, requester);

        Employee employee = getEmployeeOrFail(initials);

        Activity activity = project.findActivityByID(activityID);
        if (activity == null) {
            throw new Exception("Activity with ID " + activityID + " not found in project " + projectID);
        }

        // The domain model handles the list updates and consistency
        activity.assignEmployee(employee);
        project.grantViewAccess(employee);
    }

    public void unassignEmployee(int projectID, int activityID, String initials, String requesterInitials) throws Exception {
        Project project = getProjectOrFail(projectID);
        Employee requester = getEmployeeOrFail(normalizeInitials(requesterInitials));
        requireCanEditProject(project, requester);

        Employee employee = getEmployeeOrFail(initials);
        Activity activity = project.findActivityByID(activityID);
        if (activity == null) {
            throw new Exception("Activity with ID " + activityID + " not found in project " + projectID);
        }

        activity.unassignEmployee(employee);
    }

    public void registerTime(int projectID, int activityID, String initials, TimeEntry entry, String requesterInitials) throws Exception {
        Project project = getProjectOrFail(projectID);
        Employee requester = getEmployeeOrFail(normalizeInitials(requesterInitials));
        requireCanViewProject(project, requester);

        Activity activity = project.findActivityByID(activityID);

        if (activity == null) {
            throw new Exception("Activity with ID " + activityID + " not found in project " + projectID);
        }

        Employee targetEmployee = getEmployeeOrFail(initials);
        boolean requesterIsLeader = isLeader(project, requester);
        boolean requesterIsTarget = requester.getInitials().equals(targetEmployee.getInitials());
        if (!requesterIsLeader && !requesterIsTarget) {
            throw new Exception("Not allowed to register time for other employees.");
        }

        if (!targetEmployee.getAssignedActivities().contains(activity)) {
            throw new Exception("Employee is not assigned to this activity.");
        }

        project.registerTime(activityID, entry);
    }

    public List<Project> getVisibleProjects(String requesterInitials) throws Exception {
        Employee requester = getEmployeeOrFail(normalizeInitials(requesterInitials));
        List<Project> visible = new ArrayList<>();

        for (Project project : projectRepo.findAll()) {
            if (canViewProject(project, requester)) {
                visible.add(project);
            }
        }
        return visible;
    }

    public Project getProjectForUser(int projectID, String requesterInitials) throws Exception {
        Employee requester = getEmployeeOrFail(normalizeInitials(requesterInitials));
        Project project = getProjectOrFail(projectID);
        requireCanViewProject(project, requester);
        return project;
    }

    public List<Employee> getAssignedEmployees(int projectID, int activityID, String requesterInitials) throws Exception {
        Project project = getProjectForUser(projectID, requesterInitials);
        Activity activity = project.findActivityByID(activityID);
        if (activity == null) {
            throw new Exception("Activity not found.");
        }

        List<Employee> assigned = new ArrayList<>();
        for (Employee employee : employeeRepo.findAll()) {
            if (employee.getAssignedActivities().contains(activity)) {
                assigned.add(employee);
            }
        }
        return assigned;
    }

    public List<Employee> getAllEmployees(String requesterInitials) throws Exception {
        getEmployeeOrFail(normalizeInitials(requesterInitials));
        return new ArrayList<>(employeeRepo.findAll());
    }

    public List<Employee> getProjectViewers(int projectID, String requesterInitials) throws Exception {
        Project project = getProjectForUser(projectID, requesterInitials);
        return project.getViewers();
    }

    public void setProjectViewers(int projectID, List<String> viewerInitials, String requesterInitials) throws Exception {
        Project project = getProjectOrFail(projectID);
        Employee requester = getEmployeeOrFail(normalizeInitials(requesterInitials));
        requireCanEditProject(project, requester);

        List<Employee> desiredViewers = new ArrayList<>();
        for (String initials : viewerInitials) {
            String normalized = normalizeInitials(initials);
            if (!normalized.isBlank() && !containsByInitials(desiredViewers, normalized)) {
                desiredViewers.add(getEmployeeOrFail(normalized));
            }
        }

        for (Employee existing : new ArrayList<>(project.getViewers())) {
            if (project.getProjectLeader() != null
                && existing.getInitials().equals(project.getProjectLeader().getInitials())) {
                continue;
            }
            if (!containsByInitials(desiredViewers, existing.getInitials())) {
                project.revokeViewAccess(existing);
            }
        }

        for (Employee viewer : desiredViewers) {
            project.grantViewAccess(viewer);
        }

        if (project.getProjectLeader() != null) {
            project.grantViewAccess(project.getProjectLeader());
        }
    }

    // Helper methods (DRY principle)
    private Project getProjectOrFail(int projectID) throws Exception {
        Project project = projectRepo.findByID(projectID);
        if(project == null) {
            throw new Exception("Project with ID " + projectID + " not found.");
        }
        return project;   
    }

    private Employee getEmployeeOrFail(String initials) throws Exception {
        Employee employee = employeeRepo.findByInitials(initials);
        if (employee == null) {
            throw new Exception("Employee with initals " + initials + " not found");
        }
        return employee;
    }

    private String normalizeInitials(String initials) {
        return initials == null ? "" : initials.trim().toLowerCase(Locale.ROOT);
    }

    private boolean isLeader(Project project, Employee employee) {
        return project.getProjectLeader() != null
            && project.getProjectLeader().getInitials().equals(employee.getInitials());
    }

    private boolean canViewProject(Project project, Employee requester) {
        return project.canBeViewedBy(requester);
    }

    private void requireCanViewProject(Project project, Employee requester) throws Exception {
        if (!canViewProject(project, requester)) {
            throw new Exception("Not allowed to view project " + project.getProjectID());
        }
    }

    private void requireCanEditProject(Project project, Employee requester) throws Exception {
        if (!isLeader(project, requester)) {
            throw new Exception("Not allowed to edit project " + project.getProjectID());
        }
    }

    public List<Employee> getAvailableEmployees(int projectID, int activityID, String requesterInitials) throws Exception {
        Project project = getProjectForUser(projectID, requesterInitials);
        Activity activity = project.findActivityByID(activityID);

        if (activity == null) {
            throw new Exception("Activity not found.");
        }

        List<Employee> available = new ArrayList<>();

       for (Employee employee : employeeRepo.findAll()) {
            if (employee.isAvailableFor(activity)) {
                available.add(employee);
            }
       }
       return available;
    }

    private boolean containsByInitials(List<Employee> employees, String initials) {
        for (Employee employee : employees) {
            if (employee.getInitials().equals(initials)) {
                return true;
            }
        }
        return false;
    }

}
