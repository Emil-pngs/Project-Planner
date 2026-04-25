package dtu.projectplanner.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) {
        scene = new Scene(new PrimaryController().getView(), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    static void showPrimary() {
        scene.setRoot(new PrimaryController().getView());
    }

    static void showSecondary() {
        scene.setRoot(new SecondaryController().getView());
    }

    public static void main(String[] args) {
        launch();
    }

}