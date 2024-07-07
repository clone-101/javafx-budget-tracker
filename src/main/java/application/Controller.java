package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ListView;

public class Controller implements Initializable {

    @FXML
    private BarChart<String, Double> barChart;
    @FXML
    private ListView<String> trList = new ListView<String>();

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

    }

}
