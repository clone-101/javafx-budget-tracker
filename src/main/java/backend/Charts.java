package backend;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javafx.scene.chart.XYChart;

@SuppressWarnings({ "rawtypes", "unchecked" })

public class Charts {
	// eventually will not be hardcoded
	private static int numMonths = 12;

	// barChart
	public static void getBarChart() {
		// format for yAxis labels
		SimpleDateFormat f = new SimpleDateFormat("MMM");

		XYChart.Series fundsIn = new XYChart.Series();
		fundsIn.setName("Funds In");

		XYChart.Series fundsOut = new XYChart.Series();
		fundsOut.setName("Funds Out");

		fundsIn.getData().add(new XYChart.Data("Jan", 2000));

	}

	public static ArrayList<Date> getDates() {

		// calendar arithmatic to get the first day of the last 12 months
		Calendar calendar = Calendar.getInstance();
		ArrayList<Date> months = new ArrayList<>();
		months.add(new Date());

		// set date to midnight on the first of the current month;
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		for (int i = 0; i < numMonths; i++) {
			months.add(0, calendar.getTime());
			calendar.add(Calendar.MONTH, -1);
		}

		return months;

	}
}
