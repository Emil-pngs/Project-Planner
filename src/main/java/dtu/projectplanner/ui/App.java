package dtu.projectplanner.ui;

import dtu.projectplanner.app.ProjectPlanningService;
import dtu.projectplanner.app.DemoDataSeeder;
import dtu.projectplanner.domain.Activity;
import dtu.projectplanner.domain.Employee;
import dtu.projectplanner.domain.Project;
import dtu.projectplanner.domain.TimeEntry;
import dtu.projectplanner.repository.EmployeeRepository;
import dtu.projectplanner.repository.ProjectRepository;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class App extends Application {

    private static Scene scene;
    private static final ProjectRepository projectRepo = new ProjectRepository();
    private static final EmployeeRepository employeeRepo = new EmployeeRepository();
    private static final ProjectPlanningService service =
        new ProjectPlanningService(projectRepo, employeeRepo);

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final ObservableList<Project> projectItems = FXCollections.observableArrayList();
    private final ObservableList<Activity> activityItems = FXCollections.observableArrayList();
    private final ObservableList<Employee> teamItems = FXCollections.observableArrayList();
    private final ObservableList<Activity> myActivityItems = FXCollections.observableArrayList();

    private Employee currentEmployee;
    private Project selectedProject;
    private Activity selectedActivity;

    private Label projectTitleLabel;
    private Label projectInfoLabel;
    private Label projectViewersLabel;
    private Label statusLabel;
    private Button editViewersBtn;
    private ListView<Project> projectList;
    private TableView<Activity> activityTable;
    private ListView<Employee> teamList;
    private ListView<Activity> myActivityList;

    @Override
    public void start(Stage stage) {
        var bounds = Screen.getPrimary().getVisualBounds();
        double w = bounds.getWidth() * 0.8;
        double h = bounds.getHeight() * 0.8;

        scene = new Scene(buildLoginView(), w, h);
        stage.setScene(scene);
        stage.setTitle("Project Planner");
        stage.centerOnScreen();
        stage.show();
    }

    private Pane buildLoginView() {
        VBox page = new VBox(24);
        page.setAlignment(Pos.CENTER);
        page.setPadding(new Insets(40));
        page.setStyle(Style.loginBackground());

        VBox card = new VBox(16);
        card.setMaxWidth(420);
        card.setPadding(new Insets(28));
        card.setStyle(Style.loginCard());

        Label title = new Label("Project Planner");
        title.setStyle(Style.titleXL());

        Label subtitle = new Label("Sign in with at least 4 letters.");
        subtitle.setStyle(Style.subtitle());

        TextField initialsField = field("Your initials (e.g. huba)");

        Label validation = new Label();
        validation.setStyle(Style.errorText());

        Button loginBtn = new Button("Login");
        loginBtn.setStyle(Style.primaryButton());
        loginBtn.setMaxWidth(Double.MAX_VALUE);

        Runnable doLogin = () -> {
            String raw = initialsField.getText() == null ? "" : initialsField.getText().trim().toLowerCase(Locale.ROOT);
            if (!raw.matches("[a-zA-Z]{4,}")) {
                validation.setText("Use at least 4 English letters.");
                return;
            }

            Employee user = employeeRepo.findByInitials(raw);
            if (user == null) {
                user = new Employee(raw.substring(0, 1).toUpperCase(Locale.ROOT) + raw.substring(1), raw);
                employeeRepo.save(user);
            }

            currentEmployee = user;
            scene.setRoot(buildMainView());
        };

        loginBtn.setOnAction(e -> doLogin.run());
        initialsField.setOnAction(e -> doLogin.run());

        card.getChildren().addAll(title, subtitle, initialsField, validation, loginBtn);
        page.getChildren().add(card);
        return page;
    }

    private Pane buildMainView() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(14));
        root.setStyle(Style.rootBackground());

        root.setTop(buildTopBar());
        root.setLeft(buildProjectPanel());
        root.setCenter(buildActivityPanel());
        root.setRight(buildSidePanel());

        statusLabel = new Label("Ready");
        statusLabel.setStyle(Style.statusText());
        BorderPane.setMargin(statusLabel, new Insets(8, 4, 4, 4));
        root.setBottom(statusLabel);

        loadProjects();
        refreshMyActivities();

        return root;
    }

    private Node buildTopBar() {
        Label logo = new Label("Project Planner");
        logo.setStyle(Style.titleL());

        Label user = new Label("User: " + currentEmployee.getInitials());
        user.setStyle(Style.topBadge());

        Button createProjectBtn = actionButton("Create project", e -> openCreateProjectDialog(), true);
        Button createActivityBtn = actionButton("Create activity", e -> openCreateActivityDialog(), false);
        Button registerTimeBtn = actionButton("Register time", e -> openRegisterTimeDialog(), false);
        Button reportBtn = actionButton("Generate report", e -> generateReport(), false);
        editViewersBtn = actionButton("Edit viewers", e -> openEditViewersDialog(), false);
        Button populateBtn = actionButton("Populate demo data", e -> populateDemoData(), false);

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle(Style.ghostButton());
        logoutBtn.setOnAction(e -> {
            currentEmployee = null;
            selectedProject = null;
            selectedActivity = null;
            scene.setRoot(buildLoginView());
        });

        HBox left = new HBox(12, logo, user);
        left.setAlignment(Pos.CENTER_LEFT);

        HBox actions = new HBox(8, createProjectBtn, createActivityBtn, registerTimeBtn, reportBtn, editViewersBtn, populateBtn, logoutBtn);
        actions.setAlignment(Pos.CENTER_RIGHT);

        BorderPane bar = new BorderPane();
        bar.setLeft(left);
        bar.setRight(actions);
        bar.setPadding(new Insets(8, 4, 10, 4));
        updateEditViewersButton();
        return bar;
    }

    private Button actionButton(String text, EventHandler<ActionEvent> handler, boolean primary) {
        Button button = new Button(text);
        button.setStyle(primary ? Style.primaryButton() : Style.secondaryButton());
        button.setOnAction(handler);
        return button;
    }

    private Node buildProjectPanel() {
        VBox panel = new VBox(10);
        panel.setPrefWidth(250);
        panel.setMaxWidth(290);
        panel.setPadding(new Insets(14));
        panel.setStyle(Style.card());

        Label title = new Label("Projects");
        title.setStyle(Style.titleM());

        projectList = new ListView<>(projectItems);
        projectList.setCellFactory(v -> new ListCell<>() {
            @Override
            protected void updateItem(Project item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }
                String leader = item.getProjectLeader() == null
                    ? "Unassigned"
                    : item.getProjectLeader().getInitials().toUpperCase(Locale.ROOT);
                String created = item.getCreatedOn() == null ? "Unknown" : item.getCreatedOn().format(DATE_FMT);

                Label nameLabel = new Label(item.getName());
                nameLabel.setStyle(Style.projectNameSmall());

                Label metaLabel = new Label("Leader: " + leader + "  |  Created: " + created);
                metaLabel.setStyle(Style.projectMetaSmall());

                VBox content = new VBox(2, nameLabel, metaLabel);
                content.setPadding(new Insets(2, 0, 2, 0));
                setGraphic(content);
                setText(null);
            }
        });
        projectList.setStyle(Style.list());
        projectList.getSelectionModel().selectedItemProperty().addListener((obs, oldProject, newProject) -> selectProject(newProject));

        VBox.setVgrow(projectList, Priority.ALWAYS);
        panel.getChildren().addAll(title, projectList);
        BorderPane.setMargin(panel, new Insets(0, 12, 0, 0));
        return panel;
    }

    private Node buildActivityPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(14));
        panel.setStyle(Style.card());

        projectTitleLabel = new Label("No project selected");
        projectTitleLabel.setStyle(Style.titleM());

        projectInfoLabel = new Label("Project ID: -  |  Leader: -  |  Created: -");
        projectInfoLabel.setStyle(Style.projectMetaSmall());

        projectViewersLabel = new Label("Viewers: -");
        projectViewersLabel.setStyle(Style.projectMetaSmall());

        Label headerTag = new Label("PROJECT DETAILS");
        headerTag.setStyle(Style.projectHeaderLabel());

        VBox projectHeader = new VBox(3, headerTag, projectTitleLabel, projectInfoLabel, projectViewersLabel);

        Label subtitle = new Label("Activities");
        subtitle.setStyle(Style.subtitle());

        activityTable = new TableView<>(activityItems);
        activityTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        activityTable.setPlaceholder(new Label("No activities yet"));
        activityTable.setStyle(Style.table());

        TableColumn<Activity, String> nameCol = new TableColumn<>("Activity");
        nameCol.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getName()));
        nameCol.setStyle(Style.tableColumn());

        TableColumn<Activity, Number> startCol = new TableColumn<>("Start Week");
        startCol.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getStartWeek()));
        startCol.setStyle(Style.tableColumn());

        TableColumn<Activity, Number> endCol = new TableColumn<>("End Week");
        endCol.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getEndWeek()));
        endCol.setStyle(Style.tableColumn());

        TableColumn<Activity, Number> hoursCol = new TableColumn<>("Hours");
        hoursCol.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getBudgetedHours()));
        hoursCol.setStyle(Style.tableColumn());

        TableColumn<Activity, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getStatus().name().replace('_', ' ')));
        statusCol.setStyle(Style.tableColumn());

        activityTable.getColumns().setAll(nameCol, startCol, endCol, hoursCol, statusCol);
        activityTable.getSelectionModel().selectedItemProperty().addListener((obs, oldActivity, newActivity) -> {
            selectedActivity = newActivity;
            refreshTeamPanel();
        });

        VBox.setVgrow(activityTable, Priority.ALWAYS);
        panel.getChildren().addAll(projectHeader, subtitle, activityTable);
        BorderPane.setMargin(panel, new Insets(0, 12, 0, 0));
        return panel;
    }

    private Node buildSidePanel() {
        VBox panel = new VBox(10);
        panel.setPrefWidth(280);
        panel.setMaxWidth(320);
        panel.setPadding(new Insets(14));
        panel.setStyle(Style.card());

        teamList = new ListView<>(teamItems);
        teamList.setPlaceholder(new Label("Select an activity"));
        teamList.setCellFactory(v -> new ListCell<>() {
            @Override
            protected void updateItem(Employee item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getInitials().toUpperCase(Locale.ROOT));
            }
        });
        teamList.setStyle(Style.list());

        Label title = new Label("Your activities");
        title.setStyle(Style.titleM());

        myActivityList = new ListView<>(myActivityItems);
        myActivityList.setCellFactory(v -> new ListCell<>() {
            @Override
            protected void updateItem(Activity item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName() + " (Week " + item.getStartWeek() + "-" + item.getEndWeek() + ")");
            }
        });
        myActivityList.setStyle(Style.list());

        Tab teamTab = new Tab("Team", teamList);
        teamTab.setClosable(false);

        VBox myBox = new VBox(8, title, myActivityList);
        VBox.setVgrow(myActivityList, Priority.ALWAYS);
        Tab myTab = new Tab("Your activities", myBox);
        myTab.setClosable(false);

        TabPane tabs = new TabPane(teamTab, myTab);
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.setStyle(Style.tabPane());

        VBox.setVgrow(tabs, Priority.ALWAYS);
        panel.getChildren().addAll(tabs);
        return panel;
    }

    private void loadProjects() {
        try {
            projectItems.setAll(service.getVisibleProjects(currentEmployee.getInitials()));
        } catch (Exception ex) {
            status("Failed to load projects: " + ex.getMessage());
            projectItems.clear();
        }
        if (projectItems.isEmpty()) {
            selectProject(null);
            return;
        }

        if (selectedProject != null) {
            Project refreshed = null;
            try {
                refreshed = service.getProjectForUser(selectedProject.getProjectID(), currentEmployee.getInitials());
            } catch (Exception ignored) {
                refreshed = null;
            }
            if (refreshed != null) {
                projectList.getSelectionModel().select(refreshed);
                selectProject(refreshed);
                return;
            }
        }

        projectList.getSelectionModel().select(0);
        selectProject(projectItems.get(0));
    }

    private void selectProject(Project project) {
        selectedProject = project;
        selectedActivity = null;

        if (project == null) {
            projectTitleLabel.setText("No project selected");
            projectInfoLabel.setText("Project ID: -  |  Leader: -  |  Created: -");
            projectViewersLabel.setText("Viewers: -");
            activityItems.clear();
            teamItems.clear();
            updateEditViewersButton();
            return;
        }

        String leader = project.getProjectLeader() == null
            ? "Unassigned"
            : project.getProjectLeader().getInitials().toUpperCase(Locale.ROOT);
        String created = project.getCreatedOn() == null ? "Unknown" : project.getCreatedOn().format(DATE_FMT);
        String viewers = project.getViewers().stream()
            .map(e -> e.getInitials().toUpperCase(Locale.ROOT))
            .sorted()
            .collect(Collectors.joining(", "));
        projectTitleLabel.setText(project.getName());
        projectInfoLabel.setText("Project ID: " + project.getProjectID() + "  |  Leader: " + leader + "  |  Created: " + created);
        projectViewersLabel.setText("Viewers: " + (viewers.isBlank() ? "none" : viewers));
        activityItems.setAll(project.getActivities());
        refreshTeamPanel();
        updateEditViewersButton();
        status("Project selected: " + project.getName());
    }

    private void updateEditViewersButton() {
        if (editViewersBtn == null) {
            return;
        }
        boolean isLeader = selectedProject != null
            && selectedProject.getProjectLeader() != null
            && currentEmployee != null
            && selectedProject.getProjectLeader().getInitials().equals(currentEmployee.getInitials());
        editViewersBtn.setDisable(!isLeader);
    }

    private void refreshTeamPanel() {
        if (selectedProject == null || selectedActivity == null) {
            teamItems.clear();
            return;
        }

        try {
            teamItems.setAll(service.getAssignedEmployees(
                selectedProject.getProjectID(),
                selectedActivity.getActivityID(),
                currentEmployee.getInitials())
            );
        } catch (Exception ex) {
            teamItems.clear();
            status("Failed to load team members: " + ex.getMessage());
        }
    }

    private void refreshMyActivities() {
        if (currentEmployee == null) {
            myActivityItems.clear();
            return;
        }
        myActivityItems.setAll(
            currentEmployee.getAssignedActivities().stream()
                .sorted(Comparator.comparingInt(Activity::getStartWeek))
                .collect(Collectors.toList())
        );
    }

    private void openCreateProjectDialog() {
        Dialog<ButtonType> dialog = baseDialog("Create project");
        TextField nameField = field("Project title");
        ComboBox<Employee> leaderBox = employeeComboBox();
        leaderBox.setPromptText("Project leader (optional)");

        dialog.getDialogPane().setContent(new VBox(10,
            wrapField("Title", nameField),
            wrapField("Assign project leader", leaderBox)
        ));

        dialog.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK) {
                return;
            }
            String name = nameField.getText() == null ? "" : nameField.getText().trim();
            if (name.isBlank()) {
                status("Project title cannot be empty");
                return;
            }
            try {
                Project p = service.createProject(name, currentEmployee.getInitials());
                if (leaderBox.getValue() != null) {
                    service.setProjectLeader(p.getProjectID(), leaderBox.getValue().getInitials(), currentEmployee.getInitials());
                }
                loadProjects();
                projectList.getSelectionModel().select(p);
                selectProject(p);
                status("Project created: " + name);
            } catch (Exception ex) {
                status("Failed to create project: " + ex.getMessage());
            }
        });
    }

    private void openCreateActivityDialog() {
        if (selectedProject == null) {
            status("Select a project first");
            return;
        }

        Dialog<ButtonType> dialog = baseDialog("Create activity");
        TextField nameField = field("Activity title");
        TextField startField = field("0");
        TextField endField = field("8");
        TextField hoursField = field("50");

        ListView<Employee> assignees = new ListView<>(FXCollections.observableArrayList());
        try {
            assignees.getItems().setAll(service.getAllEmployees(currentEmployee.getInitials()));
        } catch (Exception ex) {
            status("Failed to load employees: " + ex.getMessage());
        }
        assignees.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        assignees.setCellFactory(employeeCell());
        assignees.setStyle(Style.list());
        assignees.setPrefHeight(120);

        dialog.getDialogPane().setContent(new VBox(10,
            wrapField("Title", nameField),
            new HBox(10, wrapField("Start week", startField), wrapField("End week", endField)),
            wrapField("Estimated hours", hoursField),
            new Label("Assign employees"),
            assignees
        ));

        dialog.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK) {
                return;
            }
            try {
                String name = nonBlank(nameField.getText(), "Activity title is required");
                int start = Integer.parseInt(nonBlank(startField.getText(), "Start week is required"));
                int end = Integer.parseInt(nonBlank(endField.getText(), "End week is required"));
                int hours = Integer.parseInt(nonBlank(hoursField.getText(), "Estimated hours is required"));

                Activity created = service.addActivity(selectedProject.getProjectID(), name, hours, start, end, currentEmployee.getInitials());
                for (Employee e : assignees.getSelectionModel().getSelectedItems()) {
                    service.assignEmployee(selectedProject.getProjectID(), created.getActivityID(), e.getInitials(), currentEmployee.getInitials());
                }

                loadProjects();
                activityTable.getSelectionModel().select(created);
                selectedActivity = created;
                refreshTeamPanel();
                refreshMyActivities();
                status("Activity created: " + name);
            } catch (NumberFormatException ex) {
                status("Weeks and hours must be integers");
            } catch (Exception ex) {
                status("Failed to create activity: " + ex.getMessage());
            }
        });
    }

    private void openRegisterTimeDialog() {
        if (selectedProject == null || selectedActivity == null) {
            status("Select an activity first");
            return;
        }

        Dialog<ButtonType> dialog = baseDialog("Register time");
        ComboBox<Employee> employeeBox = employeeComboBox();
        employeeBox.setValue(currentEmployee);
        TextField dateField = field(LocalDate.now().format(DATE_FMT));
        TextField hoursField = field("8");

        dialog.getDialogPane().setContent(new VBox(10,
            wrapField("Employee", employeeBox),
            wrapField("Date (yyyy-MM-dd)", dateField),
            wrapField("Hours", hoursField)
        ));

        dialog.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK) {
                return;
            }
            try {
                Employee e = employeeBox.getValue();
                if (e == null) {
                    throw new IllegalArgumentException("Employee is required");
                }
                LocalDate date = LocalDate.parse(nonBlank(dateField.getText(), "Date is required"), DATE_FMT);
                int hours = Integer.parseInt(nonBlank(hoursField.getText(), "Hours are required"));

                service.registerTime(
                    selectedProject.getProjectID(),
                    selectedActivity.getActivityID(),
                    e.getInitials(),
                    new TimeEntry(date, hours, e, selectedActivity),
                    currentEmployee.getInitials()
                );
                loadProjects();
                refreshMyActivities();
                status("Time registered for " + e.getInitials());
            } catch (NumberFormatException ex) {
                status("Hours must be an integer");
            } catch (Exception ex) {
                status("Failed to register time: " + ex.getMessage());
            }
        });
    }

    private void generateReport() {
        if (selectedProject == null) {
            status("Select a project first");
            return;
        }

        StringBuilder outText = new StringBuilder();
        outText.append("Project Report\n");
        outText.append("Project: ").append(selectedProject.getName())
            .append(" (#").append(selectedProject.getProjectID()).append(")\n\n");

        for (Activity activity : selectedProject.getActivities()) {
            String assignees = "";
            try {
                assignees = service.getAssignedEmployees(
                    selectedProject.getProjectID(),
                    activity.getActivityID(),
                    currentEmployee.getInitials()
                ).stream()
                    .map(Employee::getInitials)
                    .sorted()
                    .collect(Collectors.joining(", "));
            } catch (Exception ex) {
                status("Failed to load assignees: " + ex.getMessage());
            }

            outText.append("- ").append(activity.getName())
                .append(" | weeks ").append(activity.getStartWeek()).append("-").append(activity.getEndWeek())
                .append(" | hours ").append(activity.getBudgetedHours())
                .append(" | assignees: ").append(assignees.isBlank() ? "none" : assignees)
                .append("\n");
        }

        Path out = Path.of(System.getProperty("user.home"), "project_" + selectedProject.getProjectID() + "_report.txt");
        try {
            Files.writeString(out, outText.toString());
            status("Report generated: " + out);
        } catch (IOException ex) {
            status("Failed to write report: " + ex.getMessage());
        }
    }

    private void openEditViewersDialog() {
        if (selectedProject == null) {
            status("Select a project first");
            return;
        }

        Dialog<ButtonType> dialog = baseDialog("Edit project viewers");
        ListView<Employee> viewersList = new ListView<>(FXCollections.observableArrayList());
        viewersList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        viewersList.setCellFactory(employeeCell());
        viewersList.setStyle(Style.list());
        viewersList.setPrefHeight(180);

        try {
            List<Employee> allEmployees = service.getAllEmployees(currentEmployee.getInitials());
            viewersList.getItems().setAll(allEmployees);

            Set<String> selectedInitials = new HashSet<>(
                service.getProjectViewers(selectedProject.getProjectID(), currentEmployee.getInitials()).stream()
                    .map(Employee::getInitials)
                    .collect(Collectors.toSet())
            );

            for (int i = 0; i < allEmployees.size(); i++) {
                if (selectedInitials.contains(allEmployees.get(i).getInitials())) {
                    viewersList.getSelectionModel().select(i);
                }
            }
        } catch (Exception ex) {
            status("Failed to load viewers: " + ex.getMessage());
            return;
        }

        Label helper = new Label("Only project leader can update this list.");
        helper.setStyle(Style.subtitle());

        dialog.getDialogPane().setContent(new VBox(10, helper, viewersList));

        dialog.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK) {
                return;
            }
            try {
                List<String> viewerInitials = viewersList.getSelectionModel().getSelectedItems().stream()
                    .map(Employee::getInitials)
                    .collect(Collectors.toList());

                service.setProjectViewers(selectedProject.getProjectID(), viewerInitials, currentEmployee.getInitials());
                loadProjects();
                status("Updated project viewers");
            } catch (Exception ex) {
                status("Failed to update viewers: " + ex.getMessage());
            }
        });
    }

    private void populateDemoData() {
        try {
            String result = DemoDataSeeder.populate(service, projectRepo, employeeRepo);
            loadProjects();
            refreshMyActivities();
            status(result);
        } catch (Exception ex) {
            status("Failed to populate demo data: " + ex.getMessage());
        }
    }

    private TextField field(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setStyle(Style.input());
        return field;
    }

    private ComboBox<Employee> employeeComboBox() {
        ComboBox<Employee> comboBox = new ComboBox<>(FXCollections.observableArrayList());
        try {
            comboBox.getItems().setAll(service.getAllEmployees(currentEmployee.getInitials()));
        } catch (Exception ex) {
            status("Failed to load employees: " + ex.getMessage());
        }
        comboBox.setCellFactory(employeeCell());
        comboBox.setButtonCell(employeeCell().call(null));
        comboBox.setMaxWidth(Double.MAX_VALUE);
        return comboBox;
    }

    private static Callback<ListView<Employee>, ListCell<Employee>> employeeCell() {
        return v -> new ListCell<>() {
            @Override
            protected void updateItem(Employee item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getInitials());
            }
        };
    }

    private Dialog<ButtonType> baseDialog(String title) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().setStyle(Style.dialog());

        Node okButtonNode = dialog.getDialogPane().lookupButton(ButtonType.OK);
        if (okButtonNode instanceof Button okButton) {
            okButton.setStyle(Style.primaryButton());
        }

        Node cancelButtonNode = dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        if (cancelButtonNode instanceof Button cancelButton) {
            cancelButton.setStyle(Style.ghostButton());
        }
        return dialog;
    }

    private VBox wrapField(String label, Control field) {
        Label l = new Label(label);
        l.setStyle(Style.formLabel());
        VBox box = new VBox(4, l, field);
        VBox.setVgrow(field, Priority.NEVER);
        return box;
    }

    private String nonBlank(String value, String error) {
        if (value == null || value.trim().isBlank()) {
            throw new IllegalArgumentException(error);
        }
        return value.trim();
    }

    private void status(String msg) {
        if (statusLabel != null) {
            statusLabel.setText(msg);
        }
    }

    public static void main(String[] args) { launch(); }
}
