module application {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires com.opencsv;

    opens application to javafx.fxml;

    exports application;
}
