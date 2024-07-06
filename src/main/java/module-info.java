module application {
    requires transitive javafx.controls;
    requires javafx.fxml;

    opens application to javafx.fxml;

    exports application;
}
