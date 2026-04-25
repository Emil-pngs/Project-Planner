package dtu.projectplanner.ui;

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
        stage.setScene(scene);
        stage.setTitle("Project Planner");
        stage.centerOnScreen();
        stage.show();
    }

    static void showHome()          { scene.setRoot(new HomeView().build()); }
    static void showEmployee()      { scene.setRoot(new EmployeeView().build()); }
    static void showProjectLeader() { scene.setRoot(new ProjectLeaderView().build()); }

    public static void main(String[] args) { launch(); }
}