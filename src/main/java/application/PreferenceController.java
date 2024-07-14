package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import backend.Category;
import backend.Transaction;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

public class PreferenceController implements Initializable {

	// ************* category variables *************
	@FXML
	RadioButton expense, income;
	@FXML
	ToggleGroup categoryType;

	@FXML // textField for creating new category
	TextField createCategoryField;

	@FXML
	ComboBox<String> bulkAssignDescription, bulkAssignCategory, deleteCategory;

	@FXML
	Button createCategoryBtn, deleteCategoryBtn, bulkAssignBtn;

	@FXML // switch to application main page
	private void switchToMain() throws IOException {
		App.setRoot("Main");
	}

	// ************* category methods *************
	@FXML
	private void handleCreateCategory(ActionEvent event) {
		String name = createCategoryField.getText().trim().toLowerCase();
		if (name.length() == 0 || Category.isCategory(name, expense.isSelected())) {
			if (name.length() == 0) {
				createCategoryField.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
			}
			if (Category.isCategory(name, expense.isSelected())) {
				createCategoryField.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
			}
			return;
		}
		new Category(name, expense.isSelected());
		refresh(); // resets all fields

	} // handleCreateCategory()

	@FXML
	private void handleDeleteCategory(ActionEvent event) {
		Category deleting = Category.get(deleteCategory.getValue(), expense.isSelected());
		Category other = Category.get("other", expense.isSelected());

		if (deleting == null || deleting.equals(other)) {
			deleteCategory.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
			return;
		}
		Category.delete(deleting.getName(), expense.isSelected());
		refresh(); // resets all fields

	}// handleDeleteCategory()

	@FXML
	private void handleBulkAssign(ActionEvent event) {
		if (bulkAssignCategory.getValue() == null || bulkAssignDescription.getValue() == null
				|| bulkAssignDescription.getValue().length() == 0) {
			if (bulkAssignCategory.getValue() == null) {
				bulkAssignCategory.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
			}
			if (bulkAssignDescription.getValue() == null || bulkAssignDescription.getValue().length() == 0) {
				bulkAssignDescription.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
			}

			return; // stops if not all fields are filled/incorrect values
		}
		// reset style if correctly filled
		String description = bulkAssignDescription.getValue();
		Category category = Category.get(bulkAssignCategory.getValue(), expense.isSelected());

		Category.bulkReassign(description, category);
		bulkAssignDescription.setValue(null);
		bulkAssignCategory.setValue(null);

		bulkAssignCategory.setStyle("");
		bulkAssignDescription.setStyle("");

	} // handleBulkAssign()

	// ************* csv variables *************

	@FXML
	ComboBox<String> csvBox1, csvBox2, csvBox3, csvBox4;

	@FXML
	Button csvBtn;

	// ************* csv methods *************
	@FXML
	private void handleCSVPreferences(ActionEvent event) {
		if (csvBox1.getValue() == null || csvBox2.getValue() == null || csvBox3.getValue() == null
				|| csvBox4.getValue() == null) {
			if (csvBox1.getValue() == null) {
				csvBox1.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
			}
			if (csvBox2.getValue() == null) {
				csvBox2.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
			}
			if (csvBox3.getValue() == null) {
				csvBox3.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
			}
			if (csvBox4.getValue() == null) {
				csvBox4.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
			}
			return;
		} else if (csvBox1.getValue().equals(csvBox2.getValue()) || csvBox1.getValue().equals(csvBox3.getValue())
				|| csvBox1.getValue().equals(csvBox4.getValue()) || csvBox2.getValue().equals(csvBox3.getValue())
				|| csvBox2.getValue().equals(csvBox4.getValue()) || csvBox3.getValue().equals(csvBox4.getValue())) {
			if (csvBox1.getValue().equals(csvBox2.getValue()) || csvBox1.getValue().equals(csvBox3.getValue())
					|| csvBox1.getValue().equals(csvBox4.getValue())) {
				csvBox1.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
			}
			if (csvBox2.getValue().equals(csvBox3.getValue()) || csvBox2.getValue().equals(csvBox4.getValue())) {
				csvBox2.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
			}
			if (csvBox3.getValue().equals(csvBox4.getValue())) {
				csvBox3.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
			}
			return;
		}
		// clear red borders
		csvBox1.setStyle("");
		csvBox2.setStyle("");
		csvBox3.setStyle("");
		csvBox4.setStyle("");
		// assign values to order
		int[] order = { csvBox1.getSelectionModel().getSelectedIndex(), csvBox2.getSelectionModel().getSelectedIndex(),
				csvBox3.getSelectionModel().getSelectedIndex(), csvBox4.getSelectionModel().getSelectedIndex() };

		Transaction.setCSVOrder(order); // sets order in Transaction

		refresh(); // resets all fields
	} // handleCSVPreferences()

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// categories
		initializeChooseCategory();
		initializeCreateCategory();
		initializeDeleteCategory();
		initializeBulkAssign();

