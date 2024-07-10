// this is objectively bad code :(

package backend;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javafx.scene.chart.XYChart;

@SuppressWarnings({ "rawtypes", "unchecked" })

public class Charts {
	// eventually will not be hardcoded
	private static int NUM_MONTHS = 12;

	// unfortunate use of global variables :(
	private static ArrayList<Double> fundsInVals;
	private static ArrayList<Double> fundsOutVals;
	public static XYChart.Series fundsIn;
	public static XYChart.Series fundsOut;

	// public static XYChart.Series getFundsIn() {
	// return fundsIn;
	// }

	// public static XYChart.Series getFundsOut() {
	// return fundsOut;
	// }

	// ************bar chart***********************
	public static XYChart.Series[] getBarSeries() {
		fundsIn = new XYChart.Series();
		fundsOut = new XYChart.Series();

		// format for yAxis labels
		SimpleDateFormat f = new SimpleDateFormat("MMM");
		ArrayList<Date> dates = getDates();

		// series characteristics
		fundsIn.setName("Funds In");
		fundsOut.setName("Funds Out");

		// gets funds from backend.Transaction.transactions
		getFunds();

		// assigns values to fundsIn and fundsOut series
		for (int i = 0; i < dates.size() - 1; i++) {
			fundsIn.getData().add(new XYChart.Data(f.format(dates.get(i)).toString(), fundsInVals.get(i)));
			fundsOut.getData().add(new XYChart.Data(f.format(dates.get(i)).toString(), fundsOutVals.get(i)));
		}

		// returns both series in an array
		XYChart.Series[] series = { fundsIn, fundsOut };
		return series;
	} // getBarSeries()

	// gets ArrayList of dates for barChart
	public static ArrayList<Date> getDates() {

		// calendar arithmetic to get the first day of the last 12 months
		Calendar calendar = Calendar.getInstance();
		ArrayList<Date> months = new ArrayList<>();
		months.add(new Date());

		// set date to midnight on the first of the current month;
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		for (int i = 0; i < NUM_MONTHS; i++) {
			months.add(0, calendar.getTime());
			calendar.add(Calendar.MONTH, -1);
		}

		return months;

	}

	// gets funds by month for barChart
	public static void getFunds() {
		fundsInVals = new ArrayList<>();
		fundsOutVals = new ArrayList<>();
		ArrayList<Date> dates = getDates();

		// temporary calendar to fix overlap of first day
		Calendar temp = Calendar.getInstance();

		// gets total funds in by month
		for (int i = 0; i < dates.size() - 1; i++) {

			// fix for first day overlap
			temp.setTime(dates.get(i + 1));
			temp.set(Calendar.MILLISECOND, -1);

			// set start/end dates
			Date start = dates.get(i), end = temp.getTime();

			// gets total fundsIn/fundsOut for transactions
			fundsInVals.add(backend.Transaction.getIncome(start, end));
			fundsOutVals.add(backend.Transaction.getExpenses(start, end));
		}
	}

	// ******************pie chart***************************
}
