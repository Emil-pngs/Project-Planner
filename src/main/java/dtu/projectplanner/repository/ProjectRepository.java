package dtu.projectplanner.repository;

import dtu.projectplanner.domain.Project;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
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

    public List<Project> findAll() {
        return Collections.unmodifiableList(projects);
    }

    /**
     * Generates the next project ID in the format YY###,
     * e.g. 26001, 26002, ... 26999.
     */
    public int generateNextID() {
        int year = LocalDate.now().getYear() % 100; // e.g. 26
        int prefix = year * 1000;                   // e.g. 26000
        int maxSeq = 0;
        for (Project p : projects) {
            if (p.getProjectID() / 1000 == year) {
                int seq = p.getProjectID() % 1000;
                if (seq > maxSeq) {
                    maxSeq = seq;
                }
            }
        }
        return prefix + maxSeq + 1;
    }

}
