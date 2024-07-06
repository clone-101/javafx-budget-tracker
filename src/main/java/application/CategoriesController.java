package application;

import java.io.IOException;

import javafx.fxml.FXML;

public class CategoriesController {

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("Main");
    }
}