		// csv
		initializeCSVPreferences();

	} // initialize()

	// used to refresh all fields after an action
	private void refresh() {
		// refresh category preferences
		deleteCategory.getItems().clear();
		deleteCategory.getItems().addAll(Category.getCategoryNames(expense.isSelected()));
		createCategoryField.clear();
		bulkAssignCategory.getItems().clear();
		bulkAssignCategory.getItems().addAll(Category.getCategoryNames(expense.isSelected()));
		bulkAssignDescription.setValue(null);
		bulkAssignCategory.setValue(null);

		// refresh csv preferences
		csvBox1.setValue(null);
		csvBox2.setValue(null);
		csvBox3.setValue(null);
		csvBox4.setValue(null);

		// reset any red borders
		createCategoryField.setStyle("");
		deleteCategory.setStyle("");
		bulkAssignDescription.setStyle("");
		bulkAssignCategory.setStyle("");
		csvBox1.setStyle("");
		csvBox2.setStyle("");
		csvBox3.setStyle("");
		csvBox4.setStyle("");
	}

	// ************* initialization methods *************

	// set category type for comboBoxes
	private void initializeChooseCategory() {
		expense.setOnAction(event -> {
			deleteCategory.getItems().clear();
			deleteCategory.getItems().addAll(Category.getCategoryNames(true));
		});
		income.setOnAction(event -> {
			deleteCategory.getItems().clear();
			deleteCategory.getItems().addAll(Category.getCategoryNames(false));
		});
	} // initializeChooseCategory()

	private void initializeCreateCategory() {
		createCategoryField.setPromptText("name");
		// add listener
		createCategoryField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.length() == 0) {
				createCategoryField.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
			} else {
				createCategoryField.setStyle("");
			}
		});

		createCategoryField.setOnKeyPressed(event -> {
			if (event.getCode().toString().equals("ENTER")) {
				createCategoryBtn.fire();
			}
		});
	} // initializeCreateCategory()

	private void initializeDeleteCategory() {
		deleteCategory.getItems().clear();
		deleteCategory.getItems().addAll(Category.getCategoryNames(expense.isSelected()));
		// add listener
		deleteCategory.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == null || newValue.toString().equals("other")) {
				deleteCategory.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
			} else {
				deleteCategory.setStyle("");
			}
		});
	} // initializeDeleteCategory()

	private void initializeBulkAssign() {
		bulkAssignDescription.getItems().clear();
		bulkAssignDescription.getItems().addAll(Category.getDescriptions());
		bulkAssignCategory.getItems().addAll(Category.getCategoryNames(expense.isSelected()));
		// add listener
		bulkAssignCategory.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == null) {
				bulkAssignCategory.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
			} else {
				bulkAssignCategory.setStyle("");

			}
		});
	} // initializeBulkAssign()

	private void initializeCSVPreferences() {
		String[] csvOrder = Transaction.getCSVOrderStr();
		// clear comboBoxes
		csvBox1.getItems().clear();
		csvBox2.getItems().clear();
		csvBox3.getItems().clear();
		csvBox4.getItems().clear();

		// add values to comboBoxes
		csvBox1.getItems().addAll(csvOrder);
		csvBox2.getItems().addAll(csvOrder);
		csvBox3.getItems().addAll(csvOrder);
		csvBox4.getItems().addAll(csvOrder);

		// set prompt text as current order
		csvBox1.setPromptText(csvOrder[0]);
		csvBox2.setPromptText(csvOrder[1]);
		csvBox3.setPromptText(csvOrder[2]);
		csvBox4.setPromptText(csvOrder[3]);

		csvBox1.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == null || newValue.length() == 0 || newValue.equals(csvBox2.getValue())
					|| newValue.equals(csvBox3.getValue()) || newValue.equals(csvBox4.getValue())) {
				csvBox1.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
			} else {
				csvBox1.setStyle("");
			}
		});
		csvBox2.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == null || newValue.length() == 0 || newValue.equals(csvBox1.getValue())
					|| newValue.equals(csvBox3.getValue()) || newValue.equals(csvBox4.getValue())) {
				csvBox2.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
			} else {
				csvBox2.setStyle("");
			}
		});
		csvBox3.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == null || newValue.length() == 0 || newValue.equals(csvBox1.getValue())
					|| newValue.equals(csvBox2.getValue()) || newValue.equals(csvBox4.getValue())) {
				csvBox3.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
			} else {
				csvBox3.setStyle("");
			}
		});
		csvBox4.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == null || newValue.length() == 0 || newValue.equals(csvBox1.getValue())
					|| newValue.equals(csvBox2.getValue()) || newValue.equals(csvBox3.getValue())) {
				csvBox4.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
			} else {
				csvBox4.setStyle("");
			}
		});

	} // initializeCSVPreferences()

} // PreferenceController
