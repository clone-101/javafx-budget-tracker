package backend;

import javafx.scene.chart.XYChart;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class Charts {
	// barChart
	public static void getBarChart() {
		XYChart.Series fundsIn = new XYChart.Series();
		fundsIn.setName("Funds In");

		XYChart.Series fundsOut = new XYChart.Series();
		fundsOut.setName("Funds Out");

		fundsIn.getData().add(new XYChart.Data("Jan", 2000));

	}
}
