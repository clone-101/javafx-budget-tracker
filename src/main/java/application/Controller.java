package application;

import java.io.IOException;

import javafx.fxml.FXML;

public class Controller {

    @FXML
    private void switchToCategories() throws IOException {
        App.setRoot("Categories");
    }

    private void switchToMain() throws IOException {
        App.setRoot("Main");
    }
}
