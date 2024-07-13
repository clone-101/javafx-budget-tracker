package application;

import java.io.IOException;

import javafx.fxml.FXML;

public class AboutController {
	@FXML // switch to application main page
	private void switchToMain() throws IOException {
		App.setRoot("Main");
	}
}
