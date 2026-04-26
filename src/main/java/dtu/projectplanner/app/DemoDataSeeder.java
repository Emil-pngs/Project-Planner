package dtu.projectplanner.app;

import dtu.projectplanner.domain.Activity;
import dtu.projectplanner.domain.Employee;
import dtu.projectplanner.domain.Project;
import dtu.projectplanner.repository.EmployeeRepository;
import dtu.projectplanner.repository.ProjectRepository;

import java.util.List;

public final class DemoDataSeeder {

    private DemoDataSeeder() {
    }

    public static String populate(ProjectPlanningService service, ProjectRepository projectRepo, EmployeeRepository employeeRepo) throws Exception {
        int createdEmployees = seedEmployees(employeeRepo);
        int createdProjects = 0;
        int createdActivities = 0;

        if (findProjectByName(projectRepo, "Campus Navigation Upgrade") == null) {
            Project p = service.createProject("Campus Navigation Upgrade", "anna");
            createdProjects++;
            createdActivities += addActivity(service, p, "Requirements mapping", 40, 17, 18, List.of("anna", "jens"), "anna");
            createdActivities += addActivity(service, p, "UI prototype", 60, 18, 20, List.of("mads", "sara"), "anna");
            createdActivities += addActivity(service, p, "Usability tests", 35, 21, 22, List.of("lina", "jens"), "anna");
        }

        if (findProjectByName(projectRepo, "Exam Planner 2.0") == null) {
            Project p = service.createProject("Exam Planner 2.0", "mads");
            createdProjects++;
            createdActivities += addActivity(service, p, "Domain model cleanup", 30, 17, 18, List.of("mads", "lina"), "mads");
            createdActivities += addActivity(service, p, "Calendar integration", 80, 19, 22, List.of("sara", "jens"), "mads");
            createdActivities += addActivity(service, p, "Time registration flow", 45, 20, 23, List.of("anna", "mads"), "mads");
        }

        if (findProjectByName(projectRepo, "Lab Resource Booking") == null) {
            Project p = service.createProject("Lab Resource Booking", "sara");
            createdProjects++;
            createdActivities += addActivity(service, p, "API design", 55, 18, 20, List.of("sara", "jens"), "sara");
            createdActivities += addActivity(service, p, "Conflict detection", 65, 20, 23, List.of("mads", "lina"), "sara");
            createdActivities += addActivity(service, p, "Pilot deployment", 30, 24, 25, List.of("anna", "sara"), "sara");
        }

        return "Demo data ready: +" + createdEmployees + " employees, +" + createdProjects + " projects, +" + createdActivities + " activities";
    }

    private static int seedEmployees(EmployeeRepository employeeRepo) {
        int created = 0;
        created += saveEmployeeIfMissing(employeeRepo, "Anna Schmidt", "anna");
        created += saveEmployeeIfMissing(employeeRepo, "Mads Jensen", "mads");
        created += saveEmployeeIfMissing(employeeRepo, "Sara Lind", "sara");
        created += saveEmployeeIfMissing(employeeRepo, "Jens Krogh", "jens");
        created += saveEmployeeIfMissing(employeeRepo, "Lina Noor", "lina");
        created += saveEmployeeIfMissing(employeeRepo, "Kasper Holm", "kasp");
        created += saveEmployeeIfMissing(employeeRepo, "Mila Berg", "mila");
        created += saveEmployeeIfMissing(employeeRepo, "Noah Friis", "noah");
        return created;
    }

    private static int saveEmployeeIfMissing(EmployeeRepository employeeRepo, String name, String initials) {
        if (employeeRepo.findByInitials(initials) != null) {
            return 0;
        }
        employeeRepo.save(new Employee(name, initials));
        return 1;
    }

    private static int addActivity(ProjectPlanningService service, Project project, String name, int hours, int startWeek, int endWeek, List<String> assigneeInitials, String requesterInitials) throws Exception {
        Activity activity = service.addActivity(project.getProjectID(), name, hours, startWeek, endWeek, requesterInitials);
        for (String initials : assigneeInitials) {
            service.assignEmployee(project.getProjectID(), activity.getActivityID(), initials, requesterInitials);
        }
        return 1;
    }

    private static Project findProjectByName(ProjectRepository projectRepo, String name) {
        for (Project p : projectRepo.findAll()) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }
}
