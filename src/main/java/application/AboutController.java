package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.text.TextFlow;

public class AboutController implements Initializable {
	private static final String ABOUT_STRING = "The Budget Tracker is a desktop application built with JavaFX to help visualize and keep record of personal finance."
			+ "It offers a simple visual understanding of a users income and expenses."
			+ "Users can quickly log transactions, categorize them for better financial planning, and visualize their spending patterns over time through charts and reports."
			+ "\n\n Key features include: \n\n" + "- Easy-to-use interface for recording transactions\n"
			+ "- Customizable categories for organizing financial transactions\n"
			+ "- Visual analytics for tracking spending habits\n"
			+ "- The ability to upload transactions directly from CSV files";

	@FXML
	TextFlow about = new TextFlow();

	@FXML // switch to application main page
	private void switchToMain() throws IOException {
		App.setRoot("Main");
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		Label aboutText = new Label(ABOUT_STRING);

		aboutText.setWrapText(true);
		aboutText.setMaxWidth(450);
		about.getChildren().add(aboutText);

	}
}
