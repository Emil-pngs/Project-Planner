package dtu.projectplanner.app;

import dtu.projectplanner.domain.*;
import dtu.projectplanner.repository.*;

import java.util.ArrayList;
import java.util.List;

public class ProjectPlanningService {

    private ProjectRepository projectRepo;
    private EmployeeRepository employeeRepo;

    private int nextProjectID = 260001;
    private int nextActivityID = 1;

    public ProjectPlanningService(ProjectRepository projectRepo, EmployeeRepository employeeRepo) {
        this.projectRepo = projectRepo;
        this.employeeRepo = employeeRepo;
    }

    // Create a project
    public Project createProject(String name) {
        Project newProject = new Project(nextProjectID++, name);
        projectRepo.save(newProject);
        return newProject;
    }

    // Assign leader
    public void setProjectLeader(int projectID, String initials) throws Exception {
        Project project = getProjectOrFail(projectID);
        Employee employee = getEmployeeOrFail(initials);
        project.setProjectLeader(employee);
    }

    // Add activity to project
    public Activity addActivity(int projectID, String name, int budgetedHours, int startWeek, int endWeek) throws Exception {
        Project project = getProjectOrFail(projectID);

        Activity newActivity = new Activity(nextActivityID++, name, budgetedHours, startWeek, endWeek);
        project.addActivity(newActivity);
        return newActivity;
    }

    // Assign employee
    public void assignEmployee(int projectID, int activityID, String initials) throws Exception {
        Project project = getProjectOrFail(projectID);
        Employee employee = getEmployeeOrFail(initials);

        Activity activity = project.findActivityByID(activityID);
        if (activity == null) {
            throw new Exception("Activity with ID " + activityID + " not found in project " + projectID);
        }

        // The domain model handles the list updates and consistency
        activity.assignEmployee(employee);
    }

    // Registering time
    public void registerTime(int projectID, int activityID, String initials, TimeEntry entry) throws Exception {
        Project project = getProjectOrFail(projectID);
        Activity activity = project.findActivityByID(activityID);

        if (activity == null) {
            throw new Exception("Activity with ID " + activityID + " not found in project " + projectID);
        }

        // The project tells the activity to register time, which will check if the employee is assigned
        project.registerTime(activityID, entry);
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

    // Get all available employees
    // Intead of extracting lists from employee objects we delegate the work to the entity
    public List<Employee> getAvailableEmployees(int projectID, int activityID) throws Exception {
        Project project = getProjectOrFail(projectID);
        Activity activity = project.findActivityByID(activityID);

        if (activity == null) throw new Exception("Activity not found.");

        List<Employee> available = new ArrayList<>();

       for (Employee employee : employeeRepo.findAll()) {
            if (employee.isAvailableFor(activity)) {
                available.add(employee);
            }
       }
       return available; 
    }

}
