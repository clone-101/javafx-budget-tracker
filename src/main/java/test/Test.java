package test;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Test {
	public static void main(String[] args) throws Exception {
		backend.Transaction.readFile("SIMPLII.csv", 1, 3, 2, 4, 0);
		// Category cat = new Category("food");

		SimpleDateFormat f = new SimpleDateFormat("MM/dd/yyyy");
		Date end = new Date();
		Date start = f.parse("06/02/2024");
		double expenses = backend.Transaction.getExpenses(start, end);
		System.out.println(expenses);
	}
}
