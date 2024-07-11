package application;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class Controller implements Initializable {

    @FXML
    private BarChart<String, Double> barChart;
    @FXML
    private ListView<String> trList = new ListView<String>();

    @FXML
    private DatePicker trDate;
    @FXML
    private TextField trDescription;
    @FXML
    private TextField trFunds;
    @FXML
    private ComboBox trCategory;
    @FXML
    private ToggleGroup toggleGroup;
    @FXML
    private RadioButton trIncome, trExpense;

    @FXML
    private Button trClear, trSave, preferences, about, downloadCSV, uploadCSV;

    @FXML
    private void handleSaveButton(ActionEvent event) {
        // check if all fields are filled
        if (trDescription.getText().length() == 0 || trFunds.getText().length() == 0 || trDate.getValue() == null
                || toggleGroup.getSelectedToggle() == null || !isValidDouble(trFunds.getText())
                || trDate.getValue().isAfter(LocalDate.now())) {
            if (trDescription.getText().length() == 0) {
                trDescription.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
            }
            if (trFunds.getText().length() == 0) {
                trFunds.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
            }
            if (trDate.getValue() == null || trDate.getValue().isAfter(LocalDate.now())) {
                trDate.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
            }
            if (toggleGroup.getSelectedToggle() == null) {
                trIncome.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
                trExpense.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
            }
            if (!isValidDouble(trFunds.getText())) {
                trFunds.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
            }

            return; // stops save
        }

        // ********** save transaction **********
        String description = trDescription.getText();
        double funds = Double.parseDouble(trFunds.getText());
        if (trExpense.isSelected()) {
            funds *= -1;
        }
        Date date = Date.from(trDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
        String category = trCategory.getValue().toString();

        try { // add transaction to backend.Transaction
            new backend.Transaction(date, description, funds, category);

            // refresh list view
            String[] list = backend.Transaction.TransactionStrArr();
            trList.getItems().clear();
            trList.getItems().addAll(list);
            trList.refresh();

            // bar chart
            barChart.setAnimated(false); // disable clearing animation
            barChart.getData().clear();
            barChart.setAnimated(true);
            XYChart.Series[] series = backend.Charts.getBarSeries();
            barChart.getData().addAll(series[0], series[1]);

            handleClearButton(event); // clear form fields
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // only strictly positive doubles
    private boolean isValidDouble(String value) {
        double val = -1;
        try {
            val = Double.parseDouble(value);
        } catch (NumberFormatException e) {
        }
        return val > 0;
    }

    // changes Category comboBox based on radio button selection
    private void updateCategoryOnSelection() {
        trCategory.getItems().clear(); // clear comboBox
        if (trIncome.isSelected()) {
            trCategory.getItems().addAll(backend.Category.getIncomeNames());
        } else {
            trCategory.getItems().addAll(backend.Category.getExpenseNames());
        }
        trCategory.setValue("other"); // placeholder/default value
    }

    @FXML
    private void handleClearButton(ActionEvent event) {
        trDescription.clear(); // clear description
        trFunds.clear(); // clear funds
        // set radio buttons
        trIncome.setSelected(false);
        trExpense.setSelected(true);
        trDate.setValue(null); // clear date
        trCategory.setValue("other"); // clear comboBox

        // clear red borders
        trDescription.setStyle("");
        trFunds.setStyle("");
        trDate.setStyle("");
    }

    // switch scenes
    @FXML
    private void switchToPreferences() throws IOException {
        App.setRoot("Preferences");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // bar chart
        XYChart.Series[] series = backend.Charts.getBarSeries();
        barChart.getData().addAll(series[0], series[1]); // 0 is fundsIn, 1 is fundsOut

        // listView
        String[] list = backend.Transaction.TransactionStrArr();
        trList.getItems().addAll(list);

        // ********** transaction form **********
        trFunds.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isValidDouble(newValue)) {
                trFunds.setStyle(""); // Valid input, clear any custom styles
            } else {
                trFunds.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;"); // Invalid input, show error
                                                                                     // style
            }
        });
        trDescription.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() == 0) {
                trDescription.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
            } else {
                trDescription.setStyle("");
            }
        });

        trDate.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isAfter(LocalDate.now())) {
                trDate.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
            } else {
                trDate.setStyle("");
            }
        });

        trExpense.setSelected(true); // expense toggled by default

        // update category based on radio buttons
        trIncome.setOnAction(event -> updateCategoryOnSelection());
        trExpense.setOnAction(event -> updateCategoryOnSelection());

        // backend.Category.initialize();
        trCategory.getItems().addAll(backend.Category.getExpenseNames()); // expense by default
        trCategory.setValue("other");

    } // initialize()

    public static void shutdown() {
        try { // save to csv
            backend.Transaction.saveToFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} // Controller class
