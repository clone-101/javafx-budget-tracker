package application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

import backend.Category;
import backend.Charts;
import backend.Transaction;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;

@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
public class Controller implements Initializable {
    // for Category methods
    private static final boolean EXPENSE = true, INCOME = false;

    @FXML
    private BarChart<String, Double> barChart;
    @FXML
    private PieChart pieChart;
    @FXML
    private ListView<Transaction> trList = new ListView<Transaction>();

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
    private void handleUploadCSV(ActionEvent event) {
        File file;
        FileChooser fileChooser = new FileChooser();
        String path = System.getProperty("user.home");
        fileChooser.setInitialDirectory(new File(path));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        if ((file = fileChooser.showOpenDialog(null)) != null) {
            try {
                Transaction.readFile(file, Transaction.getCSVOrder());
                refreshListView();
                initializeBarChart();
                initializePieChart();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @FXML
    private void handleSaveButton(ActionEvent event) {
        String description, category;
        double funds;
        Date date;

        // check if all fields are filled
        if (!validateTrForm())
            return;

        description = trDescription.getText(); // can be blank

        funds = Double.parseDouble(trFunds.getText());
        funds = trExpense.isSelected() ? -1 * funds : funds;

        date = Date.from(trDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

        category = trCategory.getValue().toString();

        try { // add transaction to Transaction
            new Transaction(date, description, funds, category);

            refreshListView(); // listView
            initializeBarChart(); // bar chart
            initializePieChart(); // pie chart
            handleClearButton(event); // clear form fields

        } catch (Exception e) {
            e.printStackTrace();
        }
    } // handleSaveButton()

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

    @FXML
    private void switchToAbout() throws IOException {
        App.setRoot("About");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Transaction.initializeCSV(); // initialize memory from csv
        initializeBarChart(); // bar chart
        initializePieChart(); // pie chart
        initializeListView(); // listView
        initializeTrForm(); // transaction form

    } // initialize()

    public static void shutdown() {
        try { // save to csv
            Transaction.saveToFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // ********** initialize methods **********

    public void refresh() {
        initializeBarChart(); // bar chart
        initializePieChart(); // pie chart
        refreshListView(); // listView

        // clear form fields
        trDate.setValue(new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        trFunds.clear();
        trCategory.getItems().clear();
    }

    private void initializeBarChart() {
        barChart.setAnimated(false); // disable clearing animation
        barChart.getData().clear();
        barChart.setAnimated(true);
        XYChart.Series[] series = Charts.getBarSeries(); // 0 is fundsIn, 1 is fundsOut
        barChart.getData().addAll(series[0], series[1]);
    }

    private void initializePieChart() {
        pieChart.getData().clear();
        pieChart.getData().addAll(Charts.getPieData());
    }

    private void initializeListView() {
        Transaction[] list = Transaction.getTransactions();
        trList.getItems().clear();
        trList.getItems().addAll(list);
        trList.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.BACK_SPACE) {
                Transaction tr = trList.getSelectionModel().getSelectedItem();
                if (tr != null) {
                    tr.delete(tr);
                    refreshListView();
                    initializeBarChart();
                    initializePieChart();
                    if (!trList.getItems().isEmpty())
                        trList.getSelectionModel().selectFirst();
                }
            }
        });
    }

    private void initializeTrForm() {

        trFunds.clear();
        trFunds.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isValidDouble(newValue)) {
                trFunds.setStyle(""); // Valid input, clear any custom styles
            } else {
                trFunds.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;"); // Invalid input, show error style
            }
        });
        trFunds.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                trSave.fire();
            }
        });

        trDate.setValue(new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        trDate.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isAfter(LocalDate.now())) {
                trDate.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
            } else {
                trDate.setStyle("");
            }
        });
        trDate.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                trSave.fire();
            }
        });

        // update category based on radio buttons
        trIncome.setOnAction(event -> updateCategoryOnSelection());
        trExpense.setOnAction(event -> updateCategoryOnSelection());

        trCategory.getItems().clear();
        trCategory.getItems().addAll(Category.getCategoryNames(EXPENSE));
        trCategory.setValue("other");
        trCategory.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                trSave.fire();
            }
        });

    }

    // ********** helper methods **********

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
        trCategory.getItems().addAll(Category.getCategoryNames(trExpense.isSelected()));
        trCategory.setValue("other"); // placeholder/default value
    }

    public void refreshListView() {
        Transaction[] list = Transaction.getTransactions();
        trList.getItems().clear();
        trList.getItems().addAll(list);
        trList.refresh();
    }

    // validates transaction form and deals with red borders
    private boolean validateTrForm() {
        if (trFunds.getText().length() == 0 || trDate.getValue() == null
                || toggleGroup.getSelectedToggle() == null || !isValidDouble(trFunds.getText())
                || trDate.getValue().isAfter(LocalDate.now())) {
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

            return false; // stops save
        }
        return true;
    }
} // Controller class
