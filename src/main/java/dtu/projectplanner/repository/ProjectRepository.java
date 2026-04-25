package dtu.projectplanner.repository;

import dtu.projectplanner.domain.Project;
import java.util.ArrayList;
import java.util.List;

public class ProjectRepository {
    // The database of projects
    private List<Project> projects = new ArrayList<>();

    public void save(Project project) {
        if (!projects.contains(project)) {
            projects.add(project);
        }
    }

    public Project findByID(int projectID) {
        for (Project project : projects) {
            if (project.getProjectID() == projectID) {
                return project;
            }
        }
        // Return null if no project with the given ID is found
        return null;
    }

    
}
