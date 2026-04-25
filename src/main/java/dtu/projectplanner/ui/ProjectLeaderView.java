package dtu.projectplanner.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ProjectLeaderView {

    private double w(double u) { return Style.w(u); }
    private double h(double u) { return Style.h(u); }

    public Pane build() {
        BorderPane root = new BorderPane();
        root.setStyle(Style.background());
        root.setPrefSize(w(100), h(100));
        root.setTop(buildTopBar());
        root.setCenter(buildContent());
        return root;
    }

    private HBox buildTopBar() {
        HBox bar = new HBox();
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPrefHeight(h(10));
        bar.setPadding(new Insets(0, w(3), 0, w(3)));
        bar.setStyle(Style.topBar(Style.LEADER));

        Button back = new Button("\u2190 Back");
        back.setStyle(Style.backButton(Style.u(1.1)));
        back.setOnAction(e -> App.showHome());

        Label title = new Label("Project Leader Dashboard");
        title.setFont(Style.bold(1.8));
        title.setTextFill(Color.WHITE);
        HBox.setMargin(title, new Insets(0, 0, 0, w(1)));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label role = new Label("PROJECT LEADER");
        role.setFont(Style.bold(0.9));
        role.setTextFill(Color.web("#FFFFFF", 0.6));
        role.setStyle(Style.roleTag());

        bar.getChildren().addAll(back, title, spacer, role);
        return bar;
    }

    private VBox buildContent() {
        VBox content = new VBox(h(3));
        content.setPadding(new Insets(h(4), w(5), h(4), w(5)));

        Label heading = new Label("Projects");
        heading.setFont(Style.bold(1.7));
        heading.setTextFill(Color.web(Style.TEXT_DARK));

        Label placeholder = new Label("No projects created yet.");
        placeholder.setFont(Style.regular(1.2));
        placeholder.setTextFill(Color.web(Style.TEXT_MUTED));

        content.getChildren().addAll(heading, placeholder);
        return content;
    }
}
