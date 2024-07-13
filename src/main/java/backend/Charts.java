// this is objectively bad code :(

package backend;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

public class Charts {

	private static final boolean EXPENSE = true; // for Category methods
	private static final int NUM_MONTHS = 12;

	public static XYChart.Series<String, Double>[] getBarSeries() {
		@SuppressWarnings("unchecked")
		XYChart.Series<String, Double>[] series = new XYChart.Series[2];

		XYChart.Series<String, Double> fundsIn = new XYChart.Series<String, Double>();
		XYChart.Series<String, Double> fundsOut = new XYChart.Series<String, Double>();

		// format for yAxis labels
		SimpleDateFormat f = new SimpleDateFormat("MMM");
		ArrayList<Date> dates = getBarDates();

		// series characteristics
		fundsIn.setName("Funds In");
		fundsOut.setName("Funds Out");

		// gets funds from backend.Transaction.transactions
		ArrayList<Double> fundsInValues = new ArrayList<Double>(), fundsOutValues = new ArrayList<Double>();
		getFunds(fundsInValues, fundsOutValues);

		// assigns values to fundsIn and fundsOut series
		for (int i = 0; i < dates.size() - 1; i++) {
			String date = f.format(dates.get(i)).toString();
			fundsIn.getData().add(new XYChart.Data<String, Double>(date, fundsInValues.get(i)));
			fundsOut.getData().add(new XYChart.Data<String, Double>(date, fundsOutValues.get(i)));
		}

		// return series' as array
		series[0] = fundsIn;
		series[1] = fundsOut;

		return series;
	} // getBarSeries()

	// gets ArrayList of dates for barChart
	private static ArrayList<Date> getBarDates() {

		// calendar arithmetic to get the first day of the last 12 months
		Calendar calendar = Calendar.getInstance();
		ArrayList<Date> months = new ArrayList<>();
		months.add(new Date());

		// set date to midnight on the first of the current month;
		calendar = getFirstOfMonth();

		for (int i = 0; i < NUM_MONTHS; i++) {
			months.add(0, calendar.getTime());
			calendar.add(Calendar.MONTH, -1);
		}

		return months;

	}

	// gets funds by month for barChart
	private static void getFunds(ArrayList<Double> fundsInValues, ArrayList<Double> fundsOutValues) {
		ArrayList<Date> dates = getBarDates();

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
			fundsInValues.add(Transaction.getIncome(start, end));
			fundsOutValues.add(Transaction.getExpenses(start, end));
		}
	}

	// ******************pie chart***************************
	public static ObservableList<PieChart.Data> getPieData() {
		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
		Date start = getFirstOfMonth().getTime(), end = new Date();
		ArrayList<Category> categories = Category.getCategories(EXPENSE);

		for (Category c : categories) {
			double funds = Transaction.getExpensesByCategory(start, end, c);
			if (funds > 0)
				pieChartData.add(new PieChart.Data(c.toString(), funds));
		}

		return pieChartData;
	}

	public static Calendar getFirstOfMonth() {
		Calendar calendar = Calendar.getInstance();
		// set date to midnight on the first of the current month;
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar;
	}

}
