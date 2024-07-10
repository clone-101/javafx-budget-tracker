package application;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
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

            return;
        }

        // get values from form
        String description = trDescription.getText();
        double funds = Double.parseDouble(trFunds.getText());
        if (trExpense.isSelected()) {
            funds *= -1;
        }
        Date date = Date.from(trDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
        String category = trCategory.getValue().toString();

        // add transaction to backend.Transaction
        try {

            new backend.Transaction(description, category, funds, null, date);

            // refresh list view
            // not super elegant but it works
            String[] list = backend.Transaction.TransactionStrArr();
            trList.getItems().clear();
            trList.getItems().addAll(list);
            trList.refresh();

            // bar chart
            // disable clear animation
            barChart.setAnimated(false);
            barChart.getData().clear();
            barChart.setAnimated(true);
            XYChart.Series[] series = backend.Charts.getBarSeries();
            barChart.getData().addAll(series[0], series[1]);

            // clear if all goes well
            handleClearButton(event);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean isValidDouble(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @FXML
    private void handleClearButton(ActionEvent event) {
        trDescription.clear();
        trFunds.clear();
        trIncome.setSelected(false);
        trExpense.setSelected(false);
        trIncome.setStyle("");
        trExpense.setStyle("");
        trDate.setValue(null);

        trDescription.setStyle("");
        trFunds.setStyle("");
        trDate.setStyle("");
        trCategory.setValue("other");
    }

    // switch scenes
    @FXML
    private void switchToPreferences() throws IOException {
        App.setRoot("Preferences");
    }

    @FXML
    private void switchToMain() throws IOException {
        App.setRoot("Main");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // temporary running of import on SIMPLII.csv
        try {
            test.Test.initializeCSV();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // gets series from backend.Charts 0: fundsIn, 1: fundsOut
        XYChart.Series[] series = backend.Charts.getBarSeries();
        barChart.getData().addAll(series[0], series[1]);

        // listView
        String[] list = backend.Transaction.TransactionStrArr();
        trList.getItems().addAll(list);

        // ********** transaction form **********
        trFunds.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isValidDouble(newValue)) {
                trFunds.setStyle(""); // Valid input, clear any custom styles
            } else {
                trFunds.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;"); // Invalid input, show error style
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
            if (newValue == null) {
                trDate.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
            } else {
                trDate.setStyle("");
            }
        });

        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                trIncome.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
                trExpense.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
            } else {
                trIncome.setStyle("");
                trExpense.setStyle("");
            }
        });

        // categories
        ArrayList<String> categories = backend.Category.getNames();
        backend.Category.initialize();
        trCategory.getItems().addAll(categories);
        trCategory.setValue("other");

    } // initialize()

} // Controller
