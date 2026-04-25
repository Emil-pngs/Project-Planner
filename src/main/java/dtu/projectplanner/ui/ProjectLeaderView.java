package dtu.projectplanner.ui;

import dtu.projectplanner.domain.Project;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.List;

public class ProjectLeaderView {

    private BorderPane root;
    private VBox projectItems;
    private static Project selectedProject = null;

    private double w(double u) { return Style.w(u); }
    private double h(double u) { return Style.h(u); }

    public Pane build() {
        root = new BorderPane();
        root.setStyle(Style.background());
        root.setPrefSize(w(100), h(100));
        root.setTop(buildTopBar());
        root.setLeft(buildSidebar());
        root.setCenter(selectedProject != null
            ? new ProjectView(selectedProject).build()
            : buildWelcome());
        return root;
    }

    private HBox buildTopBar() {
        HBox bar = new HBox();
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPrefHeight(h(10));
        bar.setPadding(new Insets(0, w(3), 0, w(3)));
        bar.setStyle(Style.topBar(Style.LEADER_BAR));

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

        Button darkToggle = Style.buildDarkToggle();
        HBox.setMargin(darkToggle, new Insets(0, 0, 0, Style.w(3)));

        bar.getChildren().addAll(back, title, spacer, role, darkToggle);
        return bar;
    }

    private VBox buildSidebar() {
        BorderPane inner = new BorderPane();
        inner.setStyle(Style.sidebar());

        // ── Inline add row (fixed height, no toggle) ─────────────────
        TextField nameField = new TextField();
        nameField.setPromptText("New project name...");
        nameField.setPrefHeight(h(5));
        nameField.setStyle(Style.sidebarTextField(Style.u(1.0)));
        HBox.setHgrow(nameField, Priority.ALWAYS);

        Button addBtn = new Button("+");
        addBtn.setPrefHeight(h(5));
        addBtn.setStyle(Style.sidebarAddButton(Style.u(1.1)));

        HBox addRow = new HBox(w(1), nameField, addBtn);
        addRow.setAlignment(Pos.CENTER_LEFT);

        Label errorLabel = new Label("");
        errorLabel.setFont(Style.regular(0.85));
        errorLabel.setTextFill(Color.web("#C0557A"));
        errorLabel.setWrapText(true);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        addBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (name.isBlank()) {
                errorLabel.setText("Name cannot be empty.");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                return;
            }
            App.getService().addProject(name);
            nameField.clear();
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);
            refreshProjectItems(projectItems);
        });

        Label header = new Label("PROJECTS");
        header.setFont(Style.bold(0.85));
        header.setTextFill(Color.web(Style.SIDEBAR_MUTED));
        header.setPadding(new Insets(h(1.5), 0, h(0.5), 0));

        VBox topSection = new VBox(h(0.8), addRow, errorLabel, header);
        topSection.setPadding(new Insets(h(2), w(2), 0, w(2)));

        // ── Scrollable project list ───────────────────────────────────
        projectItems = new VBox(h(0.3));
        projectItems.setFillWidth(true);
        refreshProjectItems(projectItems);

        ScrollPane scroll = new ScrollPane(projectItems);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setStyle(Style.scrollPaneTransparent());

        inner.setTop(topSection);
        inner.setCenter(scroll);

        VBox sidebar = new VBox();
        sidebar.setPrefWidth(w(22));
        VBox.setVgrow(inner, Priority.ALWAYS);
        sidebar.getChildren().add(inner);
        return sidebar;
    }

    private void refreshProjectItems(VBox projectItems) {
        projectItems.getChildren().clear();
        List<Project> projects = App.getService().getProjects();
        if (projects.isEmpty()) {
            Label none = new Label("No projects yet");
            none.setFont(Style.regular(1.0));
            none.setStyle("-fx-text-fill: " + Style.SIDEBAR_MUTED + ";");
            none.setPadding(new Insets(h(0.5), 0, 0, w(1)));
            projectItems.getChildren().add(none);
        } else {
            for (Project p : projects) {
                projectItems.getChildren().add(buildProjectRow(p));
            }
        }
    }

    private HBox buildProjectRow(Project p) {
        HBox row = new HBox(w(1.5));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(h(1.2), w(2), h(1.2), w(2)));
        row.setStyle(p == selectedProject
            ? Style.sidebarProjectRowSelected()
            : Style.sidebarProjectRow());

        Label idLabel = new Label(String.valueOf(p.getProjectID()));
        idLabel.setFont(Style.bold(0.85));
        idLabel.setStyle("-fx-text-fill: " + Style.SIDEBAR_MUTED + ";");
        idLabel.setMinWidth(w(5));

        Label nameLabel = new Label(p.getName());
        nameLabel.setFont(Style.regular(1.1));
        nameLabel.setStyle("-fx-text-fill: " + Style.SIDEBAR_TEXT + ";");
        nameLabel.setWrapText(true);
        HBox.setHgrow(nameLabel, Priority.ALWAYS);

        row.getChildren().addAll(idLabel, nameLabel);

        row.hoverProperty().addListener((obs, was, isHovered) -> {
            if (p == selectedProject) return;
            row.setStyle(isHovered
                ? Style.sidebarProjectRowHover()
                : Style.sidebarProjectRow());
        });

        row.setOnMouseClicked(e -> {
            selectedProject = p;
            // re-render all rows to update highlight
            VBox items = (VBox) row.getParent();
            items.getChildren().forEach(child -> {
                if (child instanceof HBox r) {
                    r.setStyle(r == row
                        ? Style.sidebarProjectRowSelected()
                        : Style.sidebarProjectRow());
                }
            });
            root.setCenter(new ProjectView(p).build());
        });
        return row;
    }

    private StackPane buildWelcome() {
        StackPane pane = new StackPane();
        pane.setStyle(Style.background());

        VBox content = new VBox(h(2));
        content.setAlignment(Pos.CENTER);

        Label msg = new Label("Select a project from the sidebar");
        msg.setFont(Style.regular(1.4));
        msg.setTextFill(Color.web(Style.TEXT_MUTED));

        content.getChildren().add(msg);
        pane.getChildren().add(content);
        return pane;
    }
}

