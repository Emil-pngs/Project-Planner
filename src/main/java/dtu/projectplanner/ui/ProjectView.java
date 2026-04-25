package dtu.projectplanner.ui;

import dtu.projectplanner.domain.Project;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ProjectView {

    private final Project project;

    private double w(double u) { return Style.w(u); }
    private double h(double u) { return Style.h(u); }

    public ProjectView(Project project) {
        this.project = project;
    }

    public Pane build() {
        BorderPane pane = new BorderPane();
        pane.setStyle(Style.background());
        pane.setTop(buildHeader());
        pane.setCenter(buildPlaceholder());
        return pane;
    }

    private HBox buildHeader() {
        HBox header = new HBox(w(1.5));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(h(3), w(4), h(2), w(4)));
        header.setStyle(Style.projectViewHeader());

        Label name = new Label(project.getName());
        name.setFont(Style.bold(1.8));
        name.setTextFill(Color.web(Style.TEXT_DARK));

        Label id = new Label("# " + project.getProjectID());
        id.setFont(Style.bold(1.0));
        id.setTextFill(Color.web(Style.SIDEBAR_MUTED));
        id.setPadding(new Insets(4, 10, 4, 10));
        id.setStyle(Style.idBadge());

        header.getChildren().addAll(name, id);
        return header;
    }

    private StackPane buildPlaceholder() {
        StackPane pane = new StackPane();
        VBox content = new VBox(h(1));
        content.setAlignment(Pos.CENTER);

        Label msg = new Label("No content yet.");
        msg.setFont(Style.regular(1.3));
        msg.setTextFill(Color.web(Style.TEXT_MUTED));

        content.getChildren().add(msg);
        pane.getChildren().add(content);
        return pane;
    }
}

