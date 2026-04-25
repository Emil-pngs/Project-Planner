module hellofx {
    requires transitive javafx.controls;
    requires javafx.fxml;
 
    opens dtu.projectplanner.ui to javafx.fxml; // Gives access to fxml files
    exports dtu.projectplanner.ui; // Exports the class inheriting from javafx.application.Application
    exports dtu.projectplanner.app; // Exports the main entry point
}