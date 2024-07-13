package test;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import backend.Transaction;

public class Test {
	public static void main(String[] args) throws Exception {
		initializeCSV();
		// Category cat = new Category("food");
		// backend.Transaction.printAll();

		SimpleDateFormat f = new SimpleDateFormat("MM/dd/yyyy");
		Date end = new Date();
		Date start = f.parse("06/02/2022");
		double expenses = backend.Transaction.getExpenses(start, end);
		System.out.println(expenses);

	}

	public static void initializeCSV() {
		String windowsPath = "src\\main\\resources\\application\\SIMPLII.csv";
		String path = "src/main/java/application/SIMPLII.csv";
		if (System.getProperty("os.name").indexOf("Windows") != -1) {
			path = windowsPath;
		}
		File file = new File(path);
		try {
			Transaction.readFile(file, Transaction.getCSVOrder());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
