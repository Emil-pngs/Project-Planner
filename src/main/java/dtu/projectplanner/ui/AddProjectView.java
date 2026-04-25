package dtu.projectplanner.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class AddProjectView {

    private double w(double u) { return Style.w(u); }
    private double h(double u) { return Style.h(u); }

    public Pane build() {
        BorderPane root = new BorderPane();
        root.setStyle(Style.background());
        root.setPrefSize(w(100), h(100));
        root.setTop(buildTopBar());
        root.setCenter(buildForm());
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
        back.setOnAction(e -> App.showProjectLeader());

        Label title = new Label("New Project");
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

    private StackPane buildForm() {
        double cardW = w(46);

        VBox card = new VBox(h(2.5));
        card.setPrefWidth(cardW);
        card.setMaxWidth(cardW);
        card.setPadding(new Insets(h(5), w(4), h(5), w(4)));
        card.setStyle(Style.card("white"));
        card.setEffect(Style.cardShadow(Style.LEADER));

        Label heading = new Label("Create a Project");
        heading.setFont(Style.bold(1.6));
        heading.setTextFill(Color.web(Style.TEXT_DARK));

        Label nameLabel = new Label("Project Name");
        nameLabel.setFont(Style.bold(1.1));
        nameLabel.setTextFill(Color.web(Style.TEXT_DARK));

        TextField nameField = new TextField();
        nameField.setPromptText("Enter project name...");
        nameField.setPrefHeight(h(6));
        nameField.setStyle(Style.inputField(Style.u(1.1)));

        Label errorLabel = new Label("");
        errorLabel.setFont(Style.regular(1.0));
        errorLabel.setTextFill(Color.web("#DC2626"));
        errorLabel.setVisible(false);

        Button createBtn = new Button("Create Project");
        createBtn.setPrefWidth(cardW - Style.w(8));
        createBtn.setPrefHeight(h(7));
        createBtn.setStyle(Style.solidButton(Style.LEADER, Style.u(1.1)));
        VBox.setMargin(createBtn, new Insets(h(1.5), 0, 0, 0));

        createBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (name.isBlank()) {
                errorLabel.setText("Project name cannot be empty.");
                errorLabel.setVisible(true);
                return;
            }
            App.getService().createProject(name);
            App.showProjectLeader();
        });

        card.getChildren().addAll(heading, nameLabel, nameField, errorLabel, createBtn);

        StackPane center = new StackPane(card);
        center.setAlignment(Pos.CENTER);
        return center;
    }
}
