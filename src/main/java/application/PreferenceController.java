package application;

import java.io.IOException;

import javafx.fxml.FXML;

public class PreferenceController {
	@FXML
	private void switchToMain() throws IOException {
		App.setRoot("Main");
	}
}
