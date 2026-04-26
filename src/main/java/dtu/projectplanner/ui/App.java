package dtu.projectplanner.ui;

import dtu.projectplanner.domain.Activity;
import dtu.projectplanner.domain.Employee;
import dtu.projectplanner.repository.EmployeeRepository;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.util.Comparator;
import java.util.Locale;
import java.util.stream.Collectors;

public class App extends Application {

    private static Scene scene;
    private static final EmployeeRepository employeeRepo = new EmployeeRepository();

    private final ObservableList<Activity> myActivityItems = FXCollections.observableArrayList();

    private Employee currentEmployee;
    private Label statusLabel;
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

        TextField initialsField = new TextField();
        initialsField.setPromptText("Your initials (e.g. huba)");
        initialsField.setStyle(Style.input());

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
        root.setCenter(buildMyActivitiesPanel());

        statusLabel = new Label("Ready");
        statusLabel.setStyle(Style.statusText());
        BorderPane.setMargin(statusLabel, new Insets(8, 4, 4, 4));
        root.setBottom(statusLabel);
        refreshMyActivities();

        return root;
    }

    private Node buildTopBar() {
        Label logo = new Label("Project Planner");
        logo.setStyle(Style.titleL());

        Label user = new Label("User: " + currentEmployee.getInitials());
        user.setStyle(Style.topBadge());

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle(Style.ghostButton());
        logoutBtn.setOnAction(e -> {
            currentEmployee = null;
            scene.setRoot(buildLoginView());
        });

        HBox left = new HBox(12, logo, user);
        left.setAlignment(Pos.CENTER_LEFT);

        HBox actions = new HBox(8, logoutBtn);
        actions.setAlignment(Pos.CENTER_RIGHT);

        BorderPane bar = new BorderPane();
        bar.setLeft(left);
        bar.setRight(actions);
        bar.setPadding(new Insets(8, 4, 10, 4));
        return bar;
    }

    private Node buildMyActivitiesPanel() {
        VBox panel = new VBox(10);
        panel.setMaxWidth(540);
        panel.setPadding(new Insets(14));
        panel.setStyle(Style.card());

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

        VBox.setVgrow(myActivityList, Priority.ALWAYS);
        panel.getChildren().addAll(title, myActivityList);
        return panel;
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

    private void status(String msg) {
        if (statusLabel != null) {
            statusLabel.setText(msg);
        }
    }

    public static void main(String[] args) { launch(); }
}
