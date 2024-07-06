package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

public class Controller implements Initializable {

    @FXML
    private BarChart<String, Integer> chart;

    @FXML
    private void switchToPreferences() throws IOException {
        App.setRoot("Preferences");
    }

    @FXML
    private void switchToMain() throws IOException {
        App.setRoot("Main");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // XYChart.Series data = new XYChart.Series<>();
        // provide data
        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Funds In");
        series1.getData().add(new XYChart.Data("Jan", 2000));
        series1.getData().add(new XYChart.Data("Feb", 100));
        series1.getData().add(new XYChart.Data("Mar", 1000));

        XYChart.Series series2 = new XYChart.Series();
        series2.setName("Funds Out");
        series2.getData().add(new XYChart.Data("Jan", 450));
        series2.getData().add(new XYChart.Data("Feb", 95));
        series2.getData().add(new XYChart.Data("Mar", 748));

        chart.getData().addAll(series1, series2);
    }

}
