package dtu.projectplanner.ui;

import dtu.projectplanner.app.ProjectPlanningService;
import dtu.projectplanner.app.DemoDataSeeder;
import dtu.projectplanner.domain.Activity;
import dtu.projectplanner.domain.ActivityStatus;
import dtu.projectplanner.domain.Employee;
import dtu.projectplanner.domain.Project;
import dtu.projectplanner.domain.TimeEntry;
import dtu.projectplanner.repository.EmployeeRepository;
import dtu.projectplanner.repository.ProjectRepository;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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

    private boolean darkMode = false;

    private Label projectTitleLabel;
    private Label projectInfoLabel;
    private Label projectViewersLabel;
    private Label statusLabel;
    private Button createActivityBtn;
    private Button editActivityBtn;
    private Button editViewersBtn;
    private ListView<Project> projectList;
    private TableView<Activity> activityTable;
    private ListView<Employee> teamList;
    private ListView<Activity> myActivityList;

    private static final class EmployeeSelectionPanel {
        private final VBox container;
        private final List<Employee> allEmployees;
        private final Map<String, SimpleBooleanProperty> selectedState;

        private EmployeeSelectionPanel(VBox container, List<Employee> allEmployees, Map<String, SimpleBooleanProperty> selectedState) {
            this.container = container;
            this.allEmployees = allEmployees;
            this.selectedState = selectedState;
        }

        private VBox container() {
            return container;
        }

        private List<String> selectedInitials() {
            return allEmployees.stream()
                .filter(employee -> selectedState.get(employee.getInitials()).get())
                .map(Employee::getInitials)
                .collect(Collectors.toList());
        }
    }

    @Override
    public void start(Stage stage) {
        var bounds = Screen.getPrimary().getVisualBounds();
        double w = bounds.getWidth() * 0.8;
        double h = bounds.getHeight() * 0.8;

        scene = new Scene(buildLoginView(), w, h);
        var themeUrl = App.class.getResource("/dtu/projectplanner/ui/theme.css");
        if (themeUrl != null) {
            scene.getStylesheets().add(themeUrl.toExternalForm());
        }
        stage.setScene(scene);
        stage.setTitle("Project Planner");
        stage.centerOnScreen();
        stage.show();
    }

    private Pane buildLoginView() {
        VBox page = new VBox(24);
        page.setAlignment(Pos.CENTER);
        page.setPadding(new Insets(40));
        page.getStyleClass().add("login-background");

        VBox card = new VBox(16);
        card.setMaxWidth(420);
        card.setPadding(new Insets(28));
        card.getStyleClass().add("login-card");

        Label title = new Label("Project Planner");
        title.getStyleClass().add("title-xl");

        Label subtitle = new Label("Sign in with at least 4 letters.");
        subtitle.getStyleClass().add("subtitle-text");

        TextField initialsField = field("Your initials (e.g. huba)");

        Label validation = new Label();
        validation.getStyleClass().add("error-text");

        Button loginBtn = new Button("Login");
        loginBtn.getStyleClass().add("btn-primary");
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
            Pane main = buildMainView();
            scene.setRoot(main);
            applyTheme(main);
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
        root.getStyleClass().add("root-background");

        root.setTop(buildTopBar());
        root.setLeft(buildProjectPanel());
        root.setCenter(buildActivityPanel());
        root.setRight(buildSidePanel());

        statusLabel = new Label("Ready");
        statusLabel.getStyleClass().add("status-text");
        BorderPane.setMargin(statusLabel, new Insets(8, 4, 4, 4));
        root.setBottom(statusLabel);

        loadProjects();
        refreshMyActivities();

        return root;
    }

    private Node buildTopBar() {
        Label logo = new Label("Project Planner");
        logo.getStyleClass().add("title-l");

        Label user = new Label("User: " + currentEmployee.getInitials());
        user.getStyleClass().add("top-badge");

        Button createProjectBtn = actionButton("Create project", e -> openCreateProjectDialog(), true);
        createActivityBtn = actionButton("Create activity", e -> openCreateActivityDialog(), false);
        editActivityBtn = actionButton("Edit activity", e -> openEditActivityDialog(), false);
        Button registerTimeBtn = actionButton("Register time", e -> openRegisterTimeDialog(), false);
        Button reportBtn = actionButton("Generate report", e -> generateReport(), false);
        editViewersBtn = actionButton("Edit viewers", e -> openEditViewersDialog(), false);
        Button populateBtn = actionButton("Populate demo data", e -> populateDemoData(), false);

        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().add("btn-ghost");
        logoutBtn.getStyleClass().add("topbar-button");
        logoutBtn.setOnAction(e -> {
            currentEmployee = null;
            selectedProject = null;
            selectedActivity = null;
            Pane login = buildLoginView();
            scene.setRoot(login);
            applyTheme(login);
        });

        Button darkModeBtn = new Button(darkMode ? "Light mode" : "Dark mode");
        darkModeBtn.getStyleClass().addAll("btn-ghost", "topbar-button");
        darkModeBtn.setOnAction(e -> {
            darkMode = !darkMode;
            darkModeBtn.setText(darkMode ? "Light mode" : "Dark mode");
            applyTheme(scene.getRoot());
        });

        HBox left = new HBox(12, logo, user);
        left.setAlignment(Pos.CENTER_LEFT);

        HBox row1 = new HBox(8, createProjectBtn, createActivityBtn, editActivityBtn, editViewersBtn);
        row1.setAlignment(Pos.CENTER_RIGHT);

        HBox row2 = new HBox(8, registerTimeBtn, reportBtn, populateBtn, darkModeBtn, logoutBtn);
        row2.setAlignment(Pos.CENTER_RIGHT);

        VBox actions = new VBox(4, row1, row2);
        actions.setAlignment(Pos.CENTER_RIGHT);

        BorderPane bar = new BorderPane();
        bar.setLeft(left);
        bar.setRight(actions);
        bar.setPadding(new Insets(8, 4, 8, 4));
        updateLeaderOnlyButtons();
        return bar;
    }

    private Button actionButton(String text, EventHandler<ActionEvent> handler, boolean primary) {
        Button button = new Button(text);
        button.getStyleClass().add(primary ? "btn-primary" : "btn-secondary");
        button.getStyleClass().add("topbar-button");
        button.setOnAction(handler);
        return button;
    }

    private Node buildProjectPanel() {
        VBox panel = new VBox(10);
        panel.setPrefWidth(250);
        panel.setMaxWidth(290);
        panel.setPadding(new Insets(14));
        panel.getStyleClass().add("card-surface");

        Label title = new Label("Projects");
        title.getStyleClass().add("title-m");

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
                nameLabel.getStyleClass().add("project-name-small");

                Label metaLabel = new Label("Leader: " + leader + "  |  Created: " + created);
                metaLabel.getStyleClass().add("project-meta-small");

                VBox content = new VBox(2, nameLabel, metaLabel);
                content.setPadding(new Insets(2, 0, 2, 0));
                setGraphic(content);
                setText(null);
            }
        });
        projectList.getStyleClass().add("list-control");
        projectList.getSelectionModel().selectedItemProperty().addListener((obs, oldProject, newProject) -> selectProject(newProject));

        VBox.setVgrow(projectList, Priority.ALWAYS);
        panel.getChildren().addAll(title, projectList);
        BorderPane.setMargin(panel, new Insets(0, 12, 0, 0));
        return panel;
    }

    private Node buildActivityPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(14));
        panel.getStyleClass().add("card-surface");

        projectTitleLabel = new Label("No project selected");
        projectTitleLabel.getStyleClass().add("title-m");

        projectInfoLabel = new Label("Project ID: -  |  Leader: -  |  Created: -");
        projectInfoLabel.getStyleClass().add("project-meta-small");

        projectViewersLabel = new Label("Viewers: -");
        projectViewersLabel.getStyleClass().add("project-meta-small");

        Label headerTag = new Label("PROJECT DETAILS");
        headerTag.getStyleClass().add("project-header-label");

        VBox projectHeader = new VBox(3, headerTag, projectTitleLabel, projectInfoLabel, projectViewersLabel);

        activityTable = new TableView<>(activityItems);
        activityTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        activityTable.setPlaceholder(new Label("No activities yet"));
        activityTable.getStyleClass().addAll("table-control", "activity-table");

        TableColumn<Activity, String> nameCol = new TableColumn<>("Activity");
        nameCol.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getName()));

        TableColumn<Activity, Number> startCol = new TableColumn<>("Start Week");
        startCol.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getStartWeek()));

        TableColumn<Activity, Number> endCol = new TableColumn<>("End Week");
        endCol.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getEndWeek()));

        TableColumn<Activity, Number> hoursCol = new TableColumn<>("Hours");
        hoursCol.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getBudgetedHours()));

        TableColumn<Activity, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getStatus().name().replace('_', ' ')));

        activityTable.getColumns().setAll(nameCol, startCol, endCol, hoursCol, statusCol);
        activityTable.getSelectionModel().selectedItemProperty().addListener((obs, oldActivity, newActivity) -> {
            selectedActivity = newActivity;
            refreshTeamPanel();
            updateLeaderOnlyButtons();
        });

        VBox.setVgrow(activityTable, Priority.ALWAYS);
        panel.getChildren().addAll(projectHeader, activityTable);
        BorderPane.setMargin(panel, new Insets(0, 12, 0, 0));
        return panel;
    }

    private Node buildSidePanel() {
        VBox panel = new VBox(10);
        panel.setPrefWidth(280);
        panel.setMaxWidth(320);
        panel.setPadding(new Insets(14));
        panel.getStyleClass().add("card-surface");

        teamList = new ListView<>(teamItems);
        teamList.setPlaceholder(new Label("Select an activity"));
        teamList.setCellFactory(v -> new ListCell<>() {
            @Override
            protected void updateItem(Employee item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getInitials().toUpperCase(Locale.ROOT));
            }
        });
        teamList.getStyleClass().add("list-control");

        myActivityList = new ListView<>(myActivityItems);
        myActivityList.setCellFactory(v -> new ListCell<>() {
            @Override
            protected void updateItem(Activity item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName() + " (Week " + item.getStartWeek() + "-" + item.getEndWeek() + ")");
            }
        });
        myActivityList.getStyleClass().add("list-control");

        Tab teamTab = new Tab("Team", teamList);
        teamTab.setClosable(false);

        Tab myTab = new Tab("Your activities", myActivityList);
        myTab.setClosable(false);

        TabPane tabs = new TabPane(teamTab, myTab);
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.getStyleClass().add("tab-pane-custom");

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
            updateLeaderOnlyButtons();
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
        updateLeaderOnlyButtons();
        status("Project selected: " + project.getName());
    }

    private void updateLeaderOnlyButtons() {
        if (editViewersBtn == null || createActivityBtn == null || editActivityBtn == null) {
            return;
        }
        boolean isLeader = selectedProject != null
            && selectedProject.getProjectLeader() != null
            && currentEmployee != null
            && selectedProject.getProjectLeader().getInitials().equals(currentEmployee.getInitials());

        createActivityBtn.setDisable(!isLeader);
        editActivityBtn.setDisable(!isLeader || selectedActivity == null);
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

        if (selectedProject.getProjectLeader() == null
            || !selectedProject.getProjectLeader().getInitials().equals(currentEmployee.getInitials())) {
            status("Only the project leader can create activities");
            return;
        }

        Dialog<ButtonType> dialog = baseDialog("Create activity");
        TextField nameField = field("Activity title");
        TextField startField = field("0");
        TextField endField = field("8");
        TextField hoursField = field("50");

        List<Employee> allEmployees;
        try {
            allEmployees = service.getAllEmployees(currentEmployee.getInitials());
        } catch (Exception ex) {
            status("Failed to load employees: " + ex.getMessage());
            return;
        }

        EmployeeSelectionPanel assigneePanel = employeeCheckboxPanel(
            allEmployees,
            Set.of(),
            "Search initials or name",
            "Check employees to assign to this activity."
        );

        dialog.getDialogPane().setContent(new VBox(10,
            wrapField("Title", nameField),
            new HBox(10, wrapField("Start week", startField), wrapField("End week", endField)),
            wrapField("Estimated hours", hoursField),
            assigneePanel.container()
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
                for (String initials : assigneePanel.selectedInitials()) {
                    service.assignEmployee(selectedProject.getProjectID(), created.getActivityID(), initials, currentEmployee.getInitials());
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

    private void openEditActivityDialog() {
        if (selectedProject == null || selectedActivity == null) {
            status("Select an activity first");
            return;
        }

        if (selectedProject.getProjectLeader() == null
            || !selectedProject.getProjectLeader().getInitials().equals(currentEmployee.getInitials())) {
            status("Only the project leader can edit activities");
            return;
        }

        Dialog<ButtonType> dialog = baseDialog("Edit activity");
        TextField nameField = field(selectedActivity.getName());
        TextField startField = field(String.valueOf(selectedActivity.getStartWeek()));
        TextField endField = field(String.valueOf(selectedActivity.getEndWeek()));
        TextField hoursField = field(String.valueOf(selectedActivity.getBudgetedHours()));

        ComboBox<ActivityStatus> statusBox = new ComboBox<>(FXCollections.observableArrayList(ActivityStatus.values()));
        statusBox.setValue(selectedActivity.getStatus());
        statusBox.getStyleClass().add("input-field");
        statusBox.setMaxWidth(Double.MAX_VALUE);

        Set<String> existingAssigneeInitials = new HashSet<>();
        List<Employee> allEmployees;
        try {
            allEmployees = service.getAllEmployees(currentEmployee.getInitials());

            existingAssigneeInitials.addAll(
                service.getAssignedEmployees(
                    selectedProject.getProjectID(),
                    selectedActivity.getActivityID(),
                    currentEmployee.getInitials())
                    .stream()
                    .map(Employee::getInitials)
                    .collect(Collectors.toSet())
            );
        } catch (Exception ex) {
            status("Failed to load assignees: " + ex.getMessage());
            return;
        }

        EmployeeSelectionPanel assigneePanel = employeeCheckboxPanel(
            allEmployees,
            existingAssigneeInitials,
            "Search initials or name",
            "Check employees assigned to this activity."
        );

        dialog.getDialogPane().setContent(new VBox(10,
            wrapField("Title", nameField),
            new HBox(10, wrapField("Start week", startField), wrapField("End week", endField)),
            wrapField("Estimated hours", hoursField),
            wrapField("Status", statusBox),
            assigneePanel.container()
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
                ActivityStatus status = statusBox.getValue() == null ? ActivityStatus.PLANNED : statusBox.getValue();

                service.editActivity(
                    selectedProject.getProjectID(),
                    selectedActivity.getActivityID(),
                    name,
                    hours,
                    start,
                    end,
                    status,
                    currentEmployee.getInitials()
                );

                Set<String> updatedAssigneeInitials = new HashSet<>(assigneePanel.selectedInitials());

                for (String initials : updatedAssigneeInitials) {
                    if (!existingAssigneeInitials.contains(initials)) {
                        service.assignEmployee(
                            selectedProject.getProjectID(),
                            selectedActivity.getActivityID(),
                            initials,
                            currentEmployee.getInitials()
                        );
                    }
                }

                for (String initials : existingAssigneeInitials) {
                    if (!updatedAssigneeInitials.contains(initials)) {
                        service.unassignEmployee(
                            selectedProject.getProjectID(),
                            selectedActivity.getActivityID(),
                            initials,
                            currentEmployee.getInitials()
                        );
                    }
                }

                int keepActivityId = selectedActivity.getActivityID();
                loadProjects();
                Activity refreshed = selectedProject == null ? null : selectedProject.findActivityByID(keepActivityId);
                activityTable.getSelectionModel().select(refreshed);
                selectedActivity = refreshed;
                refreshTeamPanel();
                refreshMyActivities();
                updateLeaderOnlyButtons();
                status("Activity updated: " + name);
            } catch (NumberFormatException ex) {
                status("Weeks and hours must be integers");
            } catch (Exception ex) {
                status("Failed to edit activity: " + ex.getMessage());
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

        List<Employee> allEmployees;
        Set<String> selectedInitials;
        try {
            allEmployees = service.getAllEmployees(currentEmployee.getInitials());
            selectedInitials = new HashSet<>(
                service.getProjectViewers(selectedProject.getProjectID(), currentEmployee.getInitials()).stream()
                    .map(Employee::getInitials)
                    .collect(Collectors.toSet())
            );
        } catch (Exception ex) {
            status("Failed to load viewers: " + ex.getMessage());
            return;
        }

        EmployeeSelectionPanel viewerPanel = employeeCheckboxPanel(
            allEmployees,
            selectedInitials,
            "Search initials or name",
            "Only project leader can update this list. Use search and check the people who should view the project."
        );

        dialog.getDialogPane().setContent(viewerPanel.container());

        dialog.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK) {
                return;
            }
            try {
                List<String> viewerInitials = viewerPanel.selectedInitials();

                service.setProjectViewers(selectedProject.getProjectID(), viewerInitials, currentEmployee.getInitials());
                loadProjects();
                status("Updated project viewers");
            } catch (Exception ex) {
                status("Failed to update viewers: " + ex.getMessage());
            }
        });
    }

    private EmployeeSelectionPanel employeeCheckboxPanel(
        List<Employee> allEmployees,
        Set<String> preselectedInitials,
        String searchPrompt,
        String helperText
    ) {
        TextField searchField = field(searchPrompt);

        FilteredList<Employee> filtered = new FilteredList<>(FXCollections.observableArrayList(allEmployees), e -> true);
        ListView<Employee> listView = new ListView<>(filtered);

        Map<String, SimpleBooleanProperty> selectedState = new HashMap<>();
        for (Employee employee : allEmployees) {
            selectedState.put(employee.getInitials(), new SimpleBooleanProperty(preselectedInitials.contains(employee.getInitials())));
        }

        listView.setCellFactory(CheckBoxListCell.forListView(employee -> {
            SimpleBooleanProperty prop = selectedState.get(employee.getInitials());
            if (prop == null) {
                prop = new SimpleBooleanProperty(false);
                selectedState.put(employee.getInitials(), prop);
            }
            return prop;
        }, new StringConverter<>() {
            @Override
            public String toString(Employee employee) {
                if (employee == null) {
                    return "";
                }
                return employee.getInitials().toUpperCase(Locale.ROOT) + " - " + employee.getName();
            }

            @Override
            public Employee fromString(String string) {
                return null;
            }
        }));
        listView.getStyleClass().add("list-control");
        listView.setPrefHeight(180);

        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            String query = newValue == null ? "" : newValue.trim().toLowerCase(Locale.ROOT);
            filtered.setPredicate(employee -> {
                if (query.isBlank()) {
                    return true;
                }
                return employee.getInitials().toLowerCase(Locale.ROOT).contains(query)
                    || employee.getName().toLowerCase(Locale.ROOT).contains(query);
            });
        });

        Label helper = new Label(helperText);
        helper.getStyleClass().add("subtitle-text");

        VBox container = new VBox(10, helper, searchField, listView);
        return new EmployeeSelectionPanel(container, allEmployees, selectedState);
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
        field.getStyleClass().add("input-field");
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
        dialog.getDialogPane().getStyleClass().add("dialog-pane-custom");

        Node okButtonNode = dialog.getDialogPane().lookupButton(ButtonType.OK);
        if (okButtonNode instanceof Button okButton) {
            okButton.getStyleClass().add("btn-primary");
        }

        Node cancelButtonNode = dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        if (cancelButtonNode instanceof Button cancelButton) {
            cancelButton.getStyleClass().add("btn-ghost");
        }
        return dialog;
    }

    private VBox wrapField(String label, Control field) {
        Label l = new Label(label);
        l.getStyleClass().add("form-label");
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

    private void applyTheme(Node root) {
        if (darkMode) {
            if (!root.getStyleClass().contains("dark")) root.getStyleClass().add("dark");
        } else {
            root.getStyleClass().remove("dark");
        }
    }

    private void status(String msg) {
        if (statusLabel != null) {
            statusLabel.setText(msg);
        }
    }

    public static void main(String[] args) { launch(); }
}
