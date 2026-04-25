package dtu.projectplanner.ui;

import dtu.projectplanner.app.ProjectPlanningService;
import dtu.projectplanner.domain.Project;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class App extends Application {

    private static Scene scene;

    /** Width of one grid cell (1/100 of 80% screen width). */
    static double cellW;
    /** Height of one grid cell (1/100 of 80% screen height). */
    static double cellH;

    @Override
    public void start(Stage stage) {
        var bounds = Screen.getPrimary().getVisualBounds();
        double w = bounds.getWidth() * 0.8;
        double h = bounds.getHeight() * 0.8;
        cellW = w / 100.0;
        cellH = h / 100.0;

        scene = new Scene(new HomeView().build(), w, h);
        currentRefresh = () -> scene.setRoot(new HomeView().build());
        stage.setScene(scene);
        stage.setTitle("Project Planner");
        stage.centerOnScreen();
        stage.show();
    }

    public static ProjectPlanningService getService() {
        return ProjectPlanningService.getInstance();
    }

    private static Runnable currentRefresh;

    public static void refresh() {
        if (currentRefresh != null) currentRefresh.run();
    }

    static void showHome() {
        currentRefresh = () -> scene.setRoot(new HomeView().build());
        currentRefresh.run();
    }
    static void showEmployee() {
        currentRefresh = () -> scene.setRoot(new EmployeeView().build());
        currentRefresh.run();
    }
    static void showProjectLeader() {
        currentRefresh = () -> scene.setRoot(new ProjectLeaderView().build());
        currentRefresh.run();
    }
    static void showAddProject() {
        currentRefresh = () -> scene.setRoot(new AddProjectView().build());
        currentRefresh.run();
    }
    static void showProject(Project p) {
        currentRefresh = () -> scene.setRoot(new ProjectView(p).build());
        currentRefresh.run();
    }

    public static void main(String[] args) { launch(); }
}