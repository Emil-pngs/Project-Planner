package dtu.projectplanner.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class PrimaryController {

    public VBox getView() {
        Label label = new Label("Primary View");
        Button button = new Button("Switch to Secondary View");
        button.setOnAction(e -> App.showSecondary());

        VBox vbox = new VBox(20, label, button);
        vbox.setAlignment(javafx.geometry.Pos.CENTER);
        vbox.setPadding(new Insets(20));
        return vbox;
    }
}